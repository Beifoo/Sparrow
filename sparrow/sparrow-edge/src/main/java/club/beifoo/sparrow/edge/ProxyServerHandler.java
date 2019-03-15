package club.beifoo.sparrow.edge;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;

public class ProxyServerHandler extends SimpleChannelInboundHandler<HttpObject> {
	private final Logger logger = LoggerFactory.getLogger(ProxyServerHandler.class);

	private EdgeProxyServer server;
	
	public ProxyServerHandler(EdgeProxyServer server) {
		this.server = server;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject obj) throws Exception {
		server.processRequest(ctx, obj);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		RequestHandler requestHandler = ctx.channel().attr(EdgeProxyServer.HANDLER_KEY).get();
		if (requestHandler != null) {
			//TODO 自定义逻辑还未实现
			requestHandler.channleClosed();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (cause instanceof IOException) {
			logger.warn(cause.getMessage());
		} else {
			logger.error("Proxy server handler error: ==>", cause);
		}
	}
}
