package org.cheng.meepo;

import org.cheng.meepo.task.exception.TaskServiceException;
import org.cheng.meepo.task.constant.TaskType;
import org.cheng.meepo.task.dto.ServiceInvokeParam;
import org.cheng.meepo.task.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by ChengLi on 2016/6/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:dubbo-task-consumer.xml"
})
public class TestTaskService {

    @Autowired
    TaskService taskService;

    @Test
    public void craeteTimingTask() throws TaskServiceException {
        String createBo = taskService.create(new ServiceInvokeParam(
                "org.cheng.meepo.service.TaskTestServiceOne",
                "createBo",
                new String[]{String.class.getName(), String.class.getName(), Integer.class.getName()},
                new Object[]{"a", "b", 1}
        ), TaskType.TIMING, "20160824155000");
        System.out.println("createBo = " + createBo);
    }


    @Test
    public void craeteRealTask() throws TaskServiceException {
        String createBo = taskService.create(new ServiceInvokeParam(
                "org.cheng.meepo.service.TaskTestServiceOne",
                "createBo",
                new String[]{String.class.getName(), String.class.getName(), Integer.class.getName()},
                new Object[]{"a", "b", 1}
        ), TaskType.REAL, "");
        System.out.println("createBo = " + createBo);
    }


    @Test
    public void craeteLadderTask() throws TaskServiceException {
        String createBo = taskService.create(new ServiceInvokeParam(
                "org.cheng.meepo.service.TaskTestServiceOne",
                "createBo",
                new String[]{String.class.getName(), String.class.getName(), Integer.class.getName()},
                new Object[]{"a", "b", 1}
        ), TaskType.LADDER, "1,3,5,10,30,60");
        System.out.println("createBo = " + createBo);
    }


    @Test
    public void craetePlanTask() throws TaskServiceException {
        String createBo = taskService.create(new ServiceInvokeParam(
                "org.cheng.meepo.service.TaskTestServiceOne",
                "createBo",
                new String[]{String.class.getName(), String.class.getName(), Integer.class.getName()},
                new Object[]{"a", "b", 1}
        ), TaskType.PLAN, "0/10 * * * * ? ");
        System.out.println("createBo = " + createBo);
    }

    @Test
    public void delTask() throws TaskServiceException{
        taskService.cancel("");
    }
}
