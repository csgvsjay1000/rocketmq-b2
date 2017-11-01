package invengo.cn.rocketmq.remoting.exception;

public class RemotingTimeoutException extends RemotingException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3837328268888391816L;

	public RemotingTimeoutException(String addr,final long timeoutMillis) {
		this(addr, timeoutMillis, null);
	}

	public RemotingTimeoutException(String addr,final long timeoutMillis,Throwable cause) {
		super("wait response on the channel <"+addr+"> timeout, "+timeoutMillis+"(ms)", cause);
	}
	
}
