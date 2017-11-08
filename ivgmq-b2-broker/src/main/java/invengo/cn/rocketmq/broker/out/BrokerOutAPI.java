package invengo.cn.rocketmq.broker.out;

import invengo.cn.rocketmq.common.namesrv.RegisterBrokerResult;
import invengo.cn.rocketmq.common.protocol.body.TopicConfigSerializeWrapper;
import invengo.cn.rocketmq.remoting.RemotingClient;
import invengo.cn.rocketmq.remoting.netty.NettyClientConfig;
import invengo.cn.rocketmq.remoting.netty.NettyRemotingClient;

public class BrokerOutAPI {
	
	private final RemotingClient remotingClient;
	
	public BrokerOutAPI(final NettyClientConfig nettyClientConfig) {
		this.remotingClient = new NettyRemotingClient(nettyClientConfig);
	}
	
	public void start() {
		this.remotingClient.start();
	}
	
	public void shutdown() {
		this.remotingClient.shutdown();
	}
	
	public RegisterBrokerResult registerBrokerAll(
			final String namesrvAddr,
			final String clusterName,
			final String brokerAddr,
			final String brokerName,
			final long brokerId,
			final String haServerAddr,
			final TopicConfigSerializeWrapper topicConfigWrapper,
			final boolean oneway,
			final int timeoutMills
			){
		
		return registerBroker(namesrvAddr, clusterName, brokerAddr, 
				brokerName, brokerId, haServerAddr, topicConfigWrapper, oneway, timeoutMills);
	}
	
	private RegisterBrokerResult registerBroker(
			final String namesrvAddr,
			final String clusterName,
			final String brokerAddr,
			final String brokerName,
			final long brokerId,
			final String haServerAddr,
			final TopicConfigSerializeWrapper topicConfigWrapper,
			final boolean oneway,
			final int timeoutMills
			){
		
		return null;
	}
	

}
