package cn.edu.uestc.indoorlocation.communication.codec;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.edu.uestc.indoorlocation.communication.message.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 登录，权限验证,要想得到定位消息，首先都要经过该Handler
 * @author vincent
 */
public class AuthDecoder extends ChannelInboundHandlerAdapter{

	private static Logger LOGGER = LoggerFactory.getLogger(AuthDecoder.class);
	
	private static Map<InetSocketAddress, Message> users = new ConcurrentHashMap<>();
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object in) {
		
//		LOGGER.debug("AuthDecoder channelRead");
		JSONObject json = (JSONObject)in;
		//验证通过时
		if (isAuthAccess(json)) {
			LOGGER.info("SERVER: Auth access");
//			ctx.fireChannelRead(in);
		//验证失败时
		} else {
			
//			ctx.fireChannelRead(in);
			InetSocketAddress remote = (InetSocketAddress)ctx.channel().remoteAddress();
			LOGGER.error("SERVER: {}:{} auth failed!",remote.getHostName(), remote.getPort());
			//response failed message
			//TODO 如向客户端返回权限不足等相关消息
		}
	}
	
	/**
	 * 如果不实现具体用户服务，就直接返回true
	 * @param json
	 * @return 验证成功/失败
	 */
	private boolean isAuthAccess(JSONObject json) {

		//String sessionId = json.getString(Constant.SESSION_ID);
		//TODO
		return true;
	}
}
