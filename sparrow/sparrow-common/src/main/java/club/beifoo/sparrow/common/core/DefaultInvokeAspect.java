package club.beifoo.sparrow.common.core;

import java.lang.reflect.Method;

public abstract class DefaultInvokeAspect implements InvokeAspect {
	@Override
	public void before(Object instance, Method method, Object[] args) throws Exception {}
	@Override
	public void after(Object instance, Method method, Object[] args) {}
	@Override
	public void end(Object instance, Method method, Object[] args, Object ret, Throwable e) {}
}
