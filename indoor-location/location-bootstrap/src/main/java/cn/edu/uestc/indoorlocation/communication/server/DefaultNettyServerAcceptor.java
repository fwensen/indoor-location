package cn.edu.uestc.indoorlocation.communication.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class DefaultNettyServerAcceptor extends AbstractServerAcceptor{

	private static Logger LOGGER = LoggerFactory.getLogger(DefaultNettyServerAcceptor.class);
	
	private ServerInitializer initializer;
	public void setInitializer(ServerInitializer init) {
		this.initializer = init;
	}
	
	public DefaultNettyServerAcceptor(int port) {
		super(new InetSocketAddress(port));
		this.init();
		LOGGER.info("SERVER init at {}", port);
	}

	@Override
	protected void init() {
		super.init();
		bootstrap().option(ChannelOption.SO_BACKLOG, 32768)
		.option(ChannelOption.SO_REUSEADDR, true)
		.childOption(ChannelOption.SO_REUSEADDR, true)
		.childOption(ChannelOption.SO_KEEPALIVE, true)
		.childOption(ChannelOption.TCP_NODELAY, true)
		.childOption(ChannelOption.ALLOW_HALF_CLOSURE, false);
	}
	
	@Override
	protected ChannelFuture bind(SocketAddress localAddress) {

		bootstrap().channel(NioServerSocketChannel.class)
		.childHandler(initializer);
		return bootstrap().bind(localAddress);
	}
}