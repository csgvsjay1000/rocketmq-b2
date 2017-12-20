package invengo.cn.rocketmq.common.message;

import java.io.Serializable;
import java.util.Map;

public class Message implements Serializable{
	
	private static final long serialVersionUID = 4842104877667997818L;
	
	private String topic;
	
	private int flag;
	
	private Map<String, String> properties;
	
	private byte[] body;
	
	public Message(){
		
	}
	
	public Message(String topic, byte[] body) {
        this(topic, "", "", 0, body, true);
    }
	
	public Message(String topic,String tags,String keys,int flag,byte[] body, boolean waitStockMsgOK) {
		this.topic = topic;
		this.flag = flag;
		this.body = body;
		
	}
	

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}
	
}
