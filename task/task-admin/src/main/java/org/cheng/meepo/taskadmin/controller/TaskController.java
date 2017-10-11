package org.cheng.meepo.taskadmin.controller;

import org.cheng.meepo.task.dto.TaskContext;
import org.cheng.meepo.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Administrator on 2017/10/11.
 */
@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    TaskService taskService;

    @RequestMapping(value = "list",method = RequestMethod.GET)
    public List<TaskContext> list(){
        return taskService.list();
    }
}
