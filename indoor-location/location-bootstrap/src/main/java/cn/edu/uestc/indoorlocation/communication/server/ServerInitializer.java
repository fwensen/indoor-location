package cn.edu.uestc.indoorlocation.communication.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.uestc.indoorlocation.algorithm.Location;
import cn.edu.uestc.indoorlocation.algorithm.knn.KNNLocation;
import cn.edu.uestc.indoorlocation.communication.codec.AuthDecoder;
import cn.edu.uestc.indoorlocation.communication.codec.ByteToJsonDecoder;
import cn.edu.uestc.indoorlocation.communication.codec.ConnectionHandler;
import cn.edu.uestc.indoorlocation.communication.codec.JsonToByteEncoder;
import cn.edu.uestc.indoorlocation.communication.codec.LocationHandler;
import cn.edu.uestc.indoorlocation.communication.codec.LoginHandler;
import cn.edu.uestc.indoorlocation.communication.codec.LogoutHandler;
import cn.edu.uestc.indoorlocation.communication.codec.MessageHandler;
import cn.edu.uestc.indoorlocation.communication.codec.RegisterHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel>{

	private static Logger LOGGER = LoggerFactory.getLogger(ServerInitializer.class);
	private Location location;
	
	public void setLocation(Location l) {
		this.location = l;
	}
	
////	MessageDecoder messageDecoder;
//	private ByteToJsonDecoder byteToJsonDecoder;
//	private JsonToByteEncoder jsonToByteEncoder;
//	private AuthDecoder authDecoder;
//	private MessageHandler messageHandler;
//	private LocationHandler locationHandler;
////	MessageEncoder messageEncoder;
//	
////	public void setMessageDecoder(MessageDecoder decoder) {
////		this.messageDecoder = decoder;
////	}
//	
//	public void setByteToJsonDecoder(ByteToJsonDecoder decoder) {
//		this.byteToJsonDecoder = decoder;
//	}
//	
//	public void setJsonToByteEncoder(JsonToByteEncoder encoder) {
//		this.jsonToByteEncoder = encoder;
//	}
//	
//	public void setAuthDecoder(AuthDecoder decoder) {
//		this.authDecoder = decoder;
//	}
//	
//	public void setMessageHandler(MessageHandler decoder) {
//		this.messageHandler = decoder;
//	}
//	
//	public void setLocationHandler(LocationHandler handler) {
//		this.locationHandler = handler;
//		LOGGER.debug("void setLocationHandler(LocationHandler handler)");
//	}
	
	/**
	 * 注意coder和encoder添加的顺序
	 * 在InboundHandler执行完成需要调用Outbound的时候，
	 * 比如ChannelHandlerContext.write()方法，Netty是直接从该InboundHandler
	 * 返回逆序的查找该InboundHandler之前的OutboundHandler，
	 * 并非从Pipeline的最后一项Handler开始查找
	 * 见：http://blog.csdn.net/wgyvip/article/details/25637651
	 */
	@Override
	protected void initChannel(SocketChannel channel) throws Exception {

		ChannelPipeline pipeline = channel.pipeline();
		pipeline.addLast(new JsonObjectDecoder());   //将识别json字节流
		pipeline.addLast(new ByteToJsonDecoder());   //将字节流转化为JSON对象
		
		pipeline.addLast(new JsonToByteEncoder());   //将JSON对象转化为字节流的Encoder
		
		pipeline.addLast(new ConnectionHandler());      //处理连接、异常
		pipeline.addLast(new LocationHandler(location));     //定位请求
		
		pipeline.addLast(new LoginHandler());        //登录
		pipeline.addLast(new LogoutHandler());       //登出
		pipeline.addLast(new RegisterHandler());     //注册
		
		pipeline.addLast(new AuthDecoder());         //验证权限
		

//		pipeline.addLast(byteToJsonDecoder);   //将字节流转化为JSON对象
//		pipeline.addLast(jsonToByteEncoder);   //将JSON对象转化为字节流的Encoder
//		pipeline.addLast(authDecoder);         //验证/登录/登出
//		pipeline.addLast(messageHandler);      //处理消息(RSS, 方向，加速度)
//		pipeline.addLast(locationHandler);     //定位请求
		
		LOGGER.info("SERVER pipeline.addLast()");
	}
}