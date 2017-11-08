package invengo.cn.rocketmq.common.protocol.body;

import java.util.Map;
import java.util.Set;

import invengo.cn.rocketmq.common.protocol.route.BrokerData;
import invengo.cn.rocketmq.remoting.protocol.RemotingSerializable;

public class ClusterInfo extends RemotingSerializable{
	private Map<String/*clusterName*/, Set<String>/*brokerNames*/> clusterAddrTable; 
	private Map<String/*brokerName*/,BrokerData> brokerAddrTable;
	
	public Map<String, Set<String>> getClusterAddrTable() {
		return clusterAddrTable;
	}
	public void setClusterAddrTable(Map<String, Set<String>> clusterAddrTable) {
		this.clusterAddrTable = clusterAddrTable;
	}
	public Map<String, BrokerData> getBrokerAddrTable() {
		return brokerAddrTable;
	}
	public void setBrokerAddrTable(Map<String, BrokerData> brokerAddrTable) {
		this.brokerAddrTable = brokerAddrTable;
	}
	
}
