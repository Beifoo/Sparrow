package club.beifoo.sparrow.edge;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.Date;
import java.util.regex.Pattern;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultHttpRequest;

public class FileOpt {
	static final String TYPE_REMOTE_STREAM = "remote";
	static final String TYPE_LOCAL_FILE    = "local";
	//
	private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");
	//
	String sourceType;
	String id;
	File file;
	String uri;
	Channel channel;
	Date createTime;
	long totalBytes;
	long transferedBytes;
	long transferedPercent;
	InetSocketAddress remoteAddress;
	EdgeProxyServer server;
	DefaultHttpRequest httpRequest;
	
	//
	public FileOpt(EdgeProxyServer server, String uri, Channel channel, DefaultHttpRequest httpRequest) {
		this.server = server;
		this.httpRequest = httpRequest;
		this.uri = uri;
		this.channel = channel;
		this.remoteAddress = (InetSocketAddress)channel.remoteAddress();
		this.createTime = new Date();
		this.file = uri2FilePath(uri);
	}
	
	//
	protected File uri2FilePath(String uri) {
		try {
			uri = URLDecoder.decode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}

		if (uri.isEmpty() || uri.charAt(0) != '/') {
			return null;
		}
		//
		int indexOfParameter=uri.indexOf('?');
		if(indexOfParameter!=-1){
			uri=uri.substring(0,indexOfParameter);
		}
		// Convert file separators.
		uri = uri.replace('/', File.separatorChar);
		// Simplistic dumb security check.
		// You will have to do something serious in the production environment.
		if (uri.contains(File.separator + '.')
				|| uri.contains('.' + File.separator) || uri.charAt(0) == '.'
				|| uri.charAt(uri.length() - 1) == '.'
				|| INSECURE_URI.matcher(uri).matches()) {
			return null;
		}

		// Convert to absolute path.
		// TODO 处理namespace对应的子目录，最好测试一下
		return new File(server.getGlobalHomeDir(), uri);
	}

}
