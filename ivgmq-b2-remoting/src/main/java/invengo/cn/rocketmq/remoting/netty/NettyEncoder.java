package invengo.cn.rocketmq.remoting.netty;

import java.nio.ByteBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyEncoder extends MessageToByteEncoder<RemotingCommand>{

	private static Logger logger = LogManager.getLogger(NettyEncoder.class);
	
	@Override
	protected void encode(ChannelHandlerContext ctx, RemotingCommand msg, ByteBuf out) throws Exception {
		try {
			ByteBuffer header = msg.encodeHeader();
			out.writeBytes(header);
		} catch (Exception e) {
			logger.error("encode exception. ", e);
			if (msg != null) {
				logger.error(msg.toString());
			}
		}
		
	}

}
