package invengo.cn.rocketmq.client.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import invengo.cn.rocketmq.remoting.exception.RemotingException;
import invengo.cn.rocketmq.remoting.netty.NettyClientConfig;

public class MQClientAPIImplTest {

	MQClientAPIImpl mqClientAPIImpl;
	
	@Before
	public void init() {
		NettyClientConfig nettyClientConfig = new NettyClientConfig();
		mqClientAPIImpl = new MQClientAPIImpl(nettyClientConfig);
		mqClientAPIImpl.start();
	}
	
	@After
	public void destroy() {
		mqClientAPIImpl.shutdown();
		mqClientAPIImpl = null;
	}
	
	@Test
	public void testSendMessage() throws RemotingException, Exception {
		mqClientAPIImpl.sendMessage();
	}
	
}
