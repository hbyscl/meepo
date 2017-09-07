package org.cheng.meepo.task.constant;

/**
 * Created by ChengLi on 2016/6/22.
 */
public enum TaskStatus {
    CREATE("创建","10"),
    SCHEDULE("调度中","20"),
    RUNNING("执行中","30"),
    DONE("完成","40"),
    SUCCESS("成功","41"),
    CANCEL("取消","50"),
    FAIL("失败","51")
    ;
    private String desc;
    private String code;
    TaskStatus(String desc,String code) {
        this.desc = desc;
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public String getCode() {
        return code;
    }

    public static TaskStatus getTaskStatusByCode(String code){
        TaskStatus[] values = TaskStatus.values();
        for (TaskStatus value : values) {
            if(value.getCode().equals(code)){
                return value;
            }
        }
        return null;
    }

    public static TaskStatus getTaskStatusByDesc(String desc){
        TaskStatus[] values = TaskStatus.values();
        for (TaskStatus value : values) {
            if(value.getDesc().equals(desc)){
                return value;
            }
        }
        return null;
    }
}
