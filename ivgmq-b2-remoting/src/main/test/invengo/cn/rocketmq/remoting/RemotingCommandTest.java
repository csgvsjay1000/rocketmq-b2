package invengo.cn.rocketmq.remoting;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.TooLongFrameException;


public class RemotingCommandTest {

	public static void main(String[] args) {
		RemotingCommand command = RemotingCommand.createRequestCommand(2, null);
		System.out.println(command.getCode());
		ByteBuffer byteBuf = command.encodeHeader();
		RemotingCommand response =  RemotingCommand.decode(byteBuf);
		System.out.println(response.getCode());

	}
	
	
	@Test
	public void testCmdEncode() {
		
		RemotingCommandV1_1 request = new RemotingCommandV1_1();
		request.setCode(5);
		request.setBody("你好".getBytes());
		ByteBuffer byteBuf = request.encodeHeader();
		
		RemotingCommandV1_1 response =  RemotingCommandV1_1.decode(byteBuf);
		System.out.println(response.getCode());
		System.out.println(new String(response.getBody()));
		
	}
	
	@Test
	public void testLengthFieldFrame() {
		ByteBuf buf = Unpooled.buffer();
        buf.writeInt(32);
        for (int i = 0; i < 32; i++) {
            buf.writeByte(i);
        }
        buf.writeInt(1);
        buf.writeByte('a');
        EmbeddedChannel channel = new EmbeddedChannel(new LengthFieldBasedFrameDecoder(16, 0, 4,0,0));
        try {
            channel.writeInbound(buf);
            Assert.fail();
        } catch (TooLongFrameException e) {
            // expected
        }
        Assert.assertTrue(channel.finish());

        ByteBuf b = channel.readInbound();
        Assert.assertEquals(5, b.readableBytes());
        Assert.assertEquals(1, b.readInt());
        Assert.assertEquals('a', b.readByte());
        b.release();

        Assert.assertNull(channel.readInbound());
        channel.finish();
	}
	
	@Test
	public void testLengthFieldFrameCustom() {
		ByteBuf buf = Unpooled.buffer();
        buf.writeInt(32);
        /*for (int i = 0; i < 32; i++) {
            buf.writeByte(i);
        }*/
        buf.writeInt(1);
        buf.writeByte('a');
        EmbeddedChannel channel = new EmbeddedChannel(new LengthFieldBasedFrameDecoder(1024, 0, 4));
        try {
            channel.writeInbound(buf);
            //Assert.fail();
        } catch (TooLongFrameException e) {
            // expected
        	e.printStackTrace();
        }
        Assert.assertTrue(channel.finish());

        ByteBuf b = channel.readInbound();
        Assert.assertEquals(36, b.readableBytes());
        Assert.assertEquals(32, b.readInt());
        Assert.assertEquals(0, b.readByte());
        b.release();

        Assert.assertNull(channel.readInbound());
        channel.finish();
	}
	
}
