package cn.edu.uestc.indoorlocation.communication.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.edu.uestc.indoorlocation.common.Constant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * ####################  暂时不用   ##########
 * 
 * @author vincent
 *
 */
public class MessageHandler extends ChannelInboundHandlerAdapter{

	private static Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object in) throws Exception {
		
//		LOGGER.debug("MessageHandler channelRead");
		JSONObject json = (JSONObject)in;
		
//		if (json.getByte("type") == Constant.TYPE_COMMON_MSG) {
//			handleCommonMsg(ctx, json);
//			
//		} else {
//			ctx.fireChannelRead(in);
//		}
	}
	
	/**
	 * 处理普通消息
	 * @param ctx
	 * @param json
	 */
	private void handleCommonMsg(ChannelHandlerContext ctx, JSONObject json) {

//		LOGGER.debug("SERVER recved json: {}", json.toJSONString());
		//test
//		JSONObject oo = new JSONObject();
//		oo.put("value", "nice to meet you");
//		ctx.writeAndFlush(oo);
	}
}
