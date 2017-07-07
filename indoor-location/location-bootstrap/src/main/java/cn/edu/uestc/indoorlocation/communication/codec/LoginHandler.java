package cn.edu.uestc.indoorlocation.communication.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.edu.uestc.indoorlocation.common.Constant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 登录账户的Handler
 * 登录账号会先查看是否存在该用户，若存在则查看密码是否正确。
 * 如果密码正确则记录该用户登录成功，这样在AuthHandler处就能明白该用户已经登录,
 * 
 * @author vincent
 *
 */
public class LoginHandler extends ChannelInboundHandlerAdapter{

	private static Logger LOGGER = LoggerFactory.getLogger(LoginHandler.class);
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object in) {
		
//		LOGGER.debug("LoginHandler channelRead");
		JSONObject json = (JSONObject)in;
		
		if (json.getByte("type") == Constant.TYPE_LOGIN) {
			
			JSONObject login = new JSONObject();
			//登录成功,否则失败
			if (login(json)) {
				login.put("type", Constant.TYPE_RESPONSE_LOGIN_SUCCESS);
			} else {
				login.put("type", Constant.TYPE_RESPONSE_LOGIN_FAIL);
			}
			ctx.write(login);
			
		} else {
			ctx.fireChannelRead(in);
		}
	}
	
	/**
	 * 用户登录实现,当然这里可以以其他方式实现
	 * 如返回详细错误代码
	 * @param json
	 * @return
	 */
	private boolean login(JSONObject json) {
		return true;
	}
}
