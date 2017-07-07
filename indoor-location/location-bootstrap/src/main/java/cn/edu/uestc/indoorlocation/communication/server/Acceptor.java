package cn.edu.uestc.indoorlocation.communication.server;

/**
 * 服务器端实现通信的顶层接口
 * 在这之下可使用Netty，Mina等各种通信框架实现
 * 
 * 
 * 
 * 
 * @author vincent
 * @decription 服务器端顶层接口
 */
public interface Acceptor {
	
	/**
	 * 默认异步关闭
	 * @throws InterruptedException
	 */
	void start() throws InterruptedException;
	void start(boolean sync) throws InterruptedException;
	void shutdownGracefully();
}
