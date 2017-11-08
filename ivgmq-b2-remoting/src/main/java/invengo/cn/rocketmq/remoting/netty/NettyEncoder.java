package invengo.cn.rocketmq.remoting.netty;

import java.nio.ByteBuffer;

import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyEncoder extends MessageToByteEncoder<RemotingCommand>{

	
	@Override
	protected void encode(ChannelHandlerContext ctx, RemotingCommand msg, ByteBuf out) throws Exception {
		try {
			ByteBuffer header = msg.encodeHeader();
			out.writeBytes(header);
		} catch (Exception e) {
			
		}
		
	}

}
