package invengo.cn.rocketmq.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

import invengo.cn.rocketmq.common.constant.PermName;
import invengo.cn.rocketmq.remoting.common.RemotingUtil;

public class BrokerConfig {
	private String rocketmqHome = "D:/rocketmqHome";
	
	private String namesrvAddr = "127.0.0.1:8090";
	private String brokerIP1 = RemotingUtil.getLocalAddress();
    private String brokerIP2 = RemotingUtil.getLocalAddress();
    private String brokerName = localHostName();
    private String brokerClusterName = "DefaultCluster";
    private long brokerId = MixAll.MASTER_ID;
    private int brokerPermission = PermName.PERM_READ | PermName.PERM_WRITE;
    private int defaultTopicQueueNums = 8;
    private boolean autoCreateTopicEnable = true;

    private boolean clusterTopicEnable = true;
    private boolean brokerTopicEnable = true;
    private boolean autoCreateSubscriptionGroup = true;
    private String messageStorePlugIn = "";
    
    private int sendMessageThreadPoolNums = 1; //16 + Runtime.getRuntime().availableProcessors() * 4;
    private int pullMessageThreadPoolNums = 16 + Runtime.getRuntime().availableProcessors() * 2;
    private int adminBrokerThreadPoolNums = 16;
    private int clientManageThreadPoolNums = 32;
    private int consumerManageThreadPoolNums = 32;
    
    private int registerBrokerTimeoutMills = 6000;
    
    public static String localHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return "DEFAULT_BROKER";
    }
    
	public String getRocketmqHome() {
		return rocketmqHome;
	}
	public void setRocketmqHome(String rocketmqHome) {
		this.rocketmqHome = rocketmqHome;
	}
	public String getNamesrvAddr() {
		return namesrvAddr;
	}
	public void setNamesrvAddr(String namesrvAddr) {
		this.namesrvAddr = namesrvAddr;
	}
	public String getBrokerIP1() {
		return brokerIP1;
	}
	public void setBrokerIP1(String brokerIP1) {
		this.brokerIP1 = brokerIP1;
	}
	public String getBrokerIP2() {
		return brokerIP2;
	}
	public void setBrokerIP2(String brokerIP2) {
		this.brokerIP2 = brokerIP2;
	}

	public String getBrokerName() {
		return brokerName;
	}

	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}

	public String getBrokerClusterName() {
		return brokerClusterName;
	}

	public void setBrokerClusterName(String brokerClusterName) {
		this.brokerClusterName = brokerClusterName;
	}

	public long getBrokerId() {
		return brokerId;
	}

	public void setBrokerId(long brokerId) {
		this.brokerId = brokerId;
	}

	public int getBrokerPermission() {
		return brokerPermission;
	}

	public void setBrokerPermission(int brokerPermission) {
		this.brokerPermission = brokerPermission;
	}

	public int getDefaultTopicQueueNums() {
		return defaultTopicQueueNums;
	}

	public void setDefaultTopicQueueNums(int defaultTopicQueueNums) {
		this.defaultTopicQueueNums = defaultTopicQueueNums;
	}

	public boolean isAutoCreateTopicEnable() {
		return autoCreateTopicEnable;
	}

	public void setAutoCreateTopicEnable(boolean autoCreateTopicEnable) {
		this.autoCreateTopicEnable = autoCreateTopicEnable;
	}

	public boolean isClusterTopicEnable() {
		return clusterTopicEnable;
	}

	public void setClusterTopicEnable(boolean clusterTopicEnable) {
		this.clusterTopicEnable = clusterTopicEnable;
	}

	public boolean isBrokerTopicEnable() {
		return brokerTopicEnable;
	}

	public void setBrokerTopicEnable(boolean brokerTopicEnable) {
		this.brokerTopicEnable = brokerTopicEnable;
	}

	public boolean isAutoCreateSubscriptionGroup() {
		return autoCreateSubscriptionGroup;
	}

	public void setAutoCreateSubscriptionGroup(boolean autoCreateSubscriptionGroup) {
		this.autoCreateSubscriptionGroup = autoCreateSubscriptionGroup;
	}

	public String getMessageStorePlugIn() {
		return messageStorePlugIn;
	}

	public void setMessageStorePlugIn(String messageStorePlugIn) {
		this.messageStorePlugIn = messageStorePlugIn;
	}

	public int getSendMessageThreadPoolNums() {
		return sendMessageThreadPoolNums;
	}

	public void setSendMessageThreadPoolNums(int sendMessageThreadPoolNums) {
		this.sendMessageThreadPoolNums = sendMessageThreadPoolNums;
	}

	public int getPullMessageThreadPoolNums() {
		return pullMessageThreadPoolNums;
	}

	public void setPullMessageThreadPoolNums(int pullMessageThreadPoolNums) {
		this.pullMessageThreadPoolNums = pullMessageThreadPoolNums;
	}

	public int getAdminBrokerThreadPoolNums() {
		return adminBrokerThreadPoolNums;
	}

	public void setAdminBrokerThreadPoolNums(int adminBrokerThreadPoolNums) {
		this.adminBrokerThreadPoolNums = adminBrokerThreadPoolNums;
	}

	public int getClientManageThreadPoolNums() {
		return clientManageThreadPoolNums;
	}

	public void setClientManageThreadPoolNums(int clientManageThreadPoolNums) {
		this.clientManageThreadPoolNums = clientManageThreadPoolNums;
	}

	public int getConsumerManageThreadPoolNums() {
		return consumerManageThreadPoolNums;
	}

	public void setConsumerManageThreadPoolNums(int consumerManageThreadPoolNums) {
		this.consumerManageThreadPoolNums = consumerManageThreadPoolNums;
	}

	public int getRegisterBrokerTimeoutMills() {
		return registerBrokerTimeoutMills;
	}

	public void setRegisterBrokerTimeoutMills(int registerBrokerTimeoutMills) {
		this.registerBrokerTimeoutMills = registerBrokerTimeoutMills;
	}
	
	
}