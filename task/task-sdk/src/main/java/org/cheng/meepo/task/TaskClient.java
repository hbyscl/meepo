package org.cheng.meepo.task;

import org.cheng.meepo.task.constant.TaskType;
import org.cheng.meepo.task.dto.ServiceInvokeParam;
import org.cheng.meepo.task.service.TaskService;
import org.springframework.context.support.ApplicationObjectSupport;

/**
 * Created by ChengLi on 2016/10/10.
 */
public class TaskClient extends ApplicationObjectSupport {

    private TaskService taskService;

    private TaskClient(){
        taskService = getApplicationContext().getBean(TaskService.class);
    }

    private static TaskClient instance = null;

    public static TaskClient getInstance() {
        synchronized (TaskClient.class){
            if(null == instance){
                synchronized (TaskClient.class){
                    instance = new TaskClient();
                }
            }
        }
        return instance;
    }

    public String createTask(ServiceInvokeParam serviceInvokeParam, TaskType taskType, String executeDefine){
        return taskService.create(serviceInvokeParam,taskType,executeDefine);
    }

    public Object getTaskResult(String taskId){
        return taskService.getResult(taskId);
    }
}
