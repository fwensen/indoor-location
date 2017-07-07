package cn.edu.uestc.indoorlocation.communication.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.concurrent.Future;
/**
 * 将JSON对象转化成ByteBuf
 * 使用fastjson框架
 * @author lenovo
 *
 */
@Sharable
public class JsonToByteEncoder extends MessageToByteEncoder<JSONObject>{
	
	private static Logger LOGGER = LoggerFactory.getLogger(JsonToByteEncoder.class);
	
	@Override
	protected void encode(ChannelHandlerContext ctx, JSONObject in, ByteBuf out) throws Exception {

		String outString = in.toJSONString();
		ByteBuf bytebuf = Unpooled.copiedBuffer(outString.getBytes());
		ctx.writeAndFlush(bytebuf).addListener(new ChannelFutureListener(){

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					bytebuf.release();
					LOGGER.debug("SERVER send completed");
				} else {
					future.cause().printStackTrace();
					future.channel().close();
				}
			}
			
		});
	}

}
