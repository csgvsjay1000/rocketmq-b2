package invengo.cn.rocketmq.broker;

import invengo.cn.rocketmq.broker.out.BrokerOutAPI;
import invengo.cn.rocketmq.broker.topic.TopicConfigManager;
import invengo.cn.rocketmq.common.BrokerConfig;
import invengo.cn.rocketmq.common.namesrv.RegisterBrokerResult;
import invengo.cn.rocketmq.common.protocol.body.TopicConfigSerializeWrapper;
import invengo.cn.rocketmq.remoting.netty.NettyClientConfig;
import invengo.cn.rocketmq.remoting.netty.NettyServerConfig;

public class BrokerController {
	
	private final BrokerOutAPI brokerOutAPI;
	private final TopicConfigManager topicConfigManager;
	private final BrokerConfig brokerConfig;
	private final NettyServerConfig nettyServerConfig;
	public BrokerController(
			final BrokerConfig brokerConfig,
			NettyServerConfig nettyServerConfig,
			final NettyClientConfig nettyClientConfig) {
		this.brokerConfig = brokerConfig;
		this.nettyServerConfig = nettyServerConfig;
		this.brokerOutAPI = new BrokerOutAPI(nettyClientConfig);
		this.topicConfigManager = new TopicConfigManager(this);
	}
	
	public boolean initialize() {
		
		return true;
	}
	
	public boolean start() {
		
		if (this.brokerOutAPI != null) {
            this.brokerOutAPI.start();
        }
		
		this.registerBrokerAll(true, false);  // checkOrderConfig  oneway
		return true;
	}
	
	public void registerBrokerAll(final boolean checkOrderConfig,final boolean oneway) {
		TopicConfigSerializeWrapper topicConfigWrapper = this.getTopicConfigManager().buildTopicConfigSerializeWrapper();
		
		String haServerAddr = "";
		RegisterBrokerResult result = this.brokerOutAPI.registerBrokerAll(
				this.brokerConfig.getBrokerClusterName(), 
				this.getBrokerAddr(), 
				this.brokerConfig.getBrokerName(),
				this.brokerConfig.getBrokerId(),
				haServerAddr, topicConfigWrapper, oneway, 
				this.brokerConfig.getRegisterBrokerTimeoutMills());
	}
	
	private String getBrokerAddr(){
		return this.brokerConfig.getBrokerIP1()+":"+this.nettyServerConfig.getListenPort();
	}

	public BrokerConfig getBrokerConfig() {
		return brokerConfig;
	}

	public BrokerOutAPI getBrokerOutAPI() {
		return brokerOutAPI;
	}

	public TopicConfigManager getTopicConfigManager() {
		return topicConfigManager;
	}
	
}
