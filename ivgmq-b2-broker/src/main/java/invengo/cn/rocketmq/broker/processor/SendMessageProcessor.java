package invengo.cn.rocketmq.broker.processor;

import invengo.cn.rocketmq.remoting.netty.NettyRequestProcessor;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

public class SendMessageProcessor implements NettyRequestProcessor{

	public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean rejectRequest() {
		// TODO Auto-generated method stub
		return false;
	}

}
