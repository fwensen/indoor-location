package cn.edu.uestc.indoorlocation.communication.codec;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.edu.uestc.indoorlocation.algorithm.Location;
import cn.edu.uestc.indoorlocation.common.Constant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 用于处理定位请求
 * @author vincent
 *
 */
public class LocationHandler extends ChannelInboundHandlerAdapter{

	private static final Logger LOGGER = LoggerFactory.getLogger(LocationHandler.class);
	private static Random random = new Random();
	private final Location location;
//	ReferenceCountUtil.release(msg);
	
	public LocationHandler(Location location) {
		LOGGER.debug("**************** LocationHandler ****************");
		this.location = location;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object in) {

//		LOGGER.debug("LocationHandler channelRead");
		InetSocketAddress remote = (InetSocketAddress)ctx.channel().remoteAddress();
		JSONObject json = (JSONObject)in;
		LOGGER.debug("SERVER RECV FROM {}:{},  JSON: {}", 
				remote.getAddress(), remote.getPort() , json.toJSONString());
		
		if (json.getByte("type") == Constant.TYPE_LOCATION) {
			
			//test
//			JSONObject rt = new JSONObject();
//			rt.put("type", Constant.TYPE_RESPONSE_LOCATION);
//			rt.put("x", random.nextInt(1000));
//			rt.put("y", random.nextInt(2000));
//			ctx.writeAndFlush(rt);
			handleLocationMessage(ctx, json);
		} else {
			ctx.fireChannelRead(in);
		}
	}
	
	/**
	 * 处理定位请求信息
	 * 取出json中的mac rss及加速度等信息
	 * 调用algorithm模块处理这些信息，然后得到定位结果
	 * 该算法模块会在多线程内调用(LocationThread)
	 * 
	 * 返回定位结果给客户端
	 * 由于定位算法处理需要计算时间，故这里应该使用多线程方式处理定位
	 * @param ctx
	 * @param json
	 */
	private void handleLocationMessage(ChannelHandlerContext ctx, JSONObject json) {
		
		//使用的是Netty自带的工作线程池，若使用自己的线程，会导致效率不高
		//ExecutorService exc = Executors.newFixedThreadPool(2);
		final CountDownLatch latch = new CountDownLatch(1);
		final long currentMillis = System.currentTimeMillis();
		final Future<Boolean> future = ctx.channel().eventLoop().submit(
				new LocationThread(ctx, json, latch, this.location));
		
		//final Future<Boolean> future = exc.submit(new LocationThread(ctx, json, latch, this.location));
		ctx.channel().eventLoop().submit(new Runnable() {
			
			@Override
			public void run() {
				try {
					//此处获取发送的同步结果
					latch.await();
					boolean ret = future.get();
					LOGGER.debug("LOCATION escaped: {}", System.currentTimeMillis()-currentMillis);
					if (ret == true) {
					} else {
						LOGGER.debug("SERVER location error or send error");
						JSONObject rt = new JSONObject();
						rt.put("type", Constant.TYPE_RESPONSE_LOCATION_FAIL);
						ctx.writeAndFlush(rt);
					}
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		});
		//exc.shutdown();
	}
}
