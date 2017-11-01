package invengo.cn.rocketmq.remoting;

import java.util.concurrent.ExecutorService;

import invengo.cn.rocketmq.remoting.common.Pair;
import invengo.cn.rocketmq.remoting.netty.NettyRequestProcessor;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.channel.Channel;

public interface RemotingServer extends RemotingService{

	void registerProcessor(final int requestCode,final NettyRequestProcessor processor,
			final ExecutorService executorService);
	
	void registerDefaultProcessor(final NettyRequestProcessor processor,
			final ExecutorService executorService);
	
	int localListenPort();
	
	Pair<NettyRequestProcessor, ExecutorService> getProcessorPair(final int requestCode);
	
	RemotingCommand invokeSync(final Channel channel,final RemotingCommand request,final long timeoutMillis);
	
	void invokeAsync(final Channel channel,final RemotingCommand request,final long timeoutMillis,InvokeCallback callback);

	void invokeOneway(final Channel channel,final RemotingCommand request,final long timeoutMillis);
	
}
