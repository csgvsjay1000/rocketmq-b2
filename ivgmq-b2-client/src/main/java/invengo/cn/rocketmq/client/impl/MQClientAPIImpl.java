package invengo.cn.rocketmq.client.impl;

import java.rmi.RemoteException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import invengo.cn.rocketmq.common.protocol.RequestCode;
import invengo.cn.rocketmq.remoting.RemotingClient;
import invengo.cn.rocketmq.remoting.exception.RemotingException;
import invengo.cn.rocketmq.remoting.netty.NettyClientConfig;
import invengo.cn.rocketmq.remoting.netty.NettyRemotingClient;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;


public class MQClientAPIImpl {

	private static Logger logger = LogManager.getLogger(MQClientAPIImpl.class);
	
	private final RemotingClient remotingClient;
	
	public MQClientAPIImpl(NettyClientConfig nettyClientConfig) {
		this.remotingClient = new NettyRemotingClient(nettyClientConfig);
	}
	
	public boolean start() {
		this.remotingClient.start();
		return true;
	}
	
	public void shutdown() {
		this.remotingClient.shutdown();
	}
	
	public void sendMessage() throws RemotingException,Exception{
		RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.SEND_MESSAGE, null);
		String brokerAddr = "127.0.0.1:10198";
		long timeoutMillis = 3000;
		this.remotingClient.invokeSync(brokerAddr, request, timeoutMillis);
		
	}
	
}
