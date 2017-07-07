package cn.edu.uestc.indoorlocation.communication.codec;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * 将ByteBuf字节流转化成JSON对象
 * @author vincent
 *
 */
public class ByteToJsonDecoder extends MessageToMessageDecoder<ByteBuf>{
	
	private static Logger LOGGER = LoggerFactory.getLogger(ByteToJsonDecoder.class);
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

		ByteBuf bf = (ByteBuf)in;
		byte[] bytes = new byte[bf.readableBytes()];
		bf.readBytes(bytes);
		String obj = new String(bytes);
		JSONObject json = JSONObject.parseObject(obj);
		if (json != null) out.add(json);
	}
}
