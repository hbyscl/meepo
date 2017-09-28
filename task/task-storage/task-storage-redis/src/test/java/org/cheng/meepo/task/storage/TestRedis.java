package org.cheng.meepo.task.storage;

import org.cheng.meepo.task.constant.TaskStatus;
import org.cheng.meepo.task.constant.TaskType;
import org.cheng.meepo.task.dto.ServiceInvokeParam;
import org.cheng.meepo.task.dto.TaskContext;
import org.cheng.meepo.task.exception.TaskDataException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by ChengLi on 2016/9/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-redis.xml")
public class TestRedis {

    @Autowired
    private RedisTaskParamStore redisTaskParamStore;

    private String taskId = "a123";

    /**
     * 保存任务数据
     */
    @Test
    public void save() throws TaskDataException {
        TaskContext obj = new TaskContext();
        obj.setAppId("sadf");
        obj.setExecuteDefine("0 0/10 0 0 0 ?");
        obj.setRunOnce(true);
        obj.setServiceInvokeParam(new ServiceInvokeParam(
                "org.cheng.meepo.service.TaskTestServiceOne",
                "createBo",
                new String[]{String.class.getName(), String.class.getName(), Integer.class.getName()},
                new Object[]{"a", "b", 1}
        ));
        obj.setTaskId(taskId);
        obj.setTaskType(TaskType.PLAN);
        assertTrue(redisTaskParamStore.save(taskId, obj));
    }

    /**
     * 获取任务执行所需参数
     */
    @Test
    public void get() throws TaskDataException {
        assertNotNull(redisTaskParamStore.get(taskId));
    }

    /**
     * 删除指定任务信息
     */
    @Test
    public void delete() throws TaskDataException {
        assertTrue(redisTaskParamStore.delete(taskId));
    }

    /**
     * 更改任务状态
     */
    @Test
    public void setStatus() throws TaskDataException {
        assertTrue(redisTaskParamStore.setStatus(taskId, TaskStatus.RUNNING));
    }

    /**
     * 获取任务当前状态
     * 轻量级查询操作
     */
    @Test
    public void getStatus() throws TaskDataException {
        assertEquals(redisTaskParamStore.getStatus(taskId), TaskStatus.RUNNING);
    }

    /**
     * 记录任务执行完成状态
     */
    @Test
    public void log() throws TaskDataException {
        assertTrue(redisTaskParamStore.log(taskId, TaskStatus.DONE));
    }

    /**
     * 查询任务执行日志
     */
    @Test
    public void listLog() throws TaskDataException {
        List<TaskExecuteLog> list = redisTaskParamStore.listLog(taskId);
        for (TaskExecuteLog taskExecuteLog : list) {
            System.out.print("taskId:" + taskExecuteLog.getTaskId());
            System.out.print("-taskStatus:" + taskExecuteLog.getTaskStatus());
            System.out.println("executeTime:" + taskExecuteLog.getExecuteTime());
        }
        assertTrue(!list.isEmpty());
    }
}
