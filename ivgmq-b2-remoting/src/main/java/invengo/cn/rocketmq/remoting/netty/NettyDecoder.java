package invengo.cn.rocketmq.remoting.netty;

import java.nio.ByteBuffer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import invengo.cn.rocketmq.remoting.common.RemotingHelper;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NettyDecoder extends LengthFieldBasedFrameDecoder{
	
	private static Logger logger = LogManager.getLogger(NettyDecoder.class);

	private static final int FRAME_MAX_LENGTH = 1048576;  // 16M
	
	public NettyDecoder() {
		// maxFrameLength lengthFieldOffset lengthFieldLength lengthAdjustment initialBytesToStrip 
		super(FRAME_MAX_LENGTH, 0, 4, 0, 4);
	}
	
	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		ByteBuf frame = null;
		try {
			frame = (ByteBuf) super.decode(ctx, in);
			if (frame == null) {
				return null;
			}
			ByteBuffer byteBuffer = frame.nioBuffer();
			return RemotingCommand.decode(byteBuffer);
		} catch (Exception e) {
			logger.error("decode exception,"+RemotingHelper.parseSocketAddressAddr(ctx.channel().localAddress()), e);
		}finally {
			if (frame != null) {
				frame.release();
			}
		}
		
		return null;
	}
}
