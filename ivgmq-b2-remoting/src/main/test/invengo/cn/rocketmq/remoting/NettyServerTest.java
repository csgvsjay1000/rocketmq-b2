package invengo.cn.rocketmq.remoting;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import invengo.cn.rocketmq.remoting.exception.RemotingSendRequestException;
import invengo.cn.rocketmq.remoting.exception.RemotingTimeoutException;
import invengo.cn.rocketmq.remoting.netty.NettyClientConfig;
import invengo.cn.rocketmq.remoting.netty.NettyRemotingClient;
import invengo.cn.rocketmq.remoting.netty.NettyRemotingServer;
import invengo.cn.rocketmq.remoting.netty.NettyServerConfig;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;

public class NettyServerTest {

	private static NettyRemotingServer remotingServer;
	private static NettyRemotingClient remotingClient;
	
	private static NettyRemotingServer createRemotingServer(){
		NettyServerConfig config = new NettyServerConfig();
		NettyRemotingServer server = new NettyRemotingServer(config);
		
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
		RemotingCommand request = RemotingCommand.createRequestCommand(0, null);
        request.setCode(2);
        request.setBody("hello".getBytes());
        remotingClient.invokeSync("localhost:8888", request, 1000 * 3);
	}
	
}
