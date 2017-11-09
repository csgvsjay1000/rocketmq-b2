package invengo.cn.rocketmq.broker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import invengo.cn.rocketmq.broker.latency.BrokerFixedThreadPoolExecutor;
import invengo.cn.rocketmq.broker.out.BrokerOutAPI;
import invengo.cn.rocketmq.broker.processor.SendMessageProcessor;
import invengo.cn.rocketmq.broker.topic.TopicConfigManager;
import invengo.cn.rocketmq.common.BrokerConfig;
import invengo.cn.rocketmq.common.namesrv.RegisterBrokerResult;
import invengo.cn.rocketmq.common.protocol.RequestCode;
import invengo.cn.rocketmq.common.protocol.body.TopicConfigSerializeWrapper;
import invengo.cn.rocketmq.remoting.RemotingServer;
import invengo.cn.rocketmq.remoting.netty.NettyClientConfig;
import invengo.cn.rocketmq.remoting.netty.NettyRemotingServer;
import invengo.cn.rocketmq.remoting.netty.NettyServerConfig;

public class BrokerController {
	
	private final BrokerOutAPI brokerOutAPI;
	private final TopicConfigManager topicConfigManager;
	private final BrokerConfig brokerConfig;
	private final NettyServerConfig nettyServerConfig;
	
	private RemotingServer remotingServer;
	private ExecutorService sendMessageExecutor;
	
	private BlockingQueue<Runnable> sendThreadPoolQueue;
	
	public BrokerController(
			final BrokerConfig brokerConfig,
			NettyServerConfig nettyServerConfig,
			final NettyClientConfig nettyClientConfig) {
		this.brokerConfig = brokerConfig;
		this.nettyServerConfig = nettyServerConfig;
		this.brokerOutAPI = new BrokerOutAPI(nettyClientConfig);
		this.topicConfigManager = new TopicConfigManager(this);
		
		this.sendThreadPoolQueue = new LinkedBlockingQueue<Runnable>(brokerConfig.getSendThreadPoolQueueCapacity());
		
	}
	
	public boolean initialize() {
		
		this.remotingServer = new NettyRemotingServer(nettyServerConfig);
		
		this.sendMessageExecutor = new BrokerFixedThreadPoolExecutor(
				this.brokerConfig.getSendMessageThreadPoolNums(), 
				this.brokerConfig.getSendMessageThreadPoolNums(), 
				1000*60, 
				TimeUnit.MILLISECONDS, this.sendThreadPoolQueue);
		
		this.registerProcessor();
		return true;
	}
	
	public void registerProcessor() {
		
		SendMessageProcessor sendMessageProcessor = new SendMessageProcessor();
		
		this.remotingServer.registerProcessor(RequestCode.SEND_MESSAGE, sendMessageProcessor, this.sendMessageExecutor);
		
	}
	
	public boolean start() {
		
		if (this.brokerOutAPI != null) {
            this.brokerOutAPI.start();
        }
		this.remotingServer.start();
		
		this.registerBrokerAll(true, false);  // checkOrderConfig  oneway
		return true;
	}
	
	public void registerBrokerAll(final boolean checkOrderConfig,final boolean oneway) {
		TopicConfigSerializeWrapper topicConfigWrapper = this.getTopicConfigManager().buildTopicConfigSerializeWrapper();
		
		String haServerAddr = "haServerAddr";
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
