package club.beifoo.sparrow.edge;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netty.handler.codec.http.DefaultHttpRequest;

public class FilterContext {
	public static final int CODE_OK=200;
	//
	File reqFile;
	DefaultHttpRequest req; 
	Map<String,String>rspHeaderMap;
	int errCode;
	//
	FilterContext(File requestFile) {
		this.reqFile = requestFile;
		this.errCode = CODE_OK;
		this.rspHeaderMap = new HashMap<String, String>();
	}
	//
	public File getRequestFile() {
		return reqFile;
	}
	//
	public String getURI(){
		return req.uri();
	}
	//
	public String getRequestHeader(String name){
		return req.headers().get(name);
	}
	public List<String> getRequestHeaders(String name){
		return req.headers().getAll(name);
	}
	//
	public void setResponseHeader(String key,String value){
		rspHeaderMap.put(key, value);
	}
	//
	public void setErrCode(int errCode){
		this.errCode = errCode;
	}
}
