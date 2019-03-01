package club.beifoo.sparrow.common.core;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class InvokeThreadFactory implements ThreadFactory {
	//TODO Logger
	private final AtomicInteger threadCounter;
	private final boolean daemonThread;
	private final ThreadGroup threadGroup;
	private final String poolThreadNamePrefix;
	
	public InvokeThreadFactory(String threadGroupName, String threadNamePrefix, boolean daemonThread) {
		this.threadCounter = new AtomicInteger(1);
		this.daemonThread = daemonThread;
		SecurityManager sm = System.getSecurityManager();
		threadGroup = (sm != null) ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();
		poolThreadNamePrefix = new StringBuilder("ThreadPool-")
				.append(threadGroupName).append("-").append(threadNamePrefix).append("-").toString();
	}

	@Override
	public Thread newThread(Runnable runnable) {
		Thread t = new Thread(threadGroup, runnable, poolThreadNamePrefix + threadCounter.incrementAndGet(), 0);
		t.setDaemon(daemonThread);
		Thread.UncaughtExceptionHandler logHander=new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				//TODO 日志输出
				//logger.error(e.getMessage(),e);
			}
		};
		t.setUncaughtExceptionHandler(logHander);
		return t;
	}

}
