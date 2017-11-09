package invengo.cn.rocketmq.remoting.protocol;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import invengo.cn.rocketmq.remoting.CommandCustomHeader;
import io.netty.util.internal.StringUtil;

public class RemotingCommand {
	private static Logger logger = LogManager.getLogger(RemotingCommand.class);
	public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
	static AtomicInteger requestId = new AtomicInteger(0);
	private static final int RPC_TYPE = 0;  //flag标志位  第1位, 0 表示request，1表示response
	private static final int RPC_ONEWAY = 1;  //flag标志位  第二位, 0 表示RPC，1表示oneway
	private static final Map<Class, String> CANONICAL_NAME_CACHE = new HashMap<Class, String>();
	private static final String STRING_CANONICAL_NAME = String.class.getCanonicalName();
    private static final String DOUBLE_CANONICAL_NAME_1 = Double.class.getCanonicalName();
    private static final String DOUBLE_CANONICAL_NAME_2 = double.class.getCanonicalName();
    private static final String INTEGER_CANONICAL_NAME_1 = Integer.class.getCanonicalName();
    private static final String INTEGER_CANONICAL_NAME_2 = int.class.getCanonicalName();
    private static final String LONG_CANONICAL_NAME_1 = Long.class.getCanonicalName();
    private static final String LONG_CANONICAL_NAME_2 = long.class.getCanonicalName();
    private static final String BOOLEAN_CANONICAL_NAME_1 = Boolean.class.getCanonicalName();
    private static final String BOOLEAN_CANONICAL_NAME_2 = boolean.class.getCanonicalName();
	
	private int code;
	private int flag = 0;
	private int opaque = requestId.incrementAndGet();
	private String remark;
	private transient CommandCustomHeader customHeader;
	private Map<String, String> extFields;
	private transient byte[] body;
	
	public static RemotingCommand createRequestCommand(int code,CommandCustomHeader header) {
		RemotingCommand command = new RemotingCommand();
		command.setCode(code);
		command.setCustomHeader(header);
		return command;
	}
	
	public static RemotingCommand createResponseCommand(int code) {
		return createResponseCommand(code, null);
	}
	
	public static RemotingCommand createResponseCommand(int code,String remark) {
		RemotingCommand command = new RemotingCommand();
		command.setCode(code);
		command.makeResponseType();
		command.setRemark(remark);
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
		length += 4;  // remark length
		int remarkLength = 0;
		byte[] remarkBytes = null;
		if (!StringUtil.isNullOrEmpty(remark)) {
			remarkBytes = remark.getBytes(CHARSET_UTF8);
			remarkLength = remarkBytes.length;
		}
		length += remarkLength;
		length += 4;  // extFields length
		byte[] extBytes = null;
		if (customHeader != null) {
			this.customHeaderEncode();
		}
		if (null != this.extFields) {
			extBytes = RemotingCommand.mapSerialize(extFields);
			if (extBytes != null) {
				length += extBytes.length;
			}
		}
		
		length += bodyLength;
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(length);
		byteBuffer.putInt(length-4);
		byteBuffer.putInt(code);
		byteBuffer.putInt(flag);
		byteBuffer.putInt(opaque);
		
		if (remarkLength>0) {
			byteBuffer.putInt(remarkLength);
			byteBuffer.put(remarkBytes);
		}else {
			byteBuffer.putInt(0);
		}
		if (extBytes != null) {
			byteBuffer.putInt(extBytes.length);
			byteBuffer.put(extBytes);
		}else {
			byteBuffer.putInt(0);
		}
		
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
		int remarkHeadLength = byteBuffer.getInt();
		
		int headerLength = 4;  // code length
		headerLength += 4;  // flag length
		headerLength += 4;  // opaque length
		headerLength += 4;  // remarkHeadLength
		headerLength += 4;  // extFieldLength
		
		cmd.setCode(code);
		cmd.setFlag(flag);
		cmd.setOpaque(opaque);
		if (remarkHeadLength > 0) {
			headerLength += remarkHeadLength;
			byte[] remarkBytes = new byte[remarkHeadLength];
			byteBuffer.get(remarkBytes);
			cmd.setRemark(new String(remarkBytes, CHARSET_UTF8));
		}
		int extFieldLength = byteBuffer.getInt();

		if (extFieldLength > 0) {
			headerLength += extFieldLength;
			byte[] extBytes = new byte[extFieldLength];
			byteBuffer.get(extBytes);
			cmd.extFields = mapDeserialize(extBytes);
		}
		
		if (length > headerLength) {
			byte[] bodyData = new byte[length-headerLength];
			byteBuffer.get(bodyData);
			cmd.body = bodyData;
		}
		
		return cmd;
	}
	
	public void customHeaderEncode(){
		Field[] fields = getClazzFields(this.customHeader.getClass());
		if (extFields == null) {
			this.extFields = new HashMap<String, String>();
		}
		for (Field field : fields) {
			if (!Modifier.isStatic(field.getModifiers())) {
				// 不是静态成员
				String name = field.getName();
				Object value = null;
				try {
					field.setAccessible(true);
					value = field.get(this.customHeader);
				} catch (Exception e) {
					logger.info("Failed to access [{}]",name,e);
				}
				if (value != null) {
					this.extFields.put(name, value.toString());
				}
			}
		}
	}
	
	public CommandCustomHeader decodeCustomHeader(Class<? extends CommandCustomHeader> classHeader) {
		CommandCustomHeader objHeader = null;
		try {
			objHeader = classHeader.newInstance();
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
		if (this.extFields == null) {
			return objHeader;
		}
		Field[] fields = getClazzFields(classHeader);
		for (Field field : fields) {
			if (!Modifier.isStatic(field.getModifiers())) {
				String fieldName = field.getName();
				if (!fieldName.startsWith("this")) {
					String value = this.extFields.get(fieldName);
					if (value == null) {
						continue;
					}
					field.setAccessible(true);
					String type = getCanonicalName(field.getType());
					Object valueParsed = null;
					
					if (type.equals(STRING_CANONICAL_NAME)) {
                        valueParsed = value;
                    } else if (type.equals(INTEGER_CANONICAL_NAME_1) || type.equals(INTEGER_CANONICAL_NAME_2)) {
                        valueParsed = Integer.parseInt(value);
                    } else if (type.equals(LONG_CANONICAL_NAME_1) || type.equals(LONG_CANONICAL_NAME_2)) {
                        valueParsed = Long.parseLong(value);
                    } else if (type.equals(BOOLEAN_CANONICAL_NAME_1) || type.equals(BOOLEAN_CANONICAL_NAME_2)) {
                        valueParsed = Boolean.parseBoolean(value);
                    } else if (type.equals(DOUBLE_CANONICAL_NAME_1) || type.equals(DOUBLE_CANONICAL_NAME_2)) {
                        valueParsed = Double.parseDouble(value);
                    }
					try {
						field.set(objHeader, valueParsed);

					} catch (Exception e) {
						logger.error("Failed field [{}] decoding", fieldName, e);
					}
				}
			}
			
		}
		return objHeader;
	}
	
	public static byte[] mapSerialize(Map<String, String> map) {
		if (null == map || map.isEmpty()) {
			return null;
		}
		// keySize + key + valueSize + value
		int totalLength = 0;
		
		int kvLength = 0;
		
		Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator
					.next();
			if (entry.getKey() != null && entry.getValue() != null) {
				// keySize + key Length
				kvLength = 2+entry.getKey().getBytes(CHARSET_UTF8).length;
				// valueSize + value Length
				kvLength += (4 + entry.getValue().getBytes(CHARSET_UTF8).length);
				totalLength += kvLength;
			}
		}
		ByteBuffer content = ByteBuffer.allocate(totalLength);
		byte[] key;
		byte[] val;
		iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator
					.next();
			if (entry.getKey() != null && entry.getValue() != null) {
				key = entry.getKey().getBytes(CHARSET_UTF8);
				val = entry.getValue().getBytes(CHARSET_UTF8);
				
				content.putShort((short)key.length);
				content.put(key);
				content.putInt(val.length);
				content.put(val);
			}
		}
		
		return content.array();
	}
	
	public static Map<String, String> mapDeserialize(byte[] data) {
		// keySize + key + valueSize + value
		Map<String, String> map = new HashMap<String, String>();
		ByteBuffer byteBuffer = ByteBuffer.wrap(data);
		
		short keySize;
		byte[] key;
		int valSize;
		byte[] val;
		while (byteBuffer.hasRemaining()) {
			keySize = byteBuffer.getShort();
			key = new byte[keySize];
			byteBuffer.get(key);
			
			valSize = byteBuffer.getInt();
			val = new byte[valSize];
			byteBuffer.get(val);
			
			map.put(new String(key, CHARSET_UTF8), new String(val, CHARSET_UTF8));
		}
		
		return map;
	}
	
	private Field[] getClazzFields(Class<? extends CommandCustomHeader> classHeader){
		return classHeader.getDeclaredFields();
	}
	
	private String getCanonicalName(Class clazz){
		String name = CANONICAL_NAME_CACHE.get(clazz);
		if (name == null) {
			name = clazz.getCanonicalName();
			synchronized (CANONICAL_NAME_CACHE) {
				CANONICAL_NAME_CACHE.put(clazz, name);
			}
		}
		return name;
	}
	
	public RemotingCommandType getType() {
		if (this.isResponseType()) {
			return RemotingCommandType.RESPONSE_COMMAND;
		}
		return RemotingCommandType.REQUEST_COMMAND;
	}

	public boolean isResponseType(){
		int bit = 1 << RPC_TYPE;
		int value = flag & bit;
		return value>0?true:false;
	}
	
	public void makeResponseType(){
		int bit = 1 << RPC_TYPE;
		this.flag |= bit;
	}
	
	public boolean isOnewayType(){
		int bit = 1 << RPC_ONEWAY;
		int value = flag & bit;
		return value>0?true:false;
	}
	
	public void makeOnewayType(){
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
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public CommandCustomHeader getCustomHeader() {
		return customHeader;
	}

	public void setCustomHeader(CommandCustomHeader customHeader) {
		this.customHeader = customHeader;
	}

	public Map<String, String> getExtFields() {
		return extFields;
	}

	public void setExtFields(Map<String, String> extFields) {
		this.extFields = extFields;
	}

	@Override
    public String toString() {
        return "RemotingCommand [code=" + code + ", opaque=" + opaque +", remark="+remark+", extFields="+extFields+" ]";
    }
	
}
