package org.cheng.meepo;

import com.alibaba.fastjson.JSONArray;
import org.cheng.meepo.exception.TaskDataException;
import org.cheng.meepo.task.constant.TaskStatus;
import org.cheng.meepo.task.constant.TaskType;
import org.cheng.meepo.task.dto.TaskContext;
import org.cheng.meepo.task.storage.TaskExecuteLog;
import org.cheng.meepo.task.storage.TaskParamStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by ChengLi on 2016/9/21.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:application-mongodb.xml"
})
public class TestMongodb {
    
    private static final String taskId = "abc";
    
    @Autowired
    private TaskParamStore taskParamStore;

    @Test
    public void save()  {
        try {
            TaskContext taskContext = new TaskContext();
            taskContext.setAppId("app1");
            taskContext.setTaskType(TaskType.REAL);
            taskContext.setExecuteDefine("");
            taskContext.setScheduleServer("");
            boolean a = taskParamStore.save(taskId, taskContext);
            Assert.isTrue(a);
        } catch (TaskDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void get()  {
        try {
            TaskContext taskContext = taskParamStore.get(taskId);
            System.out.println("taskContext = " + taskContext);
            Assert.notNull(taskContext);
        } catch (TaskDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void delete()  {
        try {
            Assert.isTrue(taskParamStore.delete(taskId));
        } catch (TaskDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setStatus()  {
        try {
            Assert.isTrue(taskParamStore.setStatus(taskId,TaskStatus.SUCCESS));
        } catch (TaskDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getStatus()  {
        try {
            TaskStatus a = taskParamStore.getStatus(taskId);
            System.out.println("a = " + a);
            Assert.notNull(a);
        } catch (TaskDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setInProgressId()  {
        try {
            boolean b = taskParamStore.setInProgressId(taskId, "asdfasdf");
            Assert.isTrue(b);
        } catch (TaskDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getInProgressId()  {
        try {
            String a = taskParamStore.getInProgressId(taskId);
            System.out.println("a = " + a);
            Assert.notNull(a);
        } catch (TaskDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void log()  {
        try {
            for (int i = 0 ; i < 10 ;i ++){
                Assert.isTrue(taskParamStore.log(taskId, TaskStatus.DONE));
            }
        } catch (TaskDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void listLog()  {
        try {
            List<TaskExecuteLog> list = taskParamStore.listLog(taskId);
            System.out.println("list = " + JSONArray.toJSON(list).toString());
            Assert.notNull(list);
            Assert.notEmpty(list);
        } catch (TaskDataException e) {
            e.printStackTrace();
        }
    }
}
