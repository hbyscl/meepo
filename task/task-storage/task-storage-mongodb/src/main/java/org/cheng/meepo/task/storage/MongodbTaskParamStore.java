package org.cheng.meepo.task.storage;

import org.cheng.meepo.exception.TaskDataException;
import org.cheng.meepo.task.constant.TaskStatus;
import org.cheng.meepo.task.dto.TaskContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by ChengLi on 2016/9/18.
 * 数据存储MongoDB实现
 */
public class MongodbTaskParamStore implements TaskParamStore {

    @Autowired
    private MongoTemplate mongo;

    private static final String TASK_COLLECTION_NAME = "task_detail";
    private static final String TASK_LOG_COLLECTION_NAME = "task_log";
    private static final String FILED_TASK_STATUS = "taskStatus";
    private static final String FILED_PROGRESS = "progressId";
    private static final String FILED_RESULT = "executeResult";

    @Override
    public boolean save(String taskId, TaskContext obj) throws TaskDataException {
        try {
            obj.setTaskId(taskId);
            mongo.save(obj, TASK_COLLECTION_NAME);
            this.log(taskId, TaskStatus.CREATE);
            return true;
        } catch (Exception e) {
            throw new TaskDataException("MongoDB保存任务数据出错", e);
        }
    }

    @Override
    public TaskContext get(String taskId) throws TaskDataException {
        try {
            Query query = new Query(Criteria.where("taskId").is(taskId));
            List<TaskContext> taskContexts = mongo.find(query, TaskContext.class, TASK_COLLECTION_NAME);
            return taskContexts.isEmpty() ? null : taskContexts.get(0);
        } catch (Exception e) {
            throw new TaskDataException("MongoDB获取任务数据出错", e);
        }
    }

    @Override
    public boolean delete(String taskId) throws TaskDataException {
        try {

            Query query = new Query(Criteria.where("taskId").is(taskId));
            mongo.remove(query, TASK_COLLECTION_NAME);
            mongo.remove(query, TASK_LOG_COLLECTION_NAME);
            return true;
        } catch (Exception e) {
            throw new TaskDataException("MongoDB删除任务数据出错", e);
        }
    }

    @Override
    public boolean setStatus(String taskId, TaskStatus taskStatus) throws TaskDataException {
        try {
            Update upd = new Update();
            upd.set(FILED_TASK_STATUS, taskStatus);
            Query query = new Query(Criteria.where("taskId").is(taskId));
            TaskContext taskContext = mongo.findAndModify(query, upd, TaskContext.class, TASK_COLLECTION_NAME);
            if (null != taskContext) {
                this.log(taskId, taskStatus);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new TaskDataException("MongoDB设置任务状态数据出错", e);
        }
    }

    @Override
    public TaskStatus getStatus(String taskId) throws TaskDataException {
        try {
            Query query = new Query(Criteria.where("taskId").is(taskId));
            List<TaskContext> taskContexts = mongo.find(query, TaskContext.class, TASK_COLLECTION_NAME);
            if (!taskContexts.isEmpty()) {
                return taskContexts.get(0).getTaskStatus();
            }
            return null;
        } catch (Exception e) {
            throw new TaskDataException("MongoDB获取任务状态数据出错", e);
        }
    }

    @Override
    public boolean setInProgressId(String taskId, String progressId) throws TaskDataException {
        try {
            Update upd = new Update();
            upd.set(FILED_PROGRESS, progressId);
            Query query = new Query(Criteria.where("taskId").is(taskId));
            TaskContext taskContext = mongo.findAndModify(query, upd, TaskContext.class, TASK_COLLECTION_NAME);
            return null != taskContext;
        } catch (Exception e) {
            throw new TaskDataException("MongoDB设置任务执行ID数据出错", e);
        }
    }

    @Override
    public String getInProgressId(String taskId) throws TaskDataException {
        try {
            Query query = new Query(Criteria.where("taskId").is(taskId));
            List<TaskContext> taskContexts = mongo.find(query, TaskContext.class, TASK_COLLECTION_NAME);
            if (!taskContexts.isEmpty()) {
                return taskContexts.get(0).getProgressId();
            }
            return null;
        } catch (Exception e) {
            throw new TaskDataException("MongoDB获取任务执行ID数据出错", e);
        }
    }

    @Override
    public boolean log(String taskId, TaskStatus taskStatus) throws TaskDataException {
        try {
            TaskExecuteLog log = new TaskExecuteLog(taskId, taskStatus, new Timestamp(System.currentTimeMillis()));
            mongo.save(log, TASK_LOG_COLLECTION_NAME);
            return true;
        } catch (Exception e) {
            throw new TaskDataException("MongoDB记录任务日志数据出错", e);
        }
    }

    @Override
    public List<TaskExecuteLog> listLog(String taskId) throws TaskDataException {
        try {
            Query query = new Query(Criteria.where("taskId").is(taskId));
            return mongo.find(query, TaskExecuteLog.class, TASK_LOG_COLLECTION_NAME);
        } catch (Exception e) {
            throw new TaskDataException("MongoDB获取任务日志数据出错", e);
        }
    }

    @Override
    public boolean setResult(String taskId, Object result) throws TaskDataException {
        try {
            Update upd = new Update();
            upd.set(FILED_RESULT, result);
            Query query = new Query(Criteria.where("taskId").is(taskId));
            return null != mongo.findAndModify(query, upd, TaskContext.class, TASK_COLLECTION_NAME);
        } catch (Exception e) {
            throw new TaskDataException("MongoDB设置任务执行结果数据出错", e);
        }
    }
}
