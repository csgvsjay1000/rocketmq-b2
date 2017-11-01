package invengo.cn.rocketmq.remoting;

import invengo.cn.rocketmq.remoting.netty.ResponseFuture;

public interface InvokeCallback {

	void operationComplete(final ResponseFuture responseFuture);
	
}
