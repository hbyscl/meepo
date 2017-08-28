package org.cheng.meepo.task.scheduler.taskmanager;

import org.cheng.meepo.task.scheduler.ScheduleMain;
import org.cheng.meepo.task.scheduler.exception.TaskOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * 任务操作管理
 * Created by ChengLi on 2016/6/22.
 */
public class TaskOperationManager {

    private static transient Logger log = LoggerFactory.getLogger(TaskOperationManager.class);

    private static ScheduleMain scheduleManager;

    /**
     * 获取核心调度管理器
     *
     * @return
     * @throws Exception
     */
    public static ScheduleMain getScheduleManager() throws TaskOperationException {
        if (null == TaskOperationManager.scheduleManager) {
            synchronized (TaskOperationManager.class) {
                TaskOperationManager.scheduleManager = ScheduleMain.getApplicationcontext().getBean(ScheduleMain.class);
            }
        }
        return TaskOperationManager.scheduleManager;
    }

    /**
     * 添加调度任务
     *
     * @param taskDefine
     */
    public static void addScheduleTask(TaskDefine taskDefine) throws TaskOperationException {
        try {
            TaskOperationManager.getScheduleManager().getScheduleDataManager().addTask(taskDefine);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TaskOperationException("添加调度任务出错",e);
        }
    }

    /**
     * 删除调度任务
     *
     * @param taskDefine
     */
    public static void delScheduleTask(TaskDefine taskDefine) throws TaskOperationException {
        try {
            TaskOperationManager.getScheduleManager().getScheduleDataManager().delTask(taskDefine);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TaskOperationException("删除调度任务出错",e);
        }
    }

    /**
     * 删除指定调度任务
     *
     * @param taskId
     */
    public static void delScheduleTask(String taskId)  throws TaskOperationException{
        try {
            List<TaskDefine> taskDefines = queryScheduleTask(taskId);
            for (TaskDefine taskDefine : taskDefines) {
                TaskOperationManager.getScheduleManager().getScheduleDataManager().delTask(taskDefine);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TaskOperationException("删除指定调度任务出错",e);
        }
    }

    /**
     * 查询调度任务
     *
     * @return
     */
    public static List<TaskDefine> queryScheduleTask() throws TaskOperationException {
        List<TaskDefine> taskDefines = new ArrayList<TaskDefine>();
        try {
            List<TaskDefine> tasks = TaskOperationManager.getScheduleManager().getScheduleDataManager().selectTask();
            taskDefines.addAll(tasks);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TaskOperationException("查询调度任务出错",e);
        }
        return taskDefines;
    }

    /**
     * 通过ID查询调度任务
     *
     * @return
     */
    public static List<TaskDefine> queryScheduleTask(String taskId) throws TaskOperationException {
        List<TaskDefine> taskDefines = new ArrayList<TaskDefine>();
        try {
            List<TaskDefine> tasks = TaskOperationManager.getScheduleManager().getScheduleDataManager().selectTask(taskId);
            taskDefines.addAll(tasks);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TaskOperationException("通过ID查询调度任务出错",e);
        }
        return taskDefines;
    }

    /**
     * 判断任务是否存在
     *
     * @param taskDefine
     * @return
     * @throws Exception
     */
    public static boolean isExistsTask(TaskDefine taskDefine)  throws TaskOperationException{
        boolean existsTask = false;
        try {
            existsTask = TaskOperationManager.scheduleManager.getScheduleDataManager().isExistsTask(taskDefine);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TaskOperationException("判断任务是否存在出错",e);
        }
        return existsTask;
    }

}
