package org.cheng.meepo.task.scheduler.exception;

/**
 * Created by ChengLi on 2016/9/21.
 */
public class TaskOperationException extends Exception {
    public TaskOperationException(String message) {
        super(message);
    }

    public TaskOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
