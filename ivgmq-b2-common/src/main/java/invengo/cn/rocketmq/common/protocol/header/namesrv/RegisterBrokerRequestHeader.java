package invengo.cn.rocketmq.common.protocol.header.namesrv;

import invengo.cn.rocketmq.remoting.CommandCustomHeader;

public class RegisterBrokerRequestHeader implements CommandCustomHeader{

	private String brokerName;
	
	private String brokerAddr;

	private String clusterName;
	
	private String haServerAddr;
	
	private Long brokerId;
	
	public void checkFileds() {
		// TODO Auto-generated method stub
		
	}

	public String getBrokerName() {
		return brokerName;
	}

	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}

	public String getBrokerAddr() {
		return brokerAddr;
	}

	public void setBrokerAddr(String brokerAddr) {
		this.brokerAddr = brokerAddr;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getHaServerAddr() {
		return haServerAddr;
	}

	public void setHaServerAddr(String haServerAddr) {
		this.haServerAddr = haServerAddr;
	}

	public Long getBrokerId() {
		return brokerId;
	}

	public void setBrokerId(Long brokerId) {
		this.brokerId = brokerId;
	}
	
}
