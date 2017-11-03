package invengo.cn.rocketmq.remoting.protocol;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;


public class RemotingCommand {

	static AtomicInteger requestId = new AtomicInteger(0);
	private static final int RPC_TYPE = 0;  //flag标志位  第1位, 0 表示request，1表示response
	private static final int RPC_ONEWAY = 1;  //flag标志位  第二位, 0 表示RPC，1表示oneway
	
	private int code;
	private int flag = 0;
	private int opaque = requestId.incrementAndGet();
	private transient byte[] body;
	
	public static RemotingCommand createRequestCommand(int code,Object object) {
		RemotingCommand command = new RemotingCommand();
		command.setCode(code);
		
		return command;
	}
	
	public static RemotingCommand createResponseCommand(int code) {
		RemotingCommand command = new RemotingCommand();
		command.setCode(code);
		command.makeResponseType();
		command.setBody("回复".getBytes());
		return command;
	}
	
	public ByteBuffer encodeHeader() {
		return encodeHeader(this.body == null?0:this.body.length);
	}
	
	public ByteBuffer encodeHeader(final int bodyLength) {
		
		int length = 4;  //headFiledLength
		
		length += 4;  // code length
		length += 4;  // flag length
		length += 4;  // opaque length
		
		length += bodyLength;
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(length);
		byteBuffer.putInt(length-4);
		byteBuffer.putInt(code);
		byteBuffer.putInt(flag);
		byteBuffer.putInt(opaque);
		if (body != null) {
			byteBuffer.put(body);
		}
		byteBuffer.flip();
		return byteBuffer;
	}
	
	public static RemotingCommand decode(ByteBuffer byteBuffer) {
		RemotingCommand cmd = new RemotingCommand();
		
		int length = byteBuffer.limit();
		int code = byteBuffer.getInt();
		int flag = byteBuffer.getInt();
		int opaque = byteBuffer.getInt();
		
		int headerLength = 4;  // code length
		headerLength += 4;  // flag length
		headerLength += 4;  // opaque length
		
		cmd.setCode(code);
		cmd.setFlag(flag);
		cmd.setOpaque(opaque);
		if (length > headerLength) {
			byte[] bodyData = new byte[length-headerLength];
			byteBuffer.get(bodyData);
			cmd.body = bodyData;
		}
		
		return cmd;
	}
	
	public RemotingCommandType getType() {
		if (this.isResponseType()) {
			return RemotingCommandType.RESPONSE_COMMAND;
		}
		return RemotingCommandType.REQUEST_COMMAND;
	}

	private boolean isResponseType(){
		int bit = 1 << RPC_TYPE;
		int value = flag & bit;
		return value>0?true:false;
	}
	
	private void makeResponseType(){
		int bit = 1 << RPC_TYPE;
		this.flag |= bit;
	}
	
	private boolean isOnewayType(){
		int bit = 1 << RPC_ONEWAY;
		int value = flag & bit;
		return value>0?true:false;
	}
	
	private void makeOnewayType(){
		int bit = 1 << RPC_ONEWAY;
		this.flag |= bit;
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

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	@Override
    public String toString() {
        return "RemotingCommand [code=" + code + ", opaque=" + opaque + "]";
    }
	
}
