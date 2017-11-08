package invengo.cn.rocketmq.broker.topic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import invengo.cn.rocketmq.common.DataVersion;
import invengo.cn.rocketmq.common.TopicConfig;

public class TopicConfigManagerTest {

	private Logger logger = LogManager.getLogger(getClass());
	
	private TopicConfigManager topicConfigManager;
	
	@Before
	public void init() {
		topicConfigManager = new TopicConfigManager();
	}
	
	@Test
	public void testEncode() {
		DataVersion dataVersion = new DataVersion();
		TopicConfig topicConfig = new TopicConfig();
	
		String encodeStr = topicConfigManager.encode();
		logger.info(encodeStr);
	}
	
}
