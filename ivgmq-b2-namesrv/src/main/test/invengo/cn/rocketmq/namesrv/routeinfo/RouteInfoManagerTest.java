package invengo.cn.rocketmq.namesrv.routeinfo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import invengo.cn.rocketmq.common.TopicConfig;
import invengo.cn.rocketmq.common.protocol.body.ClusterInfo;
import invengo.cn.rocketmq.common.protocol.body.TopicConfigSerializeWrapper;
import invengo.cn.rocketmq.common.protocol.body.TopicList;

public class RouteInfoManagerTest {
	
	private Logger logger = LogManager.getLogger(RouteInfoManagerTest.class);

	private static RouteInfoManager routeInfoManager;
	
	@Before
	public void setup() {
		routeInfoManager = new RouteInfoManager();
		testRegisterBroker();
	}
	
	@Test
	public void testRegisterBroker() {
		TopicConfigSerializeWrapper topicConfigSerializeWrapper = new TopicConfigSerializeWrapper();
		ConcurrentMap<String, TopicConfig> topicConcurrentMap = new ConcurrentHashMap<String, TopicConfig>();
		TopicConfig topicConfig = new TopicConfig();
		topicConfig.setTopicName("unit-test");
		topicConcurrentMap.put("unit-test", topicConfig);
		topicConfigSerializeWrapper.setTopicConcurrentMap(topicConcurrentMap);
		routeInfoManager.registerBroker("default-cluster", "127.0.0.1:10911", "default-broker", 0,topicConfigSerializeWrapper);
	}
	
	@Test
	public void testGetAllClusterInfo() {
		byte[] data = routeInfoManager.getAllClusterInfo();
		ClusterInfo clusterInfo =  ClusterInfo.decode(data, ClusterInfo.class);
		logger.info(clusterInfo);
	}
	
	@Test
	public void testGetAllTopicList() {
		byte[] data = routeInfoManager.getAllTopicList();
		TopicList topicList =  TopicList.decode(data, TopicList.class);
		logger.info(topicList);
	}
}
