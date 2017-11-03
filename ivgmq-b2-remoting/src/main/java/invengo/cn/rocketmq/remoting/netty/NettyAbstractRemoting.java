package invengo.cn.rocketmq.remoting.netty;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

import invengo.cn.rocketmq.common.log.Logger;
import invengo.cn.rocketmq.common.log.LoggerFactory;
import invengo.cn.rocketmq.remoting.common.Pair;
import invengo.cn.rocketmq.remoting.common.RemotingHelper;
import invengo.cn.rocketmq.remoting.exception.RemotingSendRequestException;
import invengo.cn.rocketmq.remoting.exception.RemotingTimeoutException;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import invengo.cn.rocketmq.remoting.protocol.RemotingSycResponseCode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class NettyAbstractRemoting {

	private static Logger pLogger = LoggerFactory.getLogger(NettyAbstractRemoting.class);
	
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
						pLogger.getLogger().warn("send a request command to a channel <"+addr+"> success");
						responseFuture.setSendRequestOK(true);
						return;
					}else {
						responseFuture.setSendRequestOK(false);
					}
					responseTable.remove(opaque);
					responseFuture.setCause(f.cause());
					responseFuture.putResponse(null);
					pLogger.getLogger().warn("send a request command to a channel <"+addr+"> failed.");
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
	
	private void processRequestCommand(ChannelHandlerContext ctx, RemotingCommand command){
		final Pair<NettyRequestProcessor, ExecutorService> matched = this.processorTable.get(command.getCode());
		int opaque = command.getOpaque();
		if (matched == null) {
			RemotingCommand response = RemotingCommand.createResponseCommand(RemotingSycResponseCode.REQUEST_CODE_NOT_SUPPORTED);
			response.setOpaque(opaque);
			ctx.writeAndFlush(response);
			return;
		}
	}
	
	private void processResponseCommand(ChannelHandlerContext ctx, RemotingCommand command){
		ResponseFuture responseFuture = this.responseTable.get(command.getOpaque());
		if (responseFuture == null) {
			pLogger.getLogger().warn("receive response, but not matched any request, " + RemotingHelper.parseChannelRemoteAddr(ctx.channel()));
			pLogger.getLogger().warn(command.toString());
			return;
		}
		responseTable.remove(command.getOpaque());
		responseFuture.putResponse(command);
	}
	
}
