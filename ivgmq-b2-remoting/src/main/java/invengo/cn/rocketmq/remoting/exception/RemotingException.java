package invengo.cn.rocketmq.remoting.exception;

public class RemotingException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7741518068088905567L;

	public RemotingException(String message) {
		super(message);
	}
	
	public RemotingException(String message,Throwable cause) {
		super(message, cause);
	}
	
}
