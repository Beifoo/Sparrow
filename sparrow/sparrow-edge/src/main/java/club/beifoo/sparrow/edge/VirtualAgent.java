package club.beifoo.sparrow.edge;

import java.io.File;
import java.net.URL;
import java.util.concurrent.atomic.AtomicLong;

public class VirtualAgent {
	private URL originSite;
	private File agentHomeDir;
	private RequestFilter requestFilter;
	private AtomicLong requestIdGenerator;
	private CachePolicy cachePolicy;
	private SourcePolicy sourcePolicy;
	//
	
	public RequestFilter getRequestFilter() {
		return requestFilter;
	}
}
