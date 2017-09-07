package org.cheng.meepo.task.dto;

import org.cheng.meepo.task.constant.TaskStatus;
import org.cheng.meepo.task.constant.TaskType;

import java.io.Serializable;

/**
 * Created by ChengLi on 2016/6/22.
 * 任务详情
 */
public class TaskContext implements Serializable {
    // 任务ID
    private String taskId;
    private String id;
    // 执行服务参数
    private ServiceInvokeParam serviceInvokeParam;
    // 任务类型
    private TaskType taskType;
    // 执行方式定义
    private String executeDefine;
    // 是否只成功运行一次
    private boolean runOnce = false;
    // 应用ID
    private String appId;
    // 任务调度服务器信息
    private String scheduleServer;
    // 任务当前状态
    private TaskStatus taskStatus;
    // 任务当前执行标示号
    private String progressId;

    // 任务执行结果
    private Object executeResult;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
        this.id = taskId;
    }

    public ServiceInvokeParam getServiceInvokeParam() {
        return serviceInvokeParam;
    }

    public void setServiceInvokeParam(ServiceInvokeParam serviceInvokeParam) {
        this.serviceInvokeParam = serviceInvokeParam;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getExecuteDefine() {
        return executeDefine;
    }

    public void setExecuteDefine(String executeDefine) {
        this.executeDefine = executeDefine;
    }

    public boolean isRunOnce() {
        return runOnce;
    }

    public void setRunOnce(boolean runOnce) {
        this.runOnce = runOnce;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getScheduleServer() {
        return scheduleServer;
    }

    public void setScheduleServer(String scheduleServer) {
        this.scheduleServer = scheduleServer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getProgressId() {
        return progressId;
    }

    public void setProgressId(String progressId) {
        this.progressId = progressId;
    }

    public Object getExecuteResult() {
        return executeResult;
    }

    public void setExecuteResult(Object executeResult) {
        this.executeResult = executeResult;
    }
}
