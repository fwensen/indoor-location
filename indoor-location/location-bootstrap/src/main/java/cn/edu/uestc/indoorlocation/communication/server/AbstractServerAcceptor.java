package cn.edu.uestc.indoorlocation.communication.server;

import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.internal.PlatformDependent;

/**
 * 
 * @author vincent
 * @description 抽象类,服务端的Netty实现
 *
 */
public abstract class AbstractServerAcceptor implements Acceptor{

	private static Logger LOGGER = LoggerFactory.getLogger(AbstractServerAcceptor.class);
	private static final int AVILABLE_PROCESSORS =
			Runtime.getRuntime().availableProcessors();
	
	private ServerBootstrap bootstrap;
	private EventLoopGroup boss;
	private EventLoopGroup worker;
	//worker数量
	private int nWorkers;
	private SocketAddress localAddress;
	protected volatile ByteBufAllocator allocator;
	
	public AbstractServerAcceptor(SocketAddress localAddress) {
		this(localAddress, AVILABLE_PROCESSORS << 1);
	}
	
	public AbstractServerAcceptor(SocketAddress localAddress, int works) {
		this.localAddress = localAddress;
		this.nWorkers = works;
	}
	
	protected void init() {
		boss = new NioEventLoopGroup();
		worker = new NioEventLoopGroup();
		/**
		 * 使用Pooled Direct Buffer
		 */
		allocator = new PooledByteBufAllocator(PlatformDependent.directBufferPreferred());
		bootstrap = new ServerBootstrap();
		bootstrap.group(boss, worker).childOption(ChannelOption.ALLOCATOR, allocator);
	}
	
	/**
	 * 默认异步关闭
	 */
	@Override
	public void start() throws InterruptedException {
		start(true);
	}

	@Override
	public void start(boolean sync) throws InterruptedException {
		ChannelFuture future = bind(localAddress).sync();
		LOGGER.info("netty acceptor server start");

        if (sync) {
            future.channel().closeFuture().sync();
        }
	}

	public SocketAddress localAddress() {
		 return localAddress;
	}
	    
	protected ServerBootstrap bootstrap() {
		return bootstrap;
	}
	
	@Override
	public void shutdownGracefully() {
		boss.shutdownGracefully();
		worker.shutdownGracefully();
	}
	//bind
	protected abstract ChannelFuture bind(SocketAddress localAddress);
}

