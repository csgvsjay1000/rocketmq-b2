package invengo.cn.rocketmq.remoting.protocol;

import java.nio.ByteBuffer;

public class RocketMQSerializable {

	public static byte[] rocketMQProtocolEncode(RemotingCommand command) {
		
		int totalLength = calTotalLength();
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(totalLength);
		byteBuffer.putShort((short)command.getCode());
		
		return byteBuffer.array();
	}
	
	public static RemotingCommand rocketMQProtocolDecode(byte[] headerArray) {
		RemotingCommand cmd = new RemotingCommand();
		ByteBuffer byteBuffer = ByteBuffer.wrap(headerArray);
		cmd.setCode(byteBuffer.getShort());
		return cmd;
	}
	
	private static int calTotalLength(){
		
		// short int code(~32767) 
		int length = 2;
		
		return length;
	}
	
}
