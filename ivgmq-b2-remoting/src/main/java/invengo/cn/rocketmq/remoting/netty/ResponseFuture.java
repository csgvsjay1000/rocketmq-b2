package invengo.cn.rocketmq.remoting.netty;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import invengo.cn.rocketmq.remoting.InvokeCallback;
import invengo.cn.rocketmq.remoting.common.SemaphoreReleaseOnlyOnce;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;

public class ResponseFuture {

	private final int opaque;
	private final long timeoutMillis;
	private final InvokeCallback invokeCallback;
	private final long beginTimestamp = System.currentTimeMillis();
	private final CountDownLatch countDownLatch = new CountDownLatch(1);
	
	private volatile boolean sendRequestOK = true;
	private volatile RemotingCommand responseCommand;
	private volatile Throwable cause;
	private final SemaphoreReleaseOnlyOnce once;
	
	public ResponseFuture(int opaque, long timeoutMillis, InvokeCallback invokeCallback,SemaphoreReleaseOnlyOnce once) {
		this.invokeCallback = invokeCallback;
		this.opaque = opaque;
		this.timeoutMillis = timeoutMillis;
		this.once = once;
	}
	
	public RemotingCommand waitResponse(final long timeoutMillis) throws InterruptedException {
		this.countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
		return responseCommand;
	}
	
	public void executeInvokeCallback() {
		if (this.invokeCallback != null) {
			this.invokeCallback.operationComplete(this);
		}
	}

	public void release() {
		if (this.once != null) {
			this.once.release();
		}
	}
	
	public boolean isSendRequestOK() {
		return sendRequestOK;
	}

	public void setSendRequestOK(boolean sendRequestOK) {
		this.sendRequestOK = sendRequestOK;
	}
	
	public void putResponse(final RemotingCommand response) {
		this.responseCommand = response;
		this.countDownLatch.countDown();
	}

	public Throwable getCause() {
		return cause;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}

	public InvokeCallback getInvokeCallback() {
		return invokeCallback;
	}

	public RemotingCommand getResponseCommand() {
		return responseCommand;
	}

	public void setResponseCommand(RemotingCommand responseCommand) {
		this.responseCommand = responseCommand;
	}
	
}
