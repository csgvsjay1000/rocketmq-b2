package invengo.cn.rocketmq.remoting.protocol;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

public class RemotingCommand {

	static AtomicInteger requestId = new AtomicInteger(0);
	
	private int code;
	private int opaque = requestId.incrementAndGet();
	private transient byte[] body;
	
	public static RemotingCommand createRequestCommand(int code,Object object) {
		RemotingCommand command = new RemotingCommand();
		command.setCode(code);
		
		return command;
	}
	
	public ByteBuffer encodeHeader() {
		return encodeHeader(this.body == null?0:this.body.length);
	}
	
	public ByteBuffer encodeHeader(final int bodyLength) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(4+4+bodyLength);
		byteBuffer.putInt(4+4+bodyLength);
		byteBuffer.putInt(code);
		byteBuffer.put(body);
		byteBuffer.flip();
		return byteBuffer;
	}
	
	public static RemotingCommand decode(final ByteBuffer byteBuffer) {
		RemotingCommand cmd = new RemotingCommand();
		int length = byteBuffer.limit();
		int dataLength = byteBuffer.getInt();
		int code = byteBuffer.getInt();
		cmd.setCode(code);
		byte[] bodyData = new byte[length-4-4];
		byteBuffer.get(bodyData);
		cmd.body = bodyData;
		return cmd;
	}
	
	private byte[] headerEncode(){
		return RocketMQSerializable.rocketMQProtocolEncode(this);
	}
	
	private static RemotingCommand headerDecode(byte[] headerArray){
		return RocketMQSerializable.rocketMQProtocolDecode(headerArray);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getOpaque() {
		return opaque;
	}

	public void setOpaque(int opaque) {
		this.opaque = opaque;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}
	
}
