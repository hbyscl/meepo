package org.cheng.meepo.task.service;

import org.cheng.meepo.exception.TaskServiceException;
import org.cheng.meepo.task.constant.TaskType;
import org.cheng.meepo.task.dto.ServiceInvokeParam;
import org.cheng.meepo.task.dto.TaskContext;

import java.util.List;

/**
 * Created by ChengLi on 2016/6/17.
 * 任务模块API定义
 * appId使用Dubbo隐式传参
 */
public interface TaskService {
    /**
     * 创建任务
     *
     * @param serviceInvokeParam 调用的服务参数
     * @param taskType           任务类型
     * @param executeDefine      具体执行方式定义
     *                           REAL("即时任务",1):   空或空字符串
     *                           LADDER("阶梯任务",2): 0，1，3，5，10 字符串以逗号分隔，秒作为时间单位
     *                           TIMING("定时任务",3): 20160817010101 指定执行时间
     *                           PLAN("计划任务",4): 0/10 0 0 0 0 ? cron表达式
     * @return 任务ID
     */
    public String create(ServiceInvokeParam serviceInvokeParam, TaskType taskType, String executeDefine) throws TaskServiceException;

    /**
     * 取消任务
     *
     * @param taskId 任务ID
     * @return 成功与否
     */
    public Boolean cancel(String taskId) throws TaskServiceException;

    /**
     * 获取当前正在调度的任务列表
     *
     * @return 任务列表
     * @throws TaskServiceException
     */
    public List<TaskContext> list() throws TaskServiceException;

    /**
     * 获取指定任务详细信息
     *
     * @param taskId 任务ID
     * @return 任务调度信息
     * @throws TaskServiceException
     */
    public TaskContext get(String taskId) throws TaskServiceException;

    /**
     * 通过指定的具体调用服务查询任务列表
     *
     * @param serviceInvokeParam 调用服务信息
     * @return 任务列表
     * @throws TaskServiceException
     */
    public List<TaskContext> search(ServiceInvokeParam serviceInvokeParam) throws TaskServiceException;

    /**
     * 保存任务执行结果
     *
     * @param taskId 任务ID
     * @param result 任务执行结果对象
     * @return 成功与否
     * @throws TaskServiceException
     */
    public Boolean saveResult(String taskId, Object result) throws TaskServiceException;

    /**
     * 获取任务执行结果
     *
     * @param taskId 任务ID
     * @return 任务执行结果
     * @throws TaskServiceException
     */
    public Object getResult(String taskId) throws TaskServiceException;


}

