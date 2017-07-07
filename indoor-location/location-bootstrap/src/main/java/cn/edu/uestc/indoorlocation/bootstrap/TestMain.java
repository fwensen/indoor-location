package cn.edu.uestc.indoorlocation.bootstrap;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.edu.uestc.indoorlocation.algorithm.common.LoadPaths;
import cn.edu.uestc.indoorlocation.algorithm.common.LoadWallInfo;
import cn.edu.uestc.indoorlocation.communication.server.DefaultNettyServerAcceptor;

/**
 * 测试类
 * @author vincent
 *
 */
public class TestMain {

	private static Logger LOGGER = LoggerFactory.getLogger(TestMain.class);
	
	public static void main(String[] args) throws InterruptedException {
		
		/**
		 * 这里为什么使用CountDownLatch呢？因为若不使用，则在服务器端启动时，
		 * 可能墙体信息还未读取完成，从而造成错误
		 */
		CountDownLatch latch = new CountDownLatch(2);
		LoadWallInfo.initWallsInfo("wallinfo.xml",latch);
		LoadPaths.loadStaticPath("paths.xml", latch);
		latch.await();
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-context.xml");
		DefaultNettyServerAcceptor server  = (DefaultNettyServerAcceptor) ctx.getBean("defaultServer");
		server.start();  //start
	}
}