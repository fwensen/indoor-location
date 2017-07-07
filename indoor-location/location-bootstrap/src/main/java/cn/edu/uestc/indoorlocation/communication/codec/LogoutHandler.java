package cn.edu.uestc.indoorlocation.communication.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.edu.uestc.indoorlocation.common.Constant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class LogoutHandler extends ChannelInboundHandlerAdapter{

	private static Logger LOGGER = LoggerFactory.getLogger(LogoutHandler.class);
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object in) {
		
//		LOGGER.debug("LogoutHandler channelRead");
		JSONObject json = (JSONObject)in;
		
		if (json.getByte("type") == Constant.TYPE_LOGOUT) {
			
			JSONObject logout = new JSONObject();
			//登出成功,否则失败
			if (logout(json)) {
				logout.put("type", Constant.TYPE_RESPONSE_LOGIN_SUCCESS);
			} else {
				logout.put("type", Constant.TYPE_RESPONSE_LOGIN_FAIL);
			}
			ctx.write(logout);
		} else {
			ctx.fireChannelRead(in);
		}
	}

	/**
	 * 用户登出实现,当然这里可以以其他方式实现
	 * 如返回详细错误代码
	 * @param json
	 * @return 成功与否
	 */
	private boolean logout(JSONObject json) {
		return true;
	}
}
