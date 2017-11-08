package invengo.cn.rocketmq.namesrv.routeinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import invengo.cn.rocketmq.common.MixAll;
import invengo.cn.rocketmq.common.TopicConfig;
import invengo.cn.rocketmq.common.namesrv.RegisterBrokerResult;
import invengo.cn.rocketmq.common.protocol.body.ClusterInfo;
import invengo.cn.rocketmq.common.protocol.body.TopicConfigSerializeWrapper;
import invengo.cn.rocketmq.common.protocol.body.TopicList;
import invengo.cn.rocketmq.common.protocol.route.BrokerData;
import invengo.cn.rocketmq.common.protocol.route.QueueData;

public class RouteInfoManager {

	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	
	private final Map<String/*clusterName*/, Set<String>/*brokerNames*/> clusterAddrTable; 
	private final Map<String/*brokerName*/,BrokerData> brokerAddrTable;
	private final Map<String/*topic*/, List<QueueData>> topicQueueTable;
	
	public RouteInfoManager() {
		this.clusterAddrTable = new HashMap<String, Set<String>>(32);
		this.brokerAddrTable = new HashMap<String, BrokerData>(128);
		this.topicQueueTable = new HashMap<String, List<QueueData>>(1024);
		
	}
	
	public RegisterBrokerResult registerBroker(
			final String clusterName,
			final String brokerAddr,
			final String brokerName,
			long brokerId,
			final TopicConfigSerializeWrapper topicConfigWrapper
			) {
		RegisterBrokerResult result = new RegisterBrokerResult();
		try {
			try {
				this.lock.writeLock().lockInterruptibly();
				Set<String> brokerNames = this.clusterAddrTable.get(clusterName);
				if (brokerNames == null) {
					brokerNames = new HashSet<String>();
					this.clusterAddrTable.put(clusterName, brokerNames);
				}
				brokerNames.add(brokerName);
				
				boolean registerFirst = false;
				BrokerData brokerData = this.brokerAddrTable.get(brokerName);
				if (null == brokerData) {
					registerFirst = true;
					brokerData = new BrokerData(clusterName, brokerName, new HashMap<Long, String>());
					this.brokerAddrTable.put(brokerName, brokerData);
				}
				String oldAddr = brokerData.getBrokerAddrs().put(brokerId, brokerAddr);
				registerFirst = registerFirst || (null == oldAddr);
				if (null != topicConfigWrapper 
						&& MixAll.MASTER_ID == brokerId) {
					// 主broker
					if (registerFirst) {
						ConcurrentMap<String, TopicConfig> tcTable = topicConfigWrapper.getTopicConcurrentMap();
						for(Map.Entry<String, TopicConfig> entry :tcTable.entrySet()){
							this.createAndUpdateQueueData(brokerName, entry.getValue());
						}
					}
				}
				
				
			} finally {
				this.lock.writeLock().unlock();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	
	public byte[] getAllClusterInfo() {
		ClusterInfo clusterInfoSerializeWrapper = new ClusterInfo();
		clusterInfoSerializeWrapper.setBrokerAddrTable(brokerAddrTable);
		clusterInfoSerializeWrapper.setClusterAddrTable(clusterAddrTable);
		return clusterInfoSerializeWrapper.encode();
	}
	
	public byte[] getAllTopicList() {
		TopicList topicList = new TopicList();
		try {
			try {
				this.lock.readLock().lockInterruptibly();
				topicList.getTopicList().addAll(topicQueueTable.keySet());
			} finally {
				this.lock.readLock().unlock();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return topicList.encode();
	}
	
	private void createAndUpdateQueueData(final String brokerName,final TopicConfig topicConfig){
		QueueData queueData = new QueueData();
		queueData.setBrokerName(brokerName);
		queueData.setPerm(topicConfig.getPerm());
		queueData.setReadQueueNums(topicConfig.getReadQueueNums());
		queueData.setWriteQueueNums(topicConfig.getWriteQueueNums());
		
		List<QueueData> queueDataList = this.topicQueueTable.get(topicConfig.getTopicName());
		if (queueDataList == null) {
			queueDataList = new LinkedList<QueueData>();  //插入快，检索慢
			queueDataList.add(queueData);
			this.topicQueueTable.put(topicConfig.getTopicName(), queueDataList);
		}else {
			// add a new one
		}
	}
	
}
