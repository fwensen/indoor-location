package cn.edu.uestc.indoorlocation.communication.codec;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.edu.uestc.indoorlocation.algorithm.Location;
import cn.edu.uestc.indoorlocation.common.Constant;
import cn.edu.uestc.indoorlocation.dao.model.Point;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

/**
 * 定位线程，同步获取发送成功与否的结果
 * @author vincent
 */

public class LocationThread implements Callable<Boolean>{

	private static Logger LOGGER = LoggerFactory.getLogger(LocationThread.class);
	
	private ChannelHandlerContext ctx;
	private JSONObject json;
	private CountDownLatch latch;
	private Location location;
//	private static Random random = new Random();
	public LocationThread(ChannelHandlerContext ctx, JSONObject json, 
							CountDownLatch latch, Location location) {
		this.ctx = ctx;
		this.json = json;
		this.latch = latch;
		this.location = location;
	}
	
	@Override
	public Boolean call() throws Exception {
		
		try {
			Point result = this.location.predict(json);
			if (result == null) {
				LOGGER.debug("SERVER: There is no RSS datas");
				return false;
			}
		
			JSONObject ret = new JSONObject();
			ret.put("type", Constant.TYPE_RESPONSE_LOCATION);
			ret.put("x", result.getX());
			ret.put("y", result.getY());
			ret.put("z", result.getZ());
			LOGGER.debug("SERVER: location result, x: {}, y: {}", result.getX(), result.getY());
			ChannelFuture future = ctx.writeAndFlush(ret).sync();
			if (!future.isSuccess()) {
				LOGGER.debug("SERVER: send result fail");
				return false;
			}
			return true;
		} catch (Exception e) {
			
			LOGGER.debug("SERVER: LocationThread exception found: {}", e);
			return false;
		} finally {
			this.latch.countDown();
		}
	}
}
