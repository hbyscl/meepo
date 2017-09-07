package org.cheng.meepo.task.exception;

/**
 * Created by ChengLi on 2016/6/22.
 */
public class TaskServiceException extends RuntimeException {
    public TaskServiceException(String message) {
        super(message);
    }

    public TaskServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
