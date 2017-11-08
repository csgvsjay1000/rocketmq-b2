package invengo.cn.rocketmq.namesrv.processor;

import invengo.cn.rocketmq.namesrv.NamesrvController;
import invengo.cn.rocketmq.remoting.netty.NettyRequestProcessor;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

public class DefaultRequestProcessor implements NettyRequestProcessor{

	private final NamesrvController namesrvController;
	
	public DefaultRequestProcessor(NamesrvController namesrvController) {
		this.namesrvController = namesrvController;
	}
	
	public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean rejectRequest() {
		// TODO Auto-generated method stub
		return false;
	}

}
