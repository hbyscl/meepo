package org.cheng.meepo.task.storage;

import org.cheng.meepo.exception.TaskDataException;
import org.cheng.meepo.task.constant.TaskStatus;
import org.cheng.meepo.task.dto.TaskContext;

import java.util.List;

/**
 * Created by ChengLi on 2016/6/19.
 * 任务数据存储定义
 */
public interface TaskParamStore {


    /**
     * 保存任务数据
     *
     * @param taskId 任务ID
     * @param obj    执行任务所需参数
     * @return 成功与否
     * @throws TaskDataException 数据存储异常
     */
    boolean save(String taskId, TaskContext obj) throws TaskDataException;

    /**
     * 获取任务执行所需参数
     *
     * @param taskId 任务ID
     * @return 任务执行参数
     * @throws TaskDataException 数据存储异常
     */
    TaskContext get(String taskId) throws TaskDataException;

    /**
     * 删除指定任务信息
     *
     * @param taskId 任务ID
     * @return 成功与否
     * @throws TaskDataException 数据存储异常
     */
    boolean delete(String taskId) throws TaskDataException;

    /**
     * 更改任务状态
     *
     * @param taskId     任务ID
     * @param taskStatus 任务状态
     * @return 成功与否
     * @throws TaskDataException 数据存储异常
     */
    boolean setStatus(String taskId, TaskStatus taskStatus) throws TaskDataException;

    /**
     * 获取任务当前状态
     * 轻量级查询操作
     *
     * @param taskId 任务ID
     * @return 任务状态
     * @throws TaskDataException 数据存储异常
     */
    TaskStatus getStatus(String taskId) throws TaskDataException;

    /**
     * 保存任务当前执行ID
     *
     * @param taskId     任务ID
     * @param progressId 任务执行ID,可以使用MQ的MessageId
     * @return 成功与否
     * @throws TaskDataException 数据存储异常
     */
    boolean setInProgressId(String taskId, String progressId) throws TaskDataException;


    /**
     * 获取任务当前执行ID
     * 轻量级查询操作
     *
     * @param taskId 任务ID
     * @return 任务执行ID
     * @throws TaskDataException 数据存储异常
     */
    String getInProgressId(String taskId) throws TaskDataException;


    /**
     * 记录任务执行完成状态
     *
     * @param taskId     任务ID
     * @param taskStatus 任务状态
     * @throws TaskDataException 数据存储异常
     */
    boolean log(String taskId, TaskStatus taskStatus) throws TaskDataException;

    /**
     * 查询任务执行日志
     *
     * @param taskId 任务ID
     * @return 任务执行日志
     * @throws TaskDataException
     */
    List<TaskExecuteLog> listLog(String taskId) throws TaskDataException;

    /**
     * 设置任务执行结果
     *
     * @param taskId 任务ID
     * @param result 执行结果
     * @return 是否成功
     * @throws TaskDataException
     */
    boolean setResult(String taskId, Object result) throws TaskDataException;

}
