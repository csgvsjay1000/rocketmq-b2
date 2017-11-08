package invengo.cn.rocketmq.common.protocol.body;

import java.util.HashSet;
import java.util.Set;

import invengo.cn.rocketmq.remoting.protocol.RemotingSerializable;

public class TopicList extends RemotingSerializable{
	private Set<String> topicList = new HashSet<String>();
	

	public Set<String> getTopicList() {
		return topicList;
	}

	public void setTopicList(Set<String> topicList) {
		this.topicList = topicList;
	}
	
	
}
