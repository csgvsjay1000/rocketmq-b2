package invengo.cn.rocketmq.broker.out;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import invengo.cn.rocketmq.common.namesrv.RegisterBrokerResult;
import invengo.cn.rocketmq.common.protocol.RequestCode;
import invengo.cn.rocketmq.common.protocol.body.TopicConfigSerializeWrapper;
import invengo.cn.rocketmq.common.protocol.header.namesrv.RegisterBrokerRequestHeader;
import invengo.cn.rocketmq.remoting.RemotingClient;
import invengo.cn.rocketmq.remoting.exception.RemotingSendRequestException;
import invengo.cn.rocketmq.remoting.exception.RemotingTimeoutException;
import invengo.cn.rocketmq.remoting.netty.NettyClientConfig;
import invengo.cn.rocketmq.remoting.netty.NettyRemotingClient;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;

public class BrokerOutAPI {
	private static Logger logger = LogManager.getLogger(BrokerOutAPI.class);

	
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
			final String clusterName,
			final String brokerAddr,
			final String brokerName,
			final long brokerId,
			final String haServerAddr,
			final TopicConfigSerializeWrapper topicConfigWrapper,
			final boolean oneway,
			final int timeoutMills
			){
		String namesrvAddr = "127.0.0.1:9876";
		RegisterBrokerResult result = null;
		try {
			result = registerBroker(namesrvAddr, clusterName, brokerAddr, 
					brokerName, brokerId, haServerAddr, topicConfigWrapper, oneway, timeoutMills);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
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
			) throws RemotingTimeoutException, RemotingSendRequestException, InterruptedException{
		RegisterBrokerRequestHeader requestHeader = new RegisterBrokerRequestHeader();
		requestHeader.setBrokerAddr(brokerAddr);
		requestHeader.setBrokerId(brokerId);
		requestHeader.setBrokerName(brokerName);
		requestHeader.setClusterName(clusterName);
		requestHeader.setHaServerAddr(haServerAddr);
		
		RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.REGISTER_BROKER, requestHeader);
		request.setRemark("hello");
		request.setBody(topicConfigWrapper.encode());
		RemotingCommand response = this.remotingClient.invokeSync(namesrvAddr, request, 3000);
		logger.info(response);
		return null;
	}
	

}
