package club.beifoo.sparrow.edge;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

public abstract class RequestHandler {
	private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class); 
	
	public abstract void handleRequest();
	public abstract void handleContent(DefaultHttpContent content);
	public abstract void channleClosed();
	
	public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
	public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
	//TODO 考虑是不是要写死
	public static final int HTTP_CACHE_SECONDS = 60;
	//
	protected EdgeProxyServer server;
	protected ChannelHandlerContext ctx; 
	protected DefaultHttpRequest request; 
	protected File requestFile;
	protected FilterContext filterCtx;
	
	public RequestHandler(EdgeProxyServer server, ChannelHandlerContext ctx, DefaultHttpRequest request) {
		this.server = server;
		this.ctx = ctx;
		this.request = request;
	}
	
	protected boolean filter() throws Exception {
		if (!request.decoderResult().isSuccess()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Bad http request: request decode error.");
			}
			sendError(ctx, HttpResponseStatus.BAD_REQUEST);
			return false;
		}
		String[] uriSplits = request.uri().split("/");
		if (uriSplits.length==0 || !server.getNamespaces().contains(uriSplits[0])) {
			if (logger.isDebugEnabled()) {
				logger.debug("Bad http request: request uri illegal.");
			}
			sendError(ctx, HttpResponseStatus.BAD_REQUEST);
			return false;
		}
		requestFile = new File(server.getGlobalHomeDir(), request.uri());
		VirtualAgent agent = server.getAgent(uriSplits[0]);
		if (null != agent.getRequestFilter()) {
			filterCtx = new FilterContext(requestFile);
			filterCtx.req = request;
			agent.getRequestFilter().filter(filterCtx);
			if (FilterContext.CODE_OK != filterCtx.errCode) {
				if(logger.isDebugEnabled()){
					logger.debug("Request filter {} reject.", agent.getRequestFilter());
				}
				sendError(ctx, filterCtx.errCode, filterCtx.rspHeaderMap);
				return false;
			}
		}
		return true;
	}
	
	public static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
		logger.warn("send status {}", ctx.channel(), status);
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(
						status + "\r\n", CharsetUtil.UTF_8));
		response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
		// Close the connection as soon as the error message is sent.
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	public static void sendError(ChannelHandlerContext ctx, int statusCode, Map<String,String>rspHeaders) {
		HttpResponseStatus status = HttpResponseStatus.valueOf(statusCode);
		logger.warn("send status {}", ctx.channel(), statusCode);
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(
						status + "\r\n", CharsetUtil.UTF_8));
		rspHeaders.forEach((k,v) -> {
			response.headers().set(k, v);
		});
		response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
		// Close the connection as soon as the error message is sent.
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
}
