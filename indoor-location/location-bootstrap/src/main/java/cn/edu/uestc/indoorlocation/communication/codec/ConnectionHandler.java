package cn.edu.uestc.indoorlocation.communication.codec;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 连接管理的Handler
 * @author vincent
 *
 */
public class ConnectionHandler  extends ChannelInboundHandlerAdapter{

	private static Logger LOGGER = LoggerFactory.getLogger(ConnectionHandler.class);
	
	/**
	 * 与客户端建立连接时
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		
		InetSocketAddress remote = (InetSocketAddress) ctx.channel().remoteAddress();
		LOGGER.debug("SERVER: connect from: {}:{}", remote.getHostName(), remote.getPort());
	}
	
	/**
	 * 与客户端断开连接时,
	 * 这里还应该实现用户断开时将相关的登录状态记为离线\
	 * 
	 * TODO
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) {

		InetSocketAddress remote = (InetSocketAddress) ctx.channel().remoteAddress();
		LOGGER.debug("SERVER: disconnect from: {}:{}", remote.getHostName(), remote.getPort());
		//TODO
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
