package invengo.cn.rocketmq.remoting;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import invengo.cn.rocketmq.remoting.exception.RemotingSendRequestException;
import invengo.cn.rocketmq.remoting.exception.RemotingTimeoutException;
import invengo.cn.rocketmq.remoting.netty.NettyClientConfig;
import invengo.cn.rocketmq.remoting.netty.NettyRemotingClient;
import invengo.cn.rocketmq.remoting.netty.NettyRemotingServer;
import invengo.cn.rocketmq.remoting.netty.NettyRequestProcessor;
import invengo.cn.rocketmq.remoting.netty.NettyServerConfig;
import invengo.cn.rocketmq.remoting.netty.ResponseFuture;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

public class NettyServerTest {

	private static Logger logger = LogManager.getLogger(NettyServerTest.class);

	private static NettyRemotingServer remotingServer;
	private static NettyRemotingClient remotingClient;
	
	private static NettyRemotingServer createRemotingServer(){
		NettyServerConfig config = new NettyServerConfig();
		NettyRemotingServer server = new NettyRemotingServer(config);
		server.registerProcessor(2, new NettyRequestProcessor() {
			
			public boolean rejectRequest() {
				// TODO Auto-generated method stub
				return false;
			}
			
			public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {
				request.setRemark("Hi "+ctx.channel().remoteAddress());
				if (request.getBody() != null) {
				}
				return request;
			}
		}, null);
		
		server.start();
		return server;
	}
	
	private static NettyRemotingClient createRemotingClient() {
		NettyClientConfig config = new NettyClientConfig();
		NettyRemotingClient client = new NettyRemotingClient(config);
		
		client.start();
		return client;
	}
	
	@BeforeClass
	public static void setup() {
		remotingServer = createRemotingServer();
		remotingClient = createRemotingClient();
	}
	
	@AfterClass
	public static void shutdown() {
		remotingClient.shutdown();
		remotingServer.shutdown();
	}
	
	@Test
	public void testInvokeSync() throws RemotingTimeoutException, RemotingSendRequestException, InterruptedException {
		TestCustomHeader requestHeader = new TestCustomHeader();
		requestHeader.setBrokerAddr("127.0.0.1:10891");
		requestHeader.setBrokerName("test-broker");
		RemotingCommand request = RemotingCommand.createRequestCommand(0, requestHeader);
        request.setCode(2);
        request.setBody("hello".getBytes());
        RemotingCommand response = remotingClient.invokeSync("localhost:8888", request, 1000 * 3);
        logger.info(response);
	}
	
	
	
	@Test
	public void testInvokeAsync() throws RemotingTimeoutException, RemotingSendRequestException, InterruptedException {
		
		int sendNums = 10;
		final CountDownLatch latch = new CountDownLatch(sendNums);
		final AtomicInteger count = new AtomicInteger(0);
		for (int i = 0; i < sendNums; i++) {
			RemotingCommand request = RemotingCommand.createRequestCommand(0, null);
			request.setCode(2);
			request.setBody("hello".getBytes());
			remotingClient.invokeAsync("localhost:8888", request, 1000 * 3,new InvokeCallback() {
				
				public void operationComplete(ResponseFuture responseFuture) {
					latch.countDown();
					count.incrementAndGet();
					RemotingCommand response = responseFuture.getResponseCommand();
					//latch.countDown();
				}
			});
		}
		latch.await();
		assertEquals(sendNums, count.get());
	}
	
	@Test
	public void testInvokeOneway() throws RemotingTimeoutException, RemotingSendRequestException, InterruptedException {
		
		int sendNums = 10;
		final CountDownLatch latch = new CountDownLatch(sendNums);
		final AtomicInteger count = new AtomicInteger(0);
		for (int i = 0; i < sendNums; i++) {
			RemotingCommand request = RemotingCommand.createRequestCommand(0, null);
			request.setCode(2);
			request.setBody("hello".getBytes());
			remotingClient.invokeOneway("localhost:8888", request, 1000 * 3);
			count.incrementAndGet();
			//latch.countDown();
		}
		latch.await();
		assertEquals(sendNums, count.get());
	}
	
}

class TestCustomHeader implements CommandCustomHeader{
	
	private String brokerName;
	
	private String brokerAddr;

	public void checkFileds() {
		// TODO Auto-generated method stub
		
	}

	public String getBrokerName() {
		return brokerName;
	}

	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}

	public String getBrokerAddr() {
		return brokerAddr;
	}

	public void setBrokerAddr(String brokerAddr) {
		this.brokerAddr = brokerAddr;
	}
	
}
