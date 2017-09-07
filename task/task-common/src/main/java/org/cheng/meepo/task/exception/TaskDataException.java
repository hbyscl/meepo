package org.cheng.meepo.task.exception;

/**
 * Created by ChengLi on 2016/6/22.
 */
public class TaskDataException extends RuntimeException {
    public TaskDataException(String message) {
        super(message);
    }

    public TaskDataException(Throwable cause) {
        super(cause);
    }

    public TaskDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
