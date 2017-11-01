package invengo.cn.rocketmq.remoting.exception;

public class RemotingSendRequestException extends RemotingException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7883144461606430542L;

	public RemotingSendRequestException(String addr) {
		this(addr, null);
	}
	
	public RemotingSendRequestException(String addr,Throwable cause) {
		super("send request to <"+addr+"> failed. ", cause);
	}

}
