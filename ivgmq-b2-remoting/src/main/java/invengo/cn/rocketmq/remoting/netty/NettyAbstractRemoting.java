package invengo.cn.rocketmq.remoting.netty;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import invengo.cn.rocketmq.remoting.InvokeCallback;
import invengo.cn.rocketmq.remoting.common.Pair;
import invengo.cn.rocketmq.remoting.common.RemotingHelper;
import invengo.cn.rocketmq.remoting.common.SemaphoreReleaseOnlyOnce;
import invengo.cn.rocketmq.remoting.exception.RemotingSendRequestException;
import invengo.cn.rocketmq.remoting.exception.RemotingTimeoutException;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import invengo.cn.rocketmq.remoting.protocol.RemotingSycResponseCode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class NettyAbstractRemoting {

	protected final Semaphore semaphoreOneway;
	
	protected final Semaphore semaphoreAsync;
	
	protected final ConcurrentMap<Integer, ResponseFuture> responseTable = new ConcurrentHashMap<Integer, ResponseFuture>(256);
	protected final ConcurrentMap<Integer/*requestCode*/, Pair<NettyRequestProcessor, ExecutorService> > processorTable = 
			new ConcurrentHashMap<Integer, Pair<NettyRequestProcessor, ExecutorService>>(64);
	
	public NettyAbstractRemoting(final int permitsOneway,final int permitsAsync) {
		this.semaphoreOneway = new Semaphore(permitsOneway,true);
		this.semaphoreAsync = new Semaphore(permitsAsync, true);
	}
	
	protected RemotingCommand invokeSyncImpl(final Channel channel,final RemotingCommand request,final long timeoutMillis) 
			throws InterruptedException, RemotingTimeoutException, RemotingSendRequestException {
		final int opaque = request.getOpaque();
		try {
			final ResponseFuture responseFuture = new ResponseFuture(opaque, timeoutMillis, null, null);
			this.responseTable.put(opaque, responseFuture);
			final SocketAddress addr = channel.remoteAddress();
			channel.writeAndFlush(request).addListener(new ChannelFutureListener(){

				public void operationComplete(ChannelFuture f) throws Exception {
					if (f.isSuccess()) {
						responseFuture.setSendRequestOK(true);
						return;
					}else {
						responseFuture.setSendRequestOK(false);
					}
					responseTable.remove(opaque);
					responseFuture.setCause(f.cause());
					responseFuture.putResponse(null);
				}});
			
			RemotingCommand remotingCommand = responseFuture.waitResponse(timeoutMillis*30);
			if (remotingCommand == null) {
				if (responseFuture.isSendRequestOK()) {
					throw new RemotingTimeoutException(RemotingHelper.parseSocketAddressAddr(addr), timeoutMillis);
				}else {
					throw new RemotingSendRequestException(RemotingHelper.parseSocketAddressAddr(addr), responseFuture.getCause());
				}
			}
			return remotingCommand;
			
		} finally {
			responseTable.remove(opaque);
		}
	}
	
	protected void invokeAsyncImpl(final Channel channel,final RemotingCommand request,final long timeoutMillis,
			InvokeCallback invokeCallback) throws InterruptedException, RemotingTimeoutException {
		final int opaque = request.getOpaque();
		final SocketAddress addr = channel.remoteAddress();
		boolean acquire = this.semaphoreAsync.tryAcquire(timeoutMillis, TimeUnit.MILLISECONDS);
		if (!acquire) {
			// 尝试获取流量锁超时
			throw new RemotingTimeoutException("尝试获取流量锁超时");
		}
		SemaphoreReleaseOnlyOnce once = new SemaphoreReleaseOnlyOnce(semaphoreAsync);
		final ResponseFuture responseFuture = new ResponseFuture(opaque, timeoutMillis, invokeCallback, once);
		this.responseTable.put(opaque, responseFuture);
		try {
			channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
				
				public void operationComplete(ChannelFuture future) throws Exception {
					// TODO Auto-generated method stub
					if (future.isSuccess()) {
						responseFuture.setSendRequestOK(true);
						return;
					}
					responseFuture.setSendRequestOK(false);
					responseTable.remove(opaque);
					responseFuture.release();
					responseFuture.executeInvokeCallback();
					
				}
			});
		} finally {
			// TODO: handle finally clause
		}
	}
	
	protected void invokeOnewayImpl(final Channel channel,final RemotingCommand request,final long timeoutMillis) throws InterruptedException, RemotingTimeoutException {
		request.makeOnewayType();
		boolean acquire = this.semaphoreAsync.tryAcquire(timeoutMillis, TimeUnit.MILLISECONDS);
		if (!acquire) {
			// 尝试获取流量锁超时
			throw new RemotingTimeoutException("尝试获取流量锁超时");
		}
		final SemaphoreReleaseOnlyOnce once = new SemaphoreReleaseOnlyOnce(semaphoreAsync);
		try {
			channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
				
				public void operationComplete(ChannelFuture future) throws Exception {
					once.release();
				}
			});
		} catch (Exception e) {
			once.release();
		}
		
	}
	
	protected void processMessageReceived(ChannelHandlerContext ctx, RemotingCommand msg) {
		if (msg == null) {
			return;
		}
		final RemotingCommand command = msg;
		switch (command.getType()) {
		case REQUEST_COMMAND:
			processRequestCommand(ctx, command);
			break;
		case RESPONSE_COMMAND:
			processResponseCommand(ctx, command);
			break;

		default:
			break;
		}
		
	}
	
	private void processRequestCommand(final ChannelHandlerContext ctx, final RemotingCommand command){
		final Pair<NettyRequestProcessor, ExecutorService> matched = this.processorTable.get(command.getCode());
		int opaque = command.getOpaque();
		if (matched == null) {
			String remark = "request type "+command.getCode()+" not supported";
			RemotingCommand response = RemotingCommand.createResponseCommand(RemotingSycResponseCode.REQUEST_CODE_NOT_SUPPORTED,remark);
			response.setOpaque(opaque);
			ctx.writeAndFlush(response);
			return;
		}
		
		Runnable runnable = new Runnable() {
			
			public void run() {
				final RemotingCommand response = matched.getObj1().processRequest(ctx, command);
				if (!command.isOnewayType()) {
					response.makeResponseType();
					ctx.writeAndFlush(response);
				}
				
				//ctx.writeAndFlush(command);
			}
		};
		matched.getObj2().submit(runnable);
		
	}
	
	private void processResponseCommand(ChannelHandlerContext ctx, RemotingCommand command){
		ResponseFuture responseFuture = this.responseTable.get(command.getOpaque());
		if (responseFuture == null) {
			return;
		}
		responseTable.remove(command.getOpaque());
		if (responseFuture.getInvokeCallback() != null) {
			
			responseFuture.setResponseCommand(command);
			responseFuture.release();
			responseFuture.executeInvokeCallback();
		}else {
			responseFuture.putResponse(command);
		}
	}
	
}
