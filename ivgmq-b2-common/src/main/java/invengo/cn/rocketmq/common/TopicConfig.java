package invengo.cn.rocketmq.common;

import org.omg.CORBA.PRIVATE_MEMBER;

import invengo.cn.rocketmq.common.constant.PermName;

public class TopicConfig {
	private static final String SEPARATOR = " ";
    public static int defaultReadQueueNums = 16;
    public static int defaultWriteQueueNums = 16;
    private String topicName;
    private int readQueueNums = defaultReadQueueNums;
    private int writeQueueNums = defaultWriteQueueNums;
    private int perm = PermName.PERM_READ | PermName.PERM_WRITE;  // 权限 可读，可写
    private boolean order = false;
    
    
    
	public static int getDefaultWriteQueueNums() {
		return defaultWriteQueueNums;
	}
	public static void setDefaultWriteQueueNums(int defaultWriteQueueNums) {
		TopicConfig.defaultWriteQueueNums = defaultWriteQueueNums;
	}
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public int getReadQueueNums() {
		return readQueueNums;
	}
	public void setReadQueueNums(int readQueueNums) {
		this.readQueueNums = readQueueNums;
	}
	public int getWriteQueueNums() {
		return writeQueueNums;
	}
	public void setWriteQueueNums(int writeQueueNums) {
		this.writeQueueNums = writeQueueNums;
	}
	public int getPerm() {
		return perm;
	}
	public void setPerm(int perm) {
		this.perm = perm;
	}
	public boolean isOrder() {
		return order;
	}
	public void setOrder(boolean order) {
		this.order = order;
	}
	public static String getSeparator() {
		return SEPARATOR;
	}
    
}
