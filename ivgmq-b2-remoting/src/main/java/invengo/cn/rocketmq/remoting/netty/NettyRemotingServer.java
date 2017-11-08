package invengo.cn.rocketmq.remoting.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omg.CORBA.PRIVATE_MEMBER;

import invengo.cn.rocketmq.remoting.ChannelEventListener;
import invengo.cn.rocketmq.remoting.InvokeCallback;
import invengo.cn.rocketmq.remoting.RPCHook;
import invengo.cn.rocketmq.remoting.RemotingServer;
import invengo.cn.rocketmq.remoting.common.Pair;
import invengo.cn.rocketmq.remoting.common.RemotingHelper;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

public class NettyRemotingServer extends NettyAbstractRemoting implements RemotingServer{
	
	private static Logger logger = LogManager.getLogger(NettyRemotingServer.class);
	
	private final NettyServerConfig nettyServerConfig;
	private final ServerBootstrap serverBootstrap;
	private final NioEventLoopGroup eventLoopGroupSelector;
	private final NioEventLoopGroup eventLoopGroupBoss;
	
	private final ChannelEventListener channelEventListener;
	private final ExecutorService publicExecutor;
	
	private DefaultEventExecutorGroup defaultEventExecutorGroup;
	
	public NettyRemotingServer(final NettyServerConfig nettyServerConfig) {
		this(nettyServerConfig, null);
	}
	
	public NettyRemotingServer(final NettyServerConfig nettyServerConfig,final ChannelEventListener channelEventListener) {
		super(nettyServerConfig.getServerOnewaySemaphoreValue(), nettyServerConfig.getServerAsyncSemaphoreValue());
		this.channelEventListener = channelEventListener;
		this.nettyServerConfig = nettyServerConfig;
		
		this.serverBootstrap = new ServerBootstrap();
		final int bossThreadNums = 1;
		this.eventLoopGroupBoss = new NioEventLoopGroup(bossThreadNums, new ThreadFactory() {
			AtomicInteger threadIndex = new AtomicInteger(0);
			public Thread newThread(Runnable r) {
				return new Thread(r, String.format("NettyServerBoss_%d_%d", bossThreadNums,threadIndex.incrementAndGet()));
			}
		});
		final int selectorThreadNums = 8;
		this.eventLoopGroupSelector = new NioEventLoopGroup(selectorThreadNums, new ThreadFactory() {
			AtomicInteger threadIndex = new AtomicInteger(0);
			public Thread newThread(Runnable r) {
				return new Thread(r, String.format("NettyServerSelector_%d_%d", selectorThreadNums,threadIndex.incrementAndGet()));
			}
		});
		final int publicThreadNums = 4;
		this.publicExecutor = Executors.newFixedThreadPool(publicThreadNums, new ThreadFactory() {
			AtomicInteger threadIndex = new AtomicInteger(0);
			public Thread newThread(Runnable r) {
				return new Thread(r, String.format("NettyServerPublicExecutor_%d_%d", selectorThreadNums,threadIndex.incrementAndGet()));
			}
		});
		
	}


	public void start() {
		final int serverWorkThreads = 8;
		this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(serverWorkThreads, new ThreadFactory() {
			AtomicInteger threadIndex = new AtomicInteger(0);
			public Thread newThread(Runnable r) {
				return new Thread(r, String.format("NettyServerWorker_%d_%d", serverWorkThreads,threadIndex.incrementAndGet()));
			}
		});
		this.serverBootstrap.group(eventLoopGroupBoss, eventLoopGroupSelector).channel(NioServerSocketChannel.class)
			.localAddress(nettyServerConfig.getListenPort())
			.option(ChannelOption.SO_BACKLOG, 1024)
			.option(ChannelOption.SO_REUSEADDR, true)
			.childOption(ChannelOption.TCP_NODELAY, true)
			.childOption(ChannelOption.SO_SNDBUF, nettyServerConfig.getServerSocketSndBufSize())
			.childOption(ChannelOption.SO_RCVBUF, nettyServerConfig.getServerSocketRcvBufSize())
			.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(
							defaultEventExecutorGroup,
							new NettyEncoder(),
							new NettyDecoder(),
							new NettyConnectManagerHandler(),
							new NettyServerHandler());
					
					
					
				}
			});
		try {
			ChannelFuture channelFuture = this.serverBootstrap.bind().sync();
			InetSocketAddress addr = (InetSocketAddress) channelFuture.channel().localAddress();
			logger.info("server bind success"+channelFuture.channel().localAddress());
            //this.port = addr.getPort();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void shutdown() {
		
		this.eventLoopGroupBoss.shutdownGracefully();
		this.eventLoopGroupSelector.shutdownGracefully();
		
		if (this.defaultEventExecutorGroup != null) {
			this.defaultEventExecutorGroup.shutdownGracefully();
		}
		if (this.publicExecutor != null) {
			this.publicExecutor.shutdown();
		}
		
	}

	public void registerRPCHook(RPCHook rpcHook) {
		// TODO Auto-generated method stub
		
	}

	public void registerProcessor(int requestCode, NettyRequestProcessor processor, ExecutorService executorService) {
		ExecutorService executorThis = executorService;
		if (executorThis == null) {
			executorThis = this.publicExecutor;
		}
		Pair<NettyRequestProcessor, ExecutorService> processPair = 
				new Pair<NettyRequestProcessor, ExecutorService>(processor, executorThis);
		this.processorTable.put(requestCode, processPair);
	}

	public void registerDefaultProcessor(NettyRequestProcessor processor, ExecutorService executorService) {
		ExecutorService executorThis = executorService;
		if (executorThis == null) {
			executorThis = this.publicExecutor;
		}
		this.defaultRequestProcessor = new Pair<NettyRequestProcessor, ExecutorService>(processor, executorThis);
	}

	public int localListenPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Pair<NettyRequestProcessor, ExecutorService> getProcessorPair(int requestCode) {
		// TODO Auto-generated method stub
		return null;
	}

	public RemotingCommand invokeSync(Channel channel, RemotingCommand request, long timeoutMillis) {
		// TODO Auto-generated method stub
		return null;
	}

	public void invokeAsync(Channel channel, RemotingCommand request, long timeoutMillis, InvokeCallback callback) {
		// TODO Auto-generated method stub
		
	}

	public void invokeOneway(Channel channel, RemotingCommand request, long timeoutMillis) {
		// TODO Auto-generated method stub
		
	}
	
	class NettyConnectManagerHandler extends ChannelDuplexHandler{
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
		}
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
			logger.info("NETTY SERVER PIPELINE: channelActive, the channel[{}]", remoteAddress);
			super.channelActive(ctx);
		}
		
	}

	class NettyServerHandler extends SimpleChannelInboundHandler<RemotingCommand>{

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand command) throws Exception {
			
			//logger.getLogger().info(new String(command.getBody(),"utf-8"));
			processMessageReceived(ctx, command);
		}
		
	}
	
}
