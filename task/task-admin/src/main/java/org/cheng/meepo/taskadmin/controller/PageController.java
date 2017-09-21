package org.cheng.meepo.taskadmin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Administrator on 2017/9/21.
 */
@Controller
public class PageController {

    @RequestMapping("/index")
    public String indexPage(ModelMap model){
        model.put("userName","hello world");
        return "index";
    }

    @RequestMapping("/page/task")
    public String taskPage(){
        return "task";
    }

     @RequestMapping("/page/scheduler")
    public String schedulerPage(){
        return "scheduler";
    }


}
