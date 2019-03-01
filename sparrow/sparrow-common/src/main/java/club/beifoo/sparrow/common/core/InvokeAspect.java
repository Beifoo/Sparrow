package club.beifoo.sparrow.common.core;

import java.lang.reflect.Method;

public interface InvokeAspect {
	public void before(Object instance, Method method, Object[] args) throws Exception;
	public void after(Object instance, Method method, Object[] args);
	public void end(Object instance, Method method, Object[] args, Object ret, Throwable e);
}
