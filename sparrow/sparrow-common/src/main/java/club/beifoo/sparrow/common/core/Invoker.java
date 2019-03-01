package club.beifoo.sparrow.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Invoker extends AbstractExecutorService  {
	//TODO Self4j+Logback日志
	//
	private static final int DEFAULT_REQUEST_QUEUE_SIZE = 20480;
	private static final int DEFAULT_CORE_POOL_SIZE = 32;
	private static final int DEFAULT_MAX_POOL_SIZE  = 64;
	private static final int DEFAULT_KEEPALIVE_TIME = 60;
	//
	public static final Object EMPTY_ARGS[] = new Object[] {};
	public static final InvokeAspect EMPTY_CALLBACK = new DefaultInvokeAspect() {};
	//
	private String invokeThreadNamePrefix;
	private String invokeThreadGroupName;
	private ThreadPoolExecutor poolExecutor;
	private LinkedBlockingQueue<Runnable> requestQueue;
	private List<InvokeAspect> globalAspects;

	public Invoker() {
		requestQueue = new LinkedBlockingQueue<Runnable>(DEFAULT_REQUEST_QUEUE_SIZE);
		poolExecutor = new ThreadPoolExecutor(
				DEFAULT_CORE_POOL_SIZE, 
				DEFAULT_MAX_POOL_SIZE,
				DEFAULT_KEEPALIVE_TIME,
				TimeUnit.SECONDS, 
				requestQueue, new InvokeThreadFactory(invokeThreadGroupName, invokeThreadNamePrefix, false));
		poolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
		globalAspects = new ArrayList<InvokeAspect>();
	}
	
	public void addGlobalAspects(InvokeAspect aspect){
		globalAspects.add(aspect);
	}
	
	public List<InvokeAspect> getGlobalAspects(){
		return new ArrayList<InvokeAspect>(globalAspects);
	}
	
	@Override
	public void execute(Runnable runnable) {
		poolExecutor.execute(runnable);
	}
	
	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isShutdown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTerminated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Runnable> shutdownNow() {
		// TODO Auto-generated method stub
		return null;
	}
	
	//--------------------------------------------------
	/**
	 * @return
	 * @see java.util.concurrent.ThreadPoolExecutor#getActiveCount()
	 */
	public int getActiveCount() {
		return poolExecutor.getActiveCount();
	}
	/**
	 * @param corePoolSize
	 * @see java.util.concurrent.ThreadPoolExecutor#setCorePoolSize(int)
	 */
	public void setCorePoolSize(int corePoolSize) {
		poolExecutor.setCorePoolSize(corePoolSize);
	}
	/**
	 * @return
	 * @see java.util.concurrent.ThreadPoolExecutor#getCorePoolSize()
	 */
	public int getCorePoolSize() {
		return poolExecutor.getCorePoolSize();
	}
	/**
	 * @param maximumPoolSize
	 * @see java.util.concurrent.ThreadPoolExecutor#setMaximumPoolSize(int)
	 */
	public void setMaximumPoolSize(int maximumPoolSize) {
		poolExecutor.setMaximumPoolSize(maximumPoolSize);
	}
	/**
	 * @return
	 * @see java.util.concurrent.ThreadPoolExecutor#getMaximumPoolSize()
	 */
	public int getMaximumPoolSize() {
		return poolExecutor.getMaximumPoolSize();
	}
	/**
	 * @param time
	 * @param unit
	 * @see java.util.concurrent.ThreadPoolExecutor#setKeepAliveTime(long, java.util.concurrent.TimeUnit)
	 */
	public void setKeepAliveTime(long time, TimeUnit unit) {
		poolExecutor.setKeepAliveTime(time, unit);
	}
	/**
	 * @param unit
	 * @return
	 * @see java.util.concurrent.ThreadPoolExecutor#getKeepAliveTime(java.util.concurrent.TimeUnit)
	 */
	public long getKeepAliveTime(TimeUnit unit) {
		return poolExecutor.getKeepAliveTime(unit);
	}
	/**
	 * @return
	 * @see java.util.concurrent.ThreadPoolExecutor#getQueue()
	 */
	public BlockingQueue<Runnable> getQueue() {
		return poolExecutor.getQueue();
	}
	/**
	 * @return
	 * @see java.util.concurrent.ThreadPoolExecutor#getPoolSize()
	 */
	public int getPoolSize() {
		return poolExecutor.getPoolSize();
	}
	/**
	 * @return
	 * @see java.util.concurrent.ThreadPoolExecutor#getTaskCount()
	 */
	public long getTaskCount() {
		return poolExecutor.getTaskCount();
	}
	/**
	 * @return
	 * @see java.util.concurrent.ThreadPoolExecutor#getCompletedTaskCount()
	 */
	public long getCompletedTaskCount() {
		return poolExecutor.getCompletedTaskCount();
	}
	/**
	 * @return
	 * @see java.util.concurrent.ThreadPoolExecutor#setRejectedExecutionHandler()
	 */
	public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
		poolExecutor.setRejectedExecutionHandler(handler);
	}
	/**
	 * @return
	 * @see java.util.concurrent.ThreadPoolExecutor#getRejectedExecutionHandler()
	 */
	public RejectedExecutionHandler getRejectedExecutionHandler() {
		return poolExecutor.getRejectedExecutionHandler();
	}
	/**
	 * @return
	 * @see java.util.concurrent.ThreadPoolExecutor#allowsCoreThreadTimeOut()
	 */
	public boolean allowsCoreThreadTimeOut() {
		return poolExecutor.allowsCoreThreadTimeOut();
	}
	/**
	 * @return
	 * @see java.util.concurrent.ThreadPoolExecutor#getLargestPoolSize()
	 */
	public int getLargestPoolSize() {
		return poolExecutor.getLargestPoolSize();
	}
}
