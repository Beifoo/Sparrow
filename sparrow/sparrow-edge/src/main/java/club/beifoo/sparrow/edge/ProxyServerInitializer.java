package club.beifoo.sparrow.edge;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ProxyServerInitializer extends ChannelInitializer<Channel>{
	
	private EdgeProxyServer server;
	
	public ProxyServerInitializer(EdgeProxyServer server) {
		this.server = server;
	}

	@Override
	protected void initChannel(Channel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();
		//TODO SSL 暂不考虑
		//SSLEngine engine = 
		//engine.setUseClientMode(false); //这里看一下是否要设置
		//pipeline.addLast("ssl", new SslHandler(engine));
		pipeline.addLast("http", new HttpServerCodec());
		pipeline.addLast("chunk", new ChunkedWriteHandler());
		pipeline.addLast("proxy", new ProxyServerHandler(this.server));
	}
}
