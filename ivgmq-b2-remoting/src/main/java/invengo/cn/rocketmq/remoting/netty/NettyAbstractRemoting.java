package invengo.cn.rocketmq.remoting.netty;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

import invengo.cn.rocketmq.common.log.Logger;
import invengo.cn.rocketmq.common.log.LoggerFactory;
import invengo.cn.rocketmq.remoting.common.RemotingHelper;
import invengo.cn.rocketmq.remoting.exception.RemotingSendRequestException;
import invengo.cn.rocketmq.remoting.exception.RemotingTimeoutException;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class NettyAbstractRemoting {

	private static Logger pLogger = LoggerFactory.getLogger(NettyAbstractRemoting.class);
	
	protected final Semaphore semaphoreOneway;
	
	protected final Semaphore semaphoreAsync;
	
	protected final ConcurrentMap<Integer, ResponseFuture> responseTable = new ConcurrentHashMap<Integer, ResponseFuture>(256);
	
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
					pLogger.getLogger().warn("send a request command to a channel <"+addr+"> failed.");
				}});
			
			RemotingCommand remotingCommand = responseFuture.waitResponse(timeoutMillis);
			/*if (remotingCommand == null) {
				if (responseFuture.isSendRequestOK()) {
					throw new RemotingTimeoutException(RemotingHelper.parseSocketAddressAddr(addr), timeoutMillis);
				}else {
					throw new RemotingSendRequestException(RemotingHelper.parseSocketAddressAddr(addr), responseFuture.getCause());
				}
			}*/
			return remotingCommand;
			
		} finally {
			responseTable.remove(opaque);
		}
	}
	
}
