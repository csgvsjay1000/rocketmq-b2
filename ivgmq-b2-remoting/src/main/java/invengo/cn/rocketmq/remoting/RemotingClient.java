package invengo.cn.rocketmq.remoting;

import java.util.List;
import java.util.concurrent.ExecutorService;

import invengo.cn.rocketmq.remoting.exception.RemotingSendRequestException;
import invengo.cn.rocketmq.remoting.exception.RemotingTimeoutException;
import invengo.cn.rocketmq.remoting.netty.NettyRequestProcessor;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.channel.Channel;

public interface RemotingClient extends RemotingService{

	void updateNameServerAddressList(final List<String> addrs);
	
	List<String> getNameServerAddressList();
	
	void registerProcessor(final int requestCode,final NettyRequestProcessor processor,
			final ExecutorService executorService);
	
	void registerDefaultProcessor(final NettyRequestProcessor processor,
			final ExecutorService executorService);
	
	RemotingCommand invokeSync(final String addr,final RemotingCommand request,final long timeoutMillis) 
			throws InterruptedException,RemotingTimeoutException, RemotingSendRequestException;
	
	void invokeAsync(final String addr,final RemotingCommand request,final long timeoutMillis,InvokeCallback callback) 
			throws InterruptedException, RemotingTimeoutException, RemotingSendRequestException;

	void invokeOneway(final String addr,final RemotingCommand request,final long timeoutMillis) throws InterruptedException, RemotingTimeoutException;
	
	boolean isChannelWriteable(final String addr);
	
}
