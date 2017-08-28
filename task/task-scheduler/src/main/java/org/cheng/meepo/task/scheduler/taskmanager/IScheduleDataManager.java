package org.cheng.meepo.task.scheduler.taskmanager;

import java.util.List;


/**
 * 调度配置中心客户端接口，可以有基于数据库的实现，可以基于ConfigServer的实现
 * 
 * Created by ChengLi on 2016/6/19.
 * 
 */
public interface IScheduleDataManager{

    /**
     * 发送心跳信息
     *
     * @param server
     * @throws Exception
     */
	public boolean refreshScheduleServer(ScheduleServer server) throws Exception;

    /**
     * 注册服务器
     * 
     * @param server
     * @throws Exception
     */
    public void registerScheduleServer(ScheduleServer server) throws Exception;

    
    public boolean isLeader(String uuid, List<String> serverList);

	public void unRegisterScheduleServer(ScheduleServer server) throws Exception;

	public void clearExpireScheduleServer() throws Exception;
	
	
	public List<String> loadScheduleServerNames() throws Exception;
	
	public void assignTask(String currentUuid, List<String> taskServerList) throws Exception;
	
	public boolean isOwner(String name, String uuid)throws Exception;
	
	public void addTask(TaskDefine taskDefine)throws Exception;
	
	/**
	 * addTask中存储的Key由对象本身的字符串组成，此方法实现重载
	 * @param targetBean
	 * @param targetMethod
	 * @throws Exception
	 */
	@Deprecated
	public void delTask(String targetBean, String targetMethod)throws Exception;
	
	public void delTask(TaskDefine taskDefine) throws Exception;
	
	public List<TaskDefine> selectTask()throws Exception;

	public List<TaskDefine> selectTask(String taskId)throws Exception;

	public boolean checkLocalTask(String currentUuid)throws Exception;
	
	public boolean isExistsTask(TaskDefine taskDefine) throws Exception;
    
	public boolean saveRunningInfo(String name, String uuid)throws Exception;
    
     
}