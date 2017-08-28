package org.cheng.meepo.task.storage;

import org.cheng.meepo.task.constant.TaskStatus;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ChengLi on 2016/9/7.
 * 任务执行LOG Bean
 */
public class TaskExecuteLog implements Serializable {
    private String taskId;
    private TaskStatus taskStatus;
    private Date executeTime;

    public TaskExecuteLog(String taskId, TaskStatus taskStatus, Date executeTime) {
        this.taskId = taskId;
        this.taskStatus = taskStatus;
        this.executeTime = executeTime;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Date getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Date executeTime) {
        this.executeTime = executeTime;
    }

}
