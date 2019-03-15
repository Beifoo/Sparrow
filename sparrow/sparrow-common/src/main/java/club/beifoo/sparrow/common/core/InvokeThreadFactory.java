package club.beifoo.sparrow.common.core;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvokeThreadFactory implements ThreadFactory {
	private final Logger logger = LoggerFactory.getLogger(InvokeThreadFactory.class);  
	
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
				logger.error(e.getMessage(), e);
			}
		};
		t.setUncaughtExceptionHandler(logHander);
		return t;
	}

}
