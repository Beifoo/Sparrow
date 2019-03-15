package club.beifoo.sparrow.edge;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

public class IllegalRequestHandler extends RequestHandler {
	public IllegalRequestHandler(EdgeProxyServer server, ChannelHandlerContext ctx, DefaultHttpRequest request) {
		super(server, ctx, request);
	}

	@Override
	public void handleRequest() {
		sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
	}

	@Override
	public void handleContent(DefaultHttpContent content) {}

	@Override
	public void channleClosed() {}

}
