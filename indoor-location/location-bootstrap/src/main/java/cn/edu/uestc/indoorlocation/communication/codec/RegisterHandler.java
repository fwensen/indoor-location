package cn.edu.uestc.indoorlocation.communication.codec;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.edu.uestc.indoorlocation.common.Constant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 
 * 注册的Handler
 * 这个Handler不仅仅是注册的功能，同时它还提示链路的建立以及监控链路是否Active、捕捉异常
 * 注册实现：注册账号需要以下内容:
 * 	用户名、密码、邮箱(可选)
 * 注册成功后会回复注册成功的json内容
 * 
 * @author vincent
 *
 */
public class RegisterHandler extends ChannelInboundHandlerAdapter{

	
	private static Logger LOGGER = LoggerFactory.getLogger(RegisterHandler.class);
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object in) {
		
//		LOGGER.debug("RegisterHandler channelRead");
		JSONObject json = (JSONObject)in;
		LOGGER.debug("SERVER recved json: " + json.toJSONString());
		//如果是注册请求
		if (json.getByte("type") == Constant.TYPE_REGISTER) {
			
			JSONObject succ = new JSONObject();
			//注册用户成功时
			if (registerUserSucc(json)) {
				succ.put("type", Constant.TYPE_ERSPONSE_REGISTER_SUCCESS);
			} else {
				succ.put("type", Constant.TYPE_RESPONSE_REGISTER_FALIL);
			}
			ctx.writeAndFlush(succ);
		} else {
			ctx.fireChannelRead(in);
		}
	}

	/**
	 * 注册用户实现
	 * @param json
	 * @return
	 */
	private boolean registerUserSucc(JSONObject json) {
		
		return true;
	}
}
