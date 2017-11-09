package invengo.cn.rocketmq.broker;

import invengo.cn.rocketmq.common.BrokerConfig;
import invengo.cn.rocketmq.remoting.netty.NettyClientConfig;
import invengo.cn.rocketmq.remoting.netty.NettyServerConfig;

public class BrokerStartup {
	public static void main(String[] args) {
		main0(args);
	}
	
	public static void main0(String[] args) {
		BrokerController controller = createBrokerController(args);
		controller.initialize();
		controller.start();
	}
	
	private static BrokerController createBrokerController(String[] args){
		
		NettyServerConfig nettyServerConfig = new NettyServerConfig();
		nettyServerConfig.setListenPort(10198);
		BrokerConfig brokerConfig = new BrokerConfig();
		NettyClientConfig nettyClientConfig = new NettyClientConfig();
		BrokerController controller = new BrokerController(brokerConfig, nettyServerConfig, nettyClientConfig);
		
		return controller;
	}
}
