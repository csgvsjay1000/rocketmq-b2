package invengo.cn.rocketmq.namesrv;

import invengo.cn.rocketmq.common.namesrv.NamesrvConfig;
import invengo.cn.rocketmq.namesrv.processor.DefaultRequestProcessor;
import invengo.cn.rocketmq.namesrv.routeinfo.RouteInfoManager;
import invengo.cn.rocketmq.remoting.RemotingServer;
import invengo.cn.rocketmq.remoting.netty.NettyRemotingServer;
import invengo.cn.rocketmq.remoting.netty.NettyServerConfig;

public class NamesrvController {

	private final NamesrvConfig namesrvConfig;
	
	private final NettyServerConfig nettyServerConfig;
	private RemotingServer remotingServer;
	private final RouteInfoManager routeInfoManager;
	
	public NamesrvController(final NamesrvConfig namesrvConfig,final NettyServerConfig nettyServerConfig) {
		this.namesrvConfig = namesrvConfig;
		this.nettyServerConfig = nettyServerConfig;
		this.routeInfoManager = new RouteInfoManager();
	}
	
	public boolean initialize() {
		this.remotingServer = new NettyRemotingServer(nettyServerConfig);
		this.registerProcessor();
		
		return true;
	}
	
	public boolean start() {
		this.remotingServer.start();
		
		return true;
	}
	
	public boolean shutdown() {
		this.remotingServer.shutdown();
		return true;
	}
	
	private void registerProcessor(){
		this.remotingServer.registerDefaultProcessor(new DefaultRequestProcessor(this), null);
	}

	public RouteInfoManager getRouteInfoManager() {
		return routeInfoManager;
	}
	
}
