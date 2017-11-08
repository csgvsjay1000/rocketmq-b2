package invengo.cn.rocketmq.broker;

import invengo.cn.rocketmq.broker.out.BrokerOutAPI;
import invengo.cn.rocketmq.remoting.netty.NettyClientConfig;

public class BrokerController {
	
	private final BrokerOutAPI brokerOutAPI;

	public BrokerController(final NettyClientConfig nettyClientConfig) {
		this.brokerOutAPI = new BrokerOutAPI(nettyClientConfig);
	}
	
	public boolean initialize() {
		
		return true;
	}
	
	public boolean start() {
		
		//this.brokerOutAPI.registerBrokerAll(namesrvAddr, clusterName, brokerAddr, brokerName, brokerId, haServerAddr, topicConfigWrapper, oneway, timeoutMills)
		this.registerBrokerAll(true, false);  // checkOrderConfig  oneway
		return true;
	}
	
	public void registerBrokerAll(final boolean checkOrderConfig,final boolean oneway) {
		
	}
	
}
