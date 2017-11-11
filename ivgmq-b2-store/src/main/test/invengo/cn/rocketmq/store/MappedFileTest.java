package invengo.cn.rocketmq.store;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import invengo.cn.rocketmq.store.impl.MappedFile;

public class MappedFileTest {
	
	private static Logger logger = LogManager.getLogger(MappedFileTest.class);

	MappedFile mappedFile;
	
	private final String storeMessage = "Once, there was a chance for me!";
	
	@Before
	public void init() throws IOException {
		mappedFile = new MappedFile("target/unit_test_store/MappedFileTest/000", 1024*64);
	}
	
	@Test
	public void testAppendMessage() {
		
		boolean result = mappedFile.appendMessage(storeMessage.getBytes());
		result = mappedFile.appendMessage(storeMessage.getBytes());
		result = mappedFile.appendMessage(storeMessage.getBytes());
		result = mappedFile.appendMessage(storeMessage.getBytes());
		logger.info(result);
	}
	
}
