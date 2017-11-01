package invengo.cn.rocketmq.remoting;

import java.nio.ByteBuffer;

public class RemotingCommandV1_1 {

	private int code;
	private byte[] body;
	
	public ByteBuffer encodeHeader() {
		return this.encodeHeader(this.body.length);
	}
	
	public ByteBuffer encodeHeader(int bodyLength) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(4+4+bodyLength);
		byteBuffer.putInt(4+4+bodyLength);
		byteBuffer.putInt(code);
		byteBuffer.put(body);
		byteBuffer.flip();
		return byteBuffer;
	}
	
	public static RemotingCommandV1_1 decode(ByteBuffer byteBuffer) {
		RemotingCommandV1_1 cmd = new RemotingCommandV1_1();
		int length = byteBuffer.limit();
		int dataLength = byteBuffer.getInt();
		int code = byteBuffer.getInt();
		cmd.setCode(code);
		byte[] bodyData = new byte[length-4-4];
		byteBuffer.get(bodyData);
		cmd.body = bodyData;
		return cmd;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}
	
}
