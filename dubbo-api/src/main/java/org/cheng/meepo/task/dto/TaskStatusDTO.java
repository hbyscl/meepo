package org.cheng.meepo.task.dto;

import org.cheng.meepo.task.constant.TaskStatus;

import java.io.Serializable;

/**
 * Created by ChengLi on 2016/6/29.
 */
public class TaskStatusDTO implements Serializable {
    private String taskId;
    private TaskStatus taskStatus;

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

    public TaskStatusDTO(String taskId, TaskStatus taskStatus) {
        this.taskId = taskId;
        this.taskStatus = taskStatus;

    }
}
