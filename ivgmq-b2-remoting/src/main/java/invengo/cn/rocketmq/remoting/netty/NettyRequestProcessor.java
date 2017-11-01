package invengo.cn.rocketmq.remoting.netty;

import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

public interface NettyRequestProcessor {

	RemotingCommand processRequest(ChannelHandlerContext ctx,RemotingCommand request);
	
	boolean rejectRequest();
	
}
