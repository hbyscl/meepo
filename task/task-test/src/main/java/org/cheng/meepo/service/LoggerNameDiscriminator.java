/**   
 * Copyright (c) 2016, WuHan Hongtoo Technology Co., Ltd. All rights reserved. 
 * 武汉宏途科技有限公司 版权所有 
 * 
 * 审核人： 
 */
package org.cheng.meepo.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.AbstractDiscriminator;

/** 
 * @Description logger名称识别器 
 * @author zhengyf
 * @date 2016年8月3日 上午14:20
 * @version V1.0.0
 */
public class LoggerNameDiscriminator extends AbstractDiscriminator<ILoggingEvent> {

	private static final String KEY = "loggerName";
	
	private boolean started;
	
	public String getDiscriminatingValue(ILoggingEvent iLoggingEvent) {
		LoggerContext lc = (LoggerContext)getContext();
		String currentLoggerName = iLoggingEvent.getLoggerName();
		Logger currentLogger = lc.getLogger(currentLoggerName);
		//当前logger在配置中有定义返回当前logger的name
		if(currentLogger.iteratorForAppenders().hasNext()){
			return currentLoggerName;
		}

		//查找当前logger的‘有效’父logger
		Logger parentLogger = null;
		while(parentLogger == null){
			currentLoggerName = currentLoggerName.substring(0, currentLoggerName.lastIndexOf("."));
			Logger logger = lc.getLogger(currentLoggerName);
			if(logger.iteratorForAppenders().hasNext()){
				parentLogger = logger;
			}
		}
	    return parentLogger.getName();
	}
	
	public String getKey() {
	    return KEY;
	}
	
	public void start() {
	    started = true;
	}
	
	public void stop() {
	    started = false;
	}
	
	public boolean isStarted() {
	    return started;
	}
	
}
