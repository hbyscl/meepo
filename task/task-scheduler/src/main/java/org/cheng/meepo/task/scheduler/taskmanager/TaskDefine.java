package org.cheng.meepo.task.scheduler.taskmanager;

import java.util.Date;

/**
 * 任务定义，提供关键信息给使用者
 * Created by ChengLi on 2016/6/19.
 *
 */
public class TaskDefine {
	
    /**
     * 目标bean
     */
    private String targetBean;
    
    /**
     * 目标方法
     */
    private String targetMethod;
    
    /**
     * cron表达式
     */
    private String cronExpression;
	
	/**
	 * 开始时间
	 */
	private Date startTime;
	
	/**
	 * 周期（秒）
	 */
	private long period;
	
	private String currentServer;
	
	/**
	 * 参数
	 */
	private String params;
	
	/**
	 * 类型
	 */
	private String type;

	private String taskTag = "";
	
	private int runTimes;
	
	private long lastRunningTime;
	
	public boolean begin(Date sysTime) {
		return null != sysTime && sysTime.after(startTime);
	}

	public String getTargetBean() {
		return targetBean;
	}

	public void setTargetBean(String targetBean) {
		this.targetBean = targetBean;
	}

	public String getTargetMethod() {
		return targetMethod;
	}

	public void setTargetMethod(String targetMethod) {
		this.targetMethod = targetMethod;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public String getCurrentServer() {
		return currentServer;
	}

	public void setCurrentServer(String currentServer) {
		this.currentServer = currentServer;
	}
	
	public String stringKey(){
		String s = getTargetBean() + "#" + getTargetMethod() + (null == taskTag || taskTag.trim().equals("") ? "" : ("#" + taskTag));
		return s;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getRunTimes() {
		return runTimes;
	}

	public void setRunTimes(int runTimes) {
		this.runTimes = runTimes;
	}

	public long getLastRunningTime() {
		return lastRunningTime;
	}

	public void setLastRunningTime(long lastRunningTime) {
		this.lastRunningTime = lastRunningTime;
	}

	public String getTaskTag() {
		return taskTag;
	}

	public void setTaskTag(String taskTag) {
		this.taskTag = taskTag;
	}
}