package invengo.cn.rocketmq.namesrv.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import invengo.cn.rocketmq.common.protocol.RequestCode;
import invengo.cn.rocketmq.common.protocol.body.TopicConfigSerializeWrapper;
import invengo.cn.rocketmq.common.protocol.header.namesrv.RegisterBrokerRequestHeader;
import invengo.cn.rocketmq.namesrv.NamesrvController;
import invengo.cn.rocketmq.remoting.netty.NettyRequestProcessor;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import invengo.cn.rocketmq.remoting.protocol.RemotingSycResponseCode;
import io.netty.channel.ChannelHandlerContext;

public class DefaultRequestProcessor implements NettyRequestProcessor{

	private static Logger logger = LogManager.getLogger(DefaultRequestProcessor.class);
	
	private final NamesrvController namesrvController;
	
	public DefaultRequestProcessor(NamesrvController namesrvController) {
		this.namesrvController = namesrvController;
	}
	
	public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {
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
		logger.debug("request: "+request);
		RegisterBrokerRequestHeader requestHeader = (RegisterBrokerRequestHeader) request.decodeCustomHeader(RegisterBrokerRequestHeader.class);
		logger.debug("requestHeader: "+requestHeader);
		TopicConfigSerializeWrapper topicConfigSerializeWrapper = null;
		if (request.getBody() != null) {
			topicConfigSerializeWrapper = TopicConfigSerializeWrapper.decode(request.getBody(), TopicConfigSerializeWrapper.class);
		}
		this.namesrvController.getRouteInfoManager().registerBroker(
				requestHeader.getClusterName(), 
				requestHeader.getBrokerAddr(),
				requestHeader.getBrokerName(), 
				requestHeader.getBrokerId(), 
				topicConfigSerializeWrapper);
		RemotingCommand response = RemotingCommand.createResponseCommand(RemotingSycResponseCode.SUCCESS);
		response.setOpaque(request.getOpaque());
		logger.debug("response: "+response);

		return response;
	}

}
