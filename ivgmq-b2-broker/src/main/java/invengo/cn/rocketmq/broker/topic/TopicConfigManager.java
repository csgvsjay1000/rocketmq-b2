package invengo.cn.rocketmq.broker.topic;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import invengo.cn.rocketmq.common.ConfigManager;
import invengo.cn.rocketmq.common.TopicConfig;

public class TopicConfigManager extends ConfigManager{

	private final ConcurrentMap<String, TopicConfig> topicConfigTable =
	        new ConcurrentHashMap<String, TopicConfig>(1024);
	
	@Override
	public String encode() {
		// TODO Auto-generated method stub
		return null;
	}

}
