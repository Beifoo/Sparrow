package club.beifoo.sparrow.common.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import club.beifoo.sparrow.common.utils.DumpUtill;
import club.beifoo.sparrow.common.utils.NoTraceLog;


public class ThreadWorker implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(ThreadWorker.class); 
	
	private Object instance;
	private Object[] args;
	private Method method;
	private InvokeAspect aspect;
	private String traceId;
	//TODO Invoker暂时用不到
	//private Invoker invoker;
	private List<InvokeAspect>globalInvokeAspects;
	private Object ret = null;
	private Throwable exception = null;
	private long startTime;
	private boolean noTraceLog;
	
	//
	public ThreadWorker(Invoker invoker, String traceId, Object instance,
				Method method, Object[] args, InvokeAspect aspect) {
		if (instance == null) {
			throw new IllegalArgumentException("instance can not be null");
		}
		if(method == null){
			throw new IllegalArgumentException("method can not be null");
		}
		//TODO 暂时用不到
		//this.invoker = invoker;
		if (invoker.getGlobalAspects().isEmpty()) {
			this.globalInvokeAspects = null;
		} else {
			globalInvokeAspects = invoker.getGlobalAspects();
		}
		this.traceId    = traceId;
		this.instance   = instance;
		this.args       = args;
		this.method     = method;
		this.aspect     = aspect;
		this.startTime  = System.currentTimeMillis();
		this.noTraceLog = method.getAnnotation(NoTraceLog.class) != null;
	}
	
	//
	@Override
	public void run() {
		String oldName = Thread.currentThread().getName();
		Thread.currentThread().setName(oldName + "-" + traceId);
		long runStartTime = System.currentTimeMillis();
		String methodName = method.getDeclaringClass().getSimpleName() + "." + method.getName();
		if (!noTraceLog) {
			if (logger.isDebugEnabled()) {
				logger.debug(">invoke: - {}", methodName);
			}
			if (logger.isDebugEnabled()) {
				logger.debug(DumpUtill.dumpInvokeArgs(">invoke: " + methodName, args));
			}
		}
		try {
			if (globalInvokeAspects != null){
				for(InvokeAspect globalAspect : globalInvokeAspects){
					globalAspect.before(methodName, method, args);
				}
			}
			aspect.before(methodName, method, args);
			ret = method.invoke(instance, args);
			aspect.after(instance, method, args);
			if (globalInvokeAspects != null){
				for(InvokeAspect globalAspect : globalInvokeAspects){
					globalAspect.after(methodName, method, args);
				}
			}
		} catch (InvocationTargetException e) {
			exception = e.getTargetException();
		} catch (Throwable e) {
			exception = e;
		} finally {
			long current  = System.currentTimeMillis();
			long fullTime = current-startTime;
			long runTime  = current-runStartTime;
			if (exception != null) {
				if(exception instanceof InvokeException) {
					InvokeException ie = (InvokeException)exception;
					logger.warn("<invoke: {}, app exception code={}, msg={}" ,
							methodName,
							ie.getCode(),
							ie.getMessage());
				}
			}
			if (!noTraceLog) {
				if (logger.isDebugEnabled()) {
					logger.debug("<invoke: {} time={}-{}", methodName,
							runTime,fullTime);
				}
				if (logger.isDebugEnabled()) {
					logger.debug(DumpUtill.dumpInvokeObject("<invoke: " + methodName,ret));
				}
			}
			if (globalInvokeAspects!=null) {
				for (InvokeAspect globalAspect : globalInvokeAspects) {
					globalAspect.end(instance, method, args, ret, exception);
				}
			}
			aspect.end(instance, method, args, ret, exception);
			//TODO 执行统计操作
			//invoker.statMethod(method, exception,(int) runTime,(int) fullTime);
			Thread.currentThread().setName(oldName);
		}
	}
	
	public Object getRet() {
		return ret;
	}

	public Throwable getException() {
		return exception;
	}
}
