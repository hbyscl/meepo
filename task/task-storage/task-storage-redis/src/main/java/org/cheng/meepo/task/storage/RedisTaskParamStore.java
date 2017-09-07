package org.cheng.meepo.task.storage;

import org.cheng.meepo.task.exception.TaskDataException;
import org.cheng.meepo.task.constant.TaskStatus;
import org.cheng.meepo.task.dto.TaskContext;
import org.cheng.meepo.task.util.PropertiesParse;
import org.cheng.meepo.task.util.SerializerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;

/**
 * Created by ChengLi on 2016/9/6.
 * 数据存储Redis实现
 * 数据结构
 * Hash------TASK_STORE_{TASK_ID}
 *    +------STATUS : {TaskStatus}
 *    +------CONTEXT : {TaskContext}
 *    +------PROGRESS : {String}
 *
 * List------TASK_EXECUTE_LOG_{TASK_ID}
 *    +------[{TaskExecuteLog}]
 *
 */
public class RedisTaskParamStore implements TaskParamStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisTaskParamStore.class);

    private JedisPool jedisPool = null;


    private static final String KEY_PRE = "TASK_STORE_";
    private static final String KEY_STATUS = "STATUS";
    private static final String KEY_CONTEXT = "CONTEXT";
    private static final String KEY_PROGRESS = "PROGRESS";
    private static final String KEY_LOG = "TASK_EXECUTE_LOG_";

    private JedisPool getJedisPool() {
        synchronized (RedisTaskParamStore.class){
            if(null == jedisPool){
                JedisPoolConfig config = new JedisPoolConfig();
                jedisPool = new JedisPool(config, PropertiesParse.getProperty("redis.host"),
                        Integer.parseInt(PropertiesParse.getProperty("redis.port")));
            }
        }

        return jedisPool;
    }

    @Override
    public boolean save(String taskId, TaskContext obj) throws TaskDataException {
        try (Jedis jedis = getJedisPool().getResource()) {
            Map<String, String> taskBody = new HashMap<>();
            taskBody.put(KEY_STATUS, TaskStatus.CREATE.getCode());
            taskBody.put(KEY_CONTEXT, SerializerUtil.object2base64(obj));
            jedis.hmset(getTaskKey(taskId), taskBody);
            this.log(taskId,TaskStatus.CREATE);
            //jedis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public TaskContext get(String taskId) throws TaskDataException {
        TaskContext context = null;
        try (Jedis jedis = getJedisPool().getResource()) {
            List<String> contextList = jedis.hmget(getTaskKey(taskId), KEY_CONTEXT);
            if (!contextList.isEmpty()) {
                context = (TaskContext) SerializerUtil.base642object(contextList.get(0));
            }
            //jedis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return context;
    }

    @Override
    public boolean delete(String taskId) throws TaskDataException {
        try (Jedis jedis = getJedisPool().getResource()) {
            jedis.del(getTaskKey(taskId));
            jedis.del(getTaskLogKey(taskId));
            //jedis.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public TaskStatus getStatus(String taskId) throws TaskDataException {
        try (Jedis jedis = getJedisPool().getResource()) {
            List<String> contextList = jedis.hmget(getTaskKey(taskId), KEY_STATUS);
            //jedis.close();
            if (!contextList.isEmpty()) {
                return TaskStatus.getTaskStatusByCode(contextList.get(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean setStatus(String taskId, TaskStatus taskStatus) throws TaskDataException {
        try (Jedis jedis = getJedisPool().getResource()) {
            Map<String, String> taskBody = new HashMap<>();
            taskBody.put(KEY_STATUS, taskStatus.getCode());
            String status = jedis.hmset(getTaskKey(taskId), taskBody);
            this.log(taskId,taskStatus);
            //jedis.close();
            return "OK".equals(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean setInProgressId(String taskId, String progressId) throws TaskDataException {
        try (Jedis jedis = getJedisPool().getResource()) {
            Map<String, String> taskBody = new HashMap<>();
            taskBody.put(KEY_PROGRESS, progressId);
            String progress = jedis.hmset(getTaskKey(taskId), taskBody);
            //jedis.close();
            return "OK".equals(progress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getInProgressId(String taskId) throws TaskDataException {
        try (Jedis jedis = getJedisPool().getResource()) {
            List<String> contextList = jedis.hmget(getTaskKey(taskId), KEY_PROGRESS);
            //jedis.close();
            if (!contextList.isEmpty()) {
                return contextList.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean log(String taskId, TaskStatus taskStatus) throws TaskDataException {
        try (Jedis jedis = getJedisPool().getResource()) {
            jedis.lpush(getTaskLogKey(taskId),SerializerUtil.object2base64(
                    new TaskExecuteLog(
                            taskId, taskStatus, new Date(System.currentTimeMillis())
                    )));
            //jedis.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<TaskExecuteLog> listLog(String taskId) throws TaskDataException {
        List<TaskExecuteLog> list = new ArrayList<>();
        try (Jedis jedis = getJedisPool().getResource()) {
            List<String> strLogList = jedis.lrange(getTaskLogKey(taskId), 0, -1);
            for (String strLog : strLogList) {
                list.add((TaskExecuteLog) SerializerUtil.base642object(strLog));
            }
            //jedis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public boolean setResult(String taskId, Object result) throws TaskDataException {
        LOGGER.warn("Redis任务数据存储暂未实现保存执行结果功能");
        return false;
    }

    private String getTaskKey(String taskId) {
        return KEY_PRE + taskId;
    }
    private String getTaskLogKey(String taskId) {
        return KEY_LOG + taskId;
    }
}
