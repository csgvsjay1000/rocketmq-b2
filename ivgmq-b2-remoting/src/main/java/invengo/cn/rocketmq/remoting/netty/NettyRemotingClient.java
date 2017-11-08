package invengo.cn.rocketmq.remoting.netty;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import invengo.cn.rocketmq.remoting.InvokeCallback;
import invengo.cn.rocketmq.remoting.RPCHook;
import invengo.cn.rocketmq.remoting.RemotingClient;
import invengo.cn.rocketmq.remoting.common.RemotingHelper;
import invengo.cn.rocketmq.remoting.exception.RemotingSendRequestException;
import invengo.cn.rocketmq.remoting.exception.RemotingTimeoutException;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

public class NettyRemotingClient extends NettyAbstractRemoting implements RemotingClient{


	private final Bootstrap bootstrap = new Bootstrap();
	private final EventLoopGroup eventLoopGroupWorker;
	private final ConcurrentMap<String/*addr*/, ChannelWrapper> channelTables = 
			new ConcurrentHashMap<String, NettyRemotingClient.ChannelWrapper>();
	private final NettyClientConfig nettyClientConfig;
	
	private DefaultEventExecutorGroup defaultEventExecutorGroup;
	private final AtomicReference<List<String>> namesrvAddrList = new AtomicReference<List<String>>();
	
	public NettyRemotingClient(final NettyClientConfig nettyClientConfig) {
		super(nettyClientConfig.getClientOnewaySemaphoreValue(), nettyClientConfig.getClientAsyncSemaphoreValue());
		this.nettyClientConfig = nettyClientConfig;
		this.eventLoopGroupWorker = new NioEventLoopGroup(1, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);
			public Thread newThread(Runnable r) {
				return new Thread(r, String.format("NettyClientSelector_%d_%d", 1,threadIndex.incrementAndGet()));
			}
		});
		
	}
	
	public void start() {
		final int clientWrokerThreads = 8;
		this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(clientWrokerThreads, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);
			public Thread newThread(Runnable r) {
				return new Thread(r, String.format("NettyClientWorkerThread_%d_%d", clientWrokerThreads,threadIndex.incrementAndGet()));
			}
		});
		
		this.bootstrap.group(this.eventLoopGroupWorker).channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.SO_KEEPALIVE, false)
			.option(ChannelOption.SO_SNDBUF, nettyClientConfig.getClientSocketSndBufSize())
			.option(ChannelOption.SO_RCVBUF, nettyClientConfig.getClientSocketRcvBufSize())
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, nettyClientConfig.getConnectTimeoutMillis())
			.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(defaultEventExecutorGroup, 
							new NettyEncoder(),
							new NettyDecoder(),
							new NettyConnectManagerHandler(),
							new NettyClientHandler());
					
				}
			});
		
	}

	public void shutdown() {
		// TODO Auto-generated method stub
		if (this.eventLoopGroupWorker != null) {
			this.eventLoopGroupWorker.shutdownGracefully();
		}
		if (this.defaultEventExecutorGroup != null) {
			this.defaultEventExecutorGroup.shutdownGracefully();
		}
	}

	public void registerRPCHook(RPCHook rpcHook) {
		// TODO Auto-generated method stub
		
	}

	public void updateNameServerAddressList(List<String> addrs) {
		// TODO Auto-generated method stub
		
	}

	public List<String> getNameServerAddressList() {
		// TODO Auto-generated method stub
		return null;
	}

	public void registerProcessor(int requestCode, NettyRequestProcessor processor, ExecutorService executorService) {
		// TODO Auto-generated method stub
		
	}

	public void registerDefaultProcessor(NettyRequestProcessor processor, ExecutorService executorService) {
		// TODO Auto-generated method stub
		
	}

	public RemotingCommand invokeSync(final String addr, RemotingCommand request, long timeoutMillis) throws InterruptedException, RemotingTimeoutException, RemotingSendRequestException {
		Channel channel = this.getAndCreateChannel(addr);
		RemotingCommand response = this.invokeSyncImpl(channel, request, timeoutMillis);
		
		return response;
	}

	public void invokeAsync(final String addr, RemotingCommand request, long timeoutMillis, InvokeCallback callback) throws InterruptedException, RemotingTimeoutException, RemotingSendRequestException {
		Channel channel = this.getAndCreateChannel(addr);
		this.invokeAsyncImpl(channel, request, timeoutMillis, callback);
		
	}

	public void invokeOneway(final String addr, RemotingCommand request, long timeoutMillis) throws InterruptedException, RemotingTimeoutException {
		Channel channel = this.getAndCreateChannel(addr);
		this.invokeOnewayImpl(channel, request, timeoutMillis);
	}

	public boolean isChannelWriteable(String addr) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private Channel getAndCreateChannel(final String addr) throws InterruptedException{
		
		ChannelWrapper channelWrapper = this.channelTables.get(addr);
		if (channelWrapper != null) {
			return channelWrapper.getChannel();
		}
		
		return this.createChannel(addr);
	}
	
	private Channel createChannel(final String addr) throws InterruptedException{
		
		ChannelFuture channelFuture = this.bootstrap.connect(RemotingHelper.string2SocketAddress(addr));
		ChannelWrapper channelWrapper = new ChannelWrapper(channelFuture);
		this.channelTables.put(addr, channelWrapper);
		
		if(channelFuture.awaitUninterruptibly(nettyClientConfig.getConnectTimeoutMillis(), TimeUnit.MILLISECONDS)){
			// 连接请求不超时
			if (channelWrapper.isOK()) {
				return channelFuture.channel();
			}else {
			}
		}else {
			//请求超时
		}
		
		return null;
	}
	
	class NettyConnectManagerHandler extends ChannelDuplexHandler{
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
		}
		
	}
	
	class NettyClientHandler extends SimpleChannelInboundHandler<RemotingCommand>{

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand command) throws Exception {
			processMessageReceived(ctx, command);
		}
		
	}
	
	static class ChannelWrapper{
		private final ChannelFuture channelFuture;
		public ChannelWrapper(final ChannelFuture channelFuture) {
			this.channelFuture = channelFuture;
		}
		
		public boolean isOK() {
			return this.channelFuture.channel() != null && this.channelFuture.channel().isActive();
		}
		
		public boolean isWritable() {
			return this.channelFuture.channel().isWritable();
		}
		
		public Channel getChannel(){
			return this.channelFuture.channel();
		}
		
		public ChannelFuture getChannelFuture() {
			return this.channelFuture;
		}
	}

}
