package invengo.cn.rocketmq.remoting.protocol;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import invengo.cn.rocketmq.remoting.CommandCustomHeader;

public class RemotingCommandTest {

	private static Logger logger = LogManager.getLogger(RemotingCommandTest.class);
	
	@Test
	public void testMapSerialize() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("brokerName", "test-broker");
		map.put("brokerAddr", "127.0.0.1:10891");
		byte[] data = RemotingCommand.mapSerialize(map);
		Map<String, String> result = RemotingCommand.mapDeserialize(data);
		logger.info(result);
	}
	
	@Test
	public void mapSerializeTest() {
		TestCustomHeader requestHeader = new TestCustomHeader();
		requestHeader.setBrokerAddr("127.0.0.1:10891");
		requestHeader.setBrokerName("test-broker");
		RemotingCommand command = RemotingCommand.createRequestCommand(2, requestHeader);
		command.customHeaderEncode();
		
		
		
		logger.info(command);
		
	}
	
}

class TestCustomHeader implements CommandCustomHeader{
	
	private String brokerName;
	
	private String brokerAddr;

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
	
}