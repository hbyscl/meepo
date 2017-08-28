package org.cheng.meepo.task.constant;

import java.io.Serializable;

/**
 * Created by ChengLi on 2016/6/17.
 * 任务类型定义
 */
public enum TaskType implements Serializable {
    REAL("即时任务",1),
    LADDER("阶梯任务",2),
    TIMING("定时任务",3),
    PLAN("计划任务",4)
    ;
    private String desc;
    private Integer code;
    TaskType(String desc,Integer code) {
        this.desc = desc;
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getCode() {
        return code;
    }
}
