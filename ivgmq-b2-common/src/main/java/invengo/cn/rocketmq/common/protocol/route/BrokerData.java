package invengo.cn.rocketmq.common.protocol.route;

import java.util.Map;

public class BrokerData {

	private String clusterName;
	private String brokerName;
	private Map<Long/*broker ID*/, String/*brokerAddr*/> brokerAddrs;
	
	public BrokerData(String clusterName,String brokerName,Map<Long, String> brokerAddrs) {
		this.brokerName = brokerName;
		this.brokerAddrs = brokerAddrs;
		this.clusterName = clusterName;
	}
	
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	public String getBrokerName() {
		return brokerName;
	}
	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}
	public Map<Long, String> getBrokerAddrs() {
		return brokerAddrs;
	}
	public void setBrokerAddrs(Map<Long, String> brokerAddrs) {
		this.brokerAddrs = brokerAddrs;
	}
	
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((brokerAddrs == null) ? 0 : brokerAddrs.hashCode());
        result = prime * result + ((brokerName == null) ? 0 : brokerName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BrokerData other = (BrokerData) obj;
        if (brokerAddrs == null) {
            if (other.brokerAddrs != null)
                return false;
        } else if (!brokerAddrs.equals(other.brokerAddrs))
            return false;
        if (brokerName == null) {
            if (other.brokerName != null)
                return false;
        } else if (!brokerName.equals(other.brokerName))
            return false;
        return true;
    }
}
