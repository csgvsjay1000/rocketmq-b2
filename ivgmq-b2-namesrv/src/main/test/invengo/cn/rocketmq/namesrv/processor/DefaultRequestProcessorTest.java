package invengo.cn.rocketmq.namesrv.processor;


import invengo.cn.rocketmq.common.TopicConfig;
import invengo.cn.rocketmq.common.namesrv.NamesrvConfig;
import invengo.cn.rocketmq.common.namesrv.RegisterBrokerResult;
import invengo.cn.rocketmq.common.protocol.body.TopicConfigSerializeWrapper;
import invengo.cn.rocketmq.namesrv.NamesrvController;
import invengo.cn.rocketmq.namesrv.routeinfo.RouteInfoManager;
import invengo.cn.rocketmq.remoting.netty.NettyServerConfig;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.Before;
import org.junit.Test;

public class DefaultRequestProcessorTest {
	
	private DefaultRequestProcessor defaultRequestProcessor;

    private NamesrvController namesrvController;

    private NamesrvConfig namesrvConfig;

    private NettyServerConfig nettyServerConfig;

    private RouteInfoManager routeInfoManager;
	
	@Before
	public void init() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		namesrvConfig = new NamesrvConfig();
        nettyServerConfig = new NettyServerConfig();
        routeInfoManager = new RouteInfoManager();

        namesrvController = new NamesrvController(namesrvConfig, nettyServerConfig);

        Field field = NamesrvController.class.getDeclaredField("routeInfoManager");
        field.setAccessible(true);
        field.set(namesrvController, routeInfoManager);
        defaultRequestProcessor = new DefaultRequestProcessor(namesrvController);
        registerRouteInfoManager();
	}

	@Test
	public void testRegisterBroker() {
		RemotingCommand request = RemotingCommand.createRequestCommand(2, null);
		ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
		
//		RemotingCommand response = 
		
	}
	
	private void registerRouteInfoManager() {
        TopicConfigSerializeWrapper topicConfigSerializeWrapper = new TopicConfigSerializeWrapper();
        ConcurrentMap<String, TopicConfig> topicConfigConcurrentHashMap = new ConcurrentHashMap<String, TopicConfig>();
        TopicConfig topicConfig = new TopicConfig();
        topicConfig.setWriteQueueNums(8);
        topicConfig.setTopicName("unit-test");
        topicConfig.setPerm(6);
        topicConfig.setReadQueueNums(8);
        topicConfig.setOrder(false);
        topicConfigConcurrentHashMap.put("unit-test", topicConfig);
        topicConfigSerializeWrapper.setTopicConcurrentMap(topicConfigConcurrentHashMap);
        Channel channel = mock(Channel.class);
        RegisterBrokerResult registerBrokerResult = routeInfoManager.registerBroker("default-cluster", "127.0.0.1:10911", "default-broker", 0,
            topicConfigSerializeWrapper);

    }
	
}
