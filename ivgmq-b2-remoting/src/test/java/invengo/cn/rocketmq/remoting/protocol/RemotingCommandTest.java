package invengo.cn.rocketmq.remoting.protocol;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import invengo.cn.rocketmq.remoting.CommandCustomHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

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
	
	@Test
	public void testHeaderEncodeAndDecode() {
		TestCustomHeader requestHeader = new TestCustomHeader();
		requestHeader.setBrokerAddr("127.0.0.1:10891");
		requestHeader.setBrokerName("test-broker");
		RemotingCommand command = RemotingCommand.createRequestCommand(2, requestHeader);
		command.setRemark("test-remark");
		ByteBuffer byteBuffer = command.encodeHeader();
        logger.info(byteBuffer.limit());

		LengthFieldBasedFrameDecoder decoder = new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4);
		EmbeddedChannel channel = new EmbeddedChannel(new TestDecode());
		//channel.writeInbound(byteBuffer);
		Assert.assertTrue(channel.writeInbound(byteBuffer));  
        Assert.assertTrue(channel.finish());  
        
        ByteBuffer buffer = channel.readInbound();
        
        RemotingCommand response = RemotingCommand.decode(buffer);
        logger.info(response);

	}
	
	class TestDecode extends LengthFieldBasedFrameDecoder{

		public TestDecode() {
			super(1024, 0, 4, 0, 4);
		}
		
		@Override
		protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
			ByteBuf frame = null;
			try {
				frame = (ByteBuf) super.decode(ctx, in);
				if (frame == null) {
					return null;
				}
				ByteBuffer byteBuffer = frame.nioBuffer();
				return RemotingCommand.decode(byteBuffer);
			} catch (Exception e) {
			}finally {
				if (frame != null) {
					frame.release();
				}
			}
			
			return null;
		}
		
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