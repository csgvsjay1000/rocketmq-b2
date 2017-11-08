package invengo.cn.rocketmq.namesrv;

import org.junit.Test;

import invengo.cn.rocketmq.common.namesrv.NamesrvConfig;
import invengo.cn.rocketmq.remoting.netty.NettyServerConfig;

public class NamesrvControllerTest {

	@Test
	public void testRestart() {
		
		NamesrvController controller = new NamesrvController(new NamesrvConfig(), new NettyServerConfig());
		controller.initialize();
		controller.start();
		
	}
	
}
