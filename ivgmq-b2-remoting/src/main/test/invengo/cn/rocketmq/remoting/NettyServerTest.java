package invengo.cn.rocketmq.remoting;

import invengo.cn.rocketmq.remoting.netty.NettyRemotingServer;
import invengo.cn.rocketmq.remoting.netty.NettyServerConfig;

public class NettyServerTest {

	public static void main(String[] args) {
		NettyRemotingServer server = new NettyRemotingServer(new NettyServerConfig());
		server.start();
	}
	
}
