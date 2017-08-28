package org.cheng.meepo.task.scheduler.taskmanager;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

public class ScheduledMethodRunnable implements Runnable {

	private final Object target;

	private final Method method;
	
	private final String params;

	private final String taskTag;


	public ScheduledMethodRunnable(Object target, Method method, String params,String taskTag) {
		this.target = target;
		this.method = method;
		this.params = params;
		this.taskTag = taskTag;
	}

	public ScheduledMethodRunnable(Object target, String methodName, String params,String taskTag) throws NoSuchMethodException {
		this.target = target;
		this.method = target.getClass().getMethod(methodName);
		this.params = params;
		this.taskTag = taskTag;
	}


	public Object getTarget() {
		return this.target;
	}

	public Method getMethod() {
		return this.method;
	}
	
	public String getParams() {
		return params;
	}

	public String getTaskTag() {
		return taskTag;
	}

	@Override
	public void run() {
		try {
			ReflectionUtils.makeAccessible(this.method);
			if(this.getParams() != null){
				this.method.invoke(this.target, this.getParams());
			}else{
				this.method.invoke(this.target);
			}
		}
		catch (InvocationTargetException ex) {
			ReflectionUtils.rethrowRuntimeException(ex.getTargetException());
		}
		catch (IllegalAccessException ex) {
			throw new UndeclaredThrowableException(ex);
		}
	}

}
