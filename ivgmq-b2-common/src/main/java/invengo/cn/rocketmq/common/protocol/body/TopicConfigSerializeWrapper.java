package invengo.cn.rocketmq.common.protocol.body;

import java.util.concurrent.ConcurrentMap;

import invengo.cn.rocketmq.common.DataVersion;
import invengo.cn.rocketmq.common.TopicConfig;
import invengo.cn.rocketmq.remoting.protocol.RemotingSerializable;

public class TopicConfigSerializeWrapper extends RemotingSerializable{

	ConcurrentMap<String, TopicConfig> topicConcurrentMap;
	private DataVersion dataVersion;

	public ConcurrentMap<String, TopicConfig> getTopicConcurrentMap() {
		return topicConcurrentMap;
	}

	public void setTopicConcurrentMap(ConcurrentMap<String, TopicConfig> topicConcurrentMap) {
		this.topicConcurrentMap = topicConcurrentMap;
	}

	public DataVersion getDataVersion() {
		return dataVersion;
	}

	public void setDataVersion(DataVersion dataVersion) {
		this.dataVersion = dataVersion;
	}
	
}
