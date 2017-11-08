package invengo.cn.rocketmq.namesrv;

import invengo.cn.rocketmq.common.namesrv.NamesrvConfig;
import invengo.cn.rocketmq.remoting.netty.NettyServerConfig;

public class NamesrvStartup {

	public static void main(String[] args) {
		main0(args);
	}
	
	public static void main0(String[] args) {
		NamesrvController controller = createNamesrvController(args);
		controller.initialize();
		controller.start();
	}
	
	private static NamesrvController createNamesrvController(String[] args){
		NamesrvConfig namesrvConfig = new NamesrvConfig();
		NettyServerConfig nettyServerConfig = new NettyServerConfig();
		nettyServerConfig.setListenPort(9876);
		NamesrvController controller = new NamesrvController(namesrvConfig, nettyServerConfig);
		
		return controller;
	}
	
}
