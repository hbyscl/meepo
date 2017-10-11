package org.cheng.meepo.taskadmin.core;

import com.alibaba.dubbo.config.spring.ReferenceBean;
import org.cheng.meepo.task.service.TaskService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Administrator on 2017/10/11.
 */
@Configuration
public class TaskServiceBean extends DubboBaseConfig {
    @Bean
    public ReferenceBean<TaskService> taskServiceReferenceBean() {
        ReferenceBean<TaskService> ref = new ReferenceBean<>();
        ref.setVersion("1.0");
        ref.setInterface(TaskService.class);
        ref.setTimeout(5000);
        ref.setRetries(3);
        ref.setCheck(false);
        return ref;
    }
}
