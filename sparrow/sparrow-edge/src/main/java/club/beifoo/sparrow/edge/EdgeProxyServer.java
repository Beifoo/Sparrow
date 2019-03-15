package club.beifoo.sparrow.edge;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpClientConfig.Builder;
import com.ning.http.client.providers.netty.NettyAsyncHttpProviderConfig;

import club.beifoo.sparrow.common.core.Invoker;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.AttributeKey;

public class EdgeProxyServer {
	private static final Logger logger = LoggerFactory.getLogger(EdgeProxyServer.class); 
	//
	private String proxyServerName;
	//
	private boolean started;
	private int port;
	private int sslPort;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	//
	private Map<String, VirtualAgent> namespaceAgentMap;
	private Map<String, FileOpt> requestFileoptMap;
	//
	private AsyncHttpClient asyncHttpClient;
	private AsyncHttpClientConfig.Builder clientConfigBuilder;
	private AsyncHttpClientConfig clientConfig;
	//TODO 关注一下可能没用
	private DirectioryPrinter directioryPrinter;
	private File globalHomeDir;
	//ConcurrentHashMap<String,DownloadOrgin> fetchMap;
	//ConcurrentHashMap<String,FileDownload> firstMap;
	private VirtualAgentController agentController;
	//
	private Invoker ioInvoker;
	private Invoker requestHandleInvoker;
	private Invoker contentHandleInvoker;
	private Method  requestHandleMethod;
	private Method  contentHandleMethod;
	public static final AttributeKey<RequestHandler> HANDLER_KEY = AttributeKey.valueOf("REQUEST_HANDLER");
	
	public EdgeProxyServer(String proxyServerName, String globalHomeDir) {
		this.started = false;
		this.port = 80;
		this.sslPort = 443;
		this.proxyServerName = proxyServerName;
		this.namespaceAgentMap = new ConcurrentHashMap<String, VirtualAgent>();
		this.requestFileoptMap = new ConcurrentHashMap<String, FileOpt>();
		this.clientConfig = clientConfigBuilder.build();
		this.asyncHttpClient = new AsyncHttpClient(clientConfig);
		this.clientConfigBuilder = new Builder();
		this.clientConfigBuilder.setUserAgent(proxyServerName);
		this.clientConfigBuilder.setAsyncHttpClientProviderConfig(new NettyAsyncHttpProviderConfig());
		this.directioryPrinter = new HtmlDirectoryPrinter();
		//TODO this.requestFilter =
		this.agentController = new ConsulVirtualAgentController();
		this.ioInvoker = new Invoker("IOHandler", "IOThread", 
				Runtime.getRuntime().availableProcessors()*2+1);
		this.requestHandleInvoker = new Invoker("RequestHandler", "WorkThread");
		this.contentHandleInvoker = new Invoker("ContentHandler", "WorkThread");
		this.requestHandleMethod = Invoker.getMethod(RequestHandler.class, "handleRequest");
		this.contentHandleMethod = Invoker.getMethod(RequestHandler.class, "handleContent", DefaultHttpContent.class);
	}
	
	private void initNetty() throws Exception {
		this.started = true;
		//TODO 这里需要参数调整
		bossGroup = new NioEventLoopGroup(1, ioInvoker);
		workerGroup = new NioEventLoopGroup(0, ioInvoker);
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
		 .childHandler(new ProxyServerInitializer(this));
		//TODO 考虑如何绑定2个端口
		b.bind(port).sync();
	}
	
	private void initVirtualAgent() {
		agentController.initAgentMetadata(this);
		for (Entry<String, VirtualAgent> e : namespaceAgentMap.entrySet()) {
			agentController.initAgentHomeDir(e.getKey());
		}
	}
		
	void processRequest(ChannelHandlerContext ctx, HttpObject obj) {
		if (obj instanceof DefaultHttpRequest) {
			
		}
		if (obj instanceof DefaultHttpContent) {
			RequestHandler requestHandler = ctx.channel().attr(EdgeProxyServer.HANDLER_KEY).get();
			DefaultHttpContent content = (DefaultHttpContent) obj;
			//TODO 此处是否考虑不使用EMPTY_CALLBACK
			contentHandleInvoker.invokeInPool("", requestHandler, contentHandleMethod, Invoker.EMPTY_CALLBACK, content);
		}
	}
	
	boolean isStarted() {
		return started;
	}
	
	public void start() throws Exception {
		if (null == globalHomeDir) {
			throw new GlobalHomeDirNullException("global homedir must be set before started");
		}
		initVirtualAgent();
		initNetty();
	}
	
	public void stop() {
		if (bossGroup != null) {
			bossGroup.shutdownGracefully();
		}
		if (workerGroup != null) {
			workerGroup.shutdownGracefully();
		}
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		if (isStarted()) {
			throw new IllegalStateException("port must set before started");
		}
		this.port = port;
	}
	
	public String getGlobalHomeDir() {
		return globalHomeDir.getAbsolutePath();
	}
	
	public void setGlobalHomeDir(String globalHomeDir) {
		if (isStarted()) {
			throw new IllegalStateException("global homedir must set before started");
		}
		File ff = new File(globalHomeDir);
		if (ff.exists() && !ff.isDirectory()) {
			throw new IllegalArgumentException(globalHomeDir + " is not directory");
		}
		if (!ff.exists()) {
			if (!ff.mkdirs()) {
				throw new IllegalArgumentException("can not create global home dir " + globalHomeDir);
			}
		}
		this.globalHomeDir = ff;
	}
	
	public Set<String> getNamespaces() {
		return namespaceAgentMap.keySet();
	}
	
	public VirtualAgent getAgent(String namespace) {
		return namespaceAgentMap.get(namespace);
	}
}
