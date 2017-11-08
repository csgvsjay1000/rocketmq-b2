package invengo.cn.rocketmq.namesrv.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import invengo.cn.rocketmq.common.protocol.RequestCode;
import invengo.cn.rocketmq.namesrv.NamesrvController;
import invengo.cn.rocketmq.remoting.netty.NettyRequestProcessor;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

public class DefaultRequestProcessor implements NettyRequestProcessor{

	private static Logger logger = LogManager.getLogger(DefaultRequestProcessor.class);
	
	private final NamesrvController namesrvController;
	
	public DefaultRequestProcessor(NamesrvController namesrvController) {
		this.namesrvController = namesrvController;
	}
	
	public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {
		logger.info(request.getRemark());
		switch (request.getCode()) {
		case RequestCode.REGISTER_BROKER:
			return this.registerBroker(ctx, request);

		default:
			break;
		}
		return request;
	}

	public boolean rejectRequest() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private RemotingCommand registerBroker(ChannelHandlerContext ctx, RemotingCommand request){
		
		return null;
	}

}
