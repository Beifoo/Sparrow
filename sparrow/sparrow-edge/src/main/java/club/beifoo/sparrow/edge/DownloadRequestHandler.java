package club.beifoo.sparrow.edge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpRequest;

public class DownloadRequestHandler extends RequestHandler {
	private static final Logger logger = LoggerFactory.getLogger(DownloadRequestHandler.class);
	
	public DownloadRequestHandler(EdgeProxyServer server, ChannelHandlerContext ctx, DefaultHttpRequest request) {
		super(server, ctx, request);
	}

	@Override
	public void handleRequest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleContent(DefaultHttpContent content) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void channleClosed() {
		// TODO Auto-generated method stub
		
	}
}
