package invengo.cn.rocketmq.broker.topic;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import invengo.cn.rocketmq.broker.BrokerController;
import invengo.cn.rocketmq.common.ConfigManager;
import invengo.cn.rocketmq.common.DataVersion;
import invengo.cn.rocketmq.common.MixAll;
import invengo.cn.rocketmq.common.TopicConfig;
import invengo.cn.rocketmq.common.protocol.body.TopicConfigSerializeWrapper;

public class TopicConfigManager extends ConfigManager{

	private final ConcurrentMap<String, TopicConfig> topicConfigTable =
	        new ConcurrentHashMap<String, TopicConfig>(1024);
	private final DataVersion dataVersion = new DataVersion();
	private final Set<String> systemTopicList = new HashSet<String>();
	private BrokerController brokerController;
	
	public TopicConfigManager() {
		// TODO Auto-generated constructor stub
	}
	
	public TopicConfigManager(BrokerController brokerController) {
		this.brokerController = brokerController;
		{
			// MixAll.SELF_TEST_TOPIC
			String topic = MixAll.SELF_TEST_TOPIC;
			TopicConfig topicConfig = new TopicConfig(topic);
			this.systemTopicList.add(topic);
			topicConfig.setReadQueueNums(1);
			topicConfig.setWriteQueueNums(1);
			this.topicConfigTable.put(topic, topicConfig);
		}
	}
	
	@Override
	public String encode() {
		TopicConfigSerializeWrapper wrapper = new TopicConfigSerializeWrapper();
		wrapper.setDataVersion(dataVersion);
		wrapper.setTopicConcurrentMap(topicConfigTable);
		return wrapper.toJson();
	}
	
	public TopicConfigSerializeWrapper buildTopicConfigSerializeWrapper() {
        TopicConfigSerializeWrapper topicConfigSerializeWrapper = new TopicConfigSerializeWrapper();
        topicConfigSerializeWrapper.setTopicConcurrentMap(this.topicConfigTable);
        topicConfigSerializeWrapper.setDataVersion(this.dataVersion);
        return topicConfigSerializeWrapper;
    }

	public ConcurrentMap<String, TopicConfig> getTopicConfigTable() {
		return topicConfigTable;
	}

	public DataVersion getDataVersion() {
		return dataVersion;
	}
	
	
}
