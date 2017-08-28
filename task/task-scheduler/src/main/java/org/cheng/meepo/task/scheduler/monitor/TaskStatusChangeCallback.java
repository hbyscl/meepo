package org.cheng.meepo.task.scheduler.monitor;

import org.cheng.meepo.task.constant.TaskConstants;
import org.cheng.meepo.task.constant.TaskStatus;
import org.cheng.meepo.task.constant.TaskType;
import org.cheng.meepo.task.dto.TaskContext;
import org.cheng.meepo.task.scheduler.mq.TaskMessageSender;
import org.cheng.meepo.task.scheduler.taskmanager.TaskDefine;
import org.cheng.meepo.task.scheduler.taskmanager.TaskOperationManager;
import org.cheng.meepo.task.storage.TaskParamStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by ChengLi on 2016/6/30.
 */
@Component("taskStatusChangeCallback")
public class TaskStatusChangeCallback {

    private static Logger log = LoggerFactory.getLogger(TaskStatusChangeCallback.class);

    @Autowired
    private TaskParamStore taskParamStore;

    @Autowired
    private TaskMessageSender taskMessageSender;


    public void onCreate(String taskId) {
        log.info("TaskStatusChangeCallback.onCreate." + taskId);
    }

    public void onSchedule(String taskId) {
        log.info("TaskStatusChangeCallback.onSchedule." + taskId);
    }

    /**
     * 消息发送监听
     * <p>
     * 1.MQ发送任务执行消息成功后在RocketMQ中TASK_EXPIRED_TOPIC频道发送一条消息,内容为{TASK_ID}-{PROGRESS_ID}，延时10秒投递
     * 2.监听TASK_EXPIRED事件，监听到后查询Redis中该TASKID的执行状态，如果还是TaskStatus.RUNNING则触发告警
     * 3.如果在10秒内任务反馈模块已经收到该任务执行成功的消息的情况下会删除该KEY
     *
     * @param taskId    任务ID
     * @param messageId 消息ID，作为任务执行ID使用
     */
    public void onSend(String taskId, String messageId) {
        log.info("TaskStatusChangeCallback.onSend");
        try {
            taskMessageSender.sendCustomMessage(
                    TaskConstants.TASK_EXPIRED_TOPIC, "",
                    (taskId + "-" + messageId).getBytes("utf-8"), 3);
            taskParamStore.setInProgressId(taskId, messageId);
        } catch (Exception e) {
            log.error("消息发送监听回调onSend出错",e);
        }

    }

    public void onRunning(String taskId) {
        log.info(String.format("TaskStatusChangeCallback.onRunning.%s", taskId));
    }

    /**
     * 阶梯及计划任务执行一次成功回调
     * @param taskId
     */
    public void onSuccess(String taskId) {
        log.info(String.format("TaskStatusChangeCallback.onSuccess.%s", taskId));
        TaskContext taskContext = null;
        try {
            taskContext = taskParamStore.get(taskId);
            if (null != taskContext) {
                TaskType taskType = taskContext.getTaskType();
                switch (taskType) {
                    case PLAN:
                        // 计划任务如果设置只执行一次的情况 下，成功一次则删除任务
                        if (taskContext.isRunOnce()) {
                            TaskOperationManager.delScheduleTask(taskId);
                            taskParamStore.setStatus(taskId, TaskStatus.DONE);
                        }
                        else{
                            taskParamStore.setStatus(taskId, TaskStatus.SUCCESS);
                        }
                        break;
                    case LADDER:
                        //  阶梯任务如果设置只执行一次的情况下，成功一次则删除任务
                        if (taskContext.isRunOnce()) {
                            TaskOperationManager.delScheduleTask(taskId);
                            taskParamStore.setStatus(taskId, TaskStatus.DONE);
                        } else {
                            List<TaskDefine> taskDefines = TaskOperationManager.queryScheduleTask(taskId);
                            int maxLadder = 0;
                            // 阶梯任务最后一次执行完成后，删除任务
                            boolean isDone = false;
                            for (TaskDefine taskDefine : taskDefines) {
                                String taskTag = taskDefine.getTaskTag();
                                String[] tags = taskTag.split("-");
                                int ladder = Integer.valueOf(tags[tags.length - 1]);
                                if (ladder > maxLadder) {
                                    maxLadder = ladder;
                                    isDone = taskDefine.getRunTimes() > 0;
                                }
                            }
                            if (isDone) {
                                TaskOperationManager.delScheduleTask(taskId);
                                taskParamStore.setStatus(taskId, TaskStatus.DONE);
                            }
                            else{
                                taskParamStore.setStatus(taskId, TaskStatus.SUCCESS);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            log.error("阶梯及计划任务执行一次成功回调onSuccess出错",e);
        }
    }

    /**
     * 即时及定时任务执行成功回调
     *
     * @param taskId
     */
    public void onDone(String taskId) {
        try {
            log.info("TaskStatusChangeCallback.onDone." + taskId);
            TaskContext taskContext = taskParamStore.get(taskId);
            taskParamStore.setStatus(taskId, TaskStatus.DONE);
            if (null != taskContext) {
                TaskType taskType = taskContext.getTaskType();
                taskParamStore.setStatus(taskId, TaskStatus.DONE);
                switch (taskType) {
                    case REAL:
                        // 即时任务没有调度信息，无需删除
                        break;
                    case TIMING:
                        TaskOperationManager.delScheduleTask(taskId);
                        break;
                    default:
                        log.info("TaskStatusChangeCallback.onDone");
                }


            }
        } catch (Exception e) {
            log.error("即时及定时任务执行成功回调onDone出错",e);
        }
    }

    public void onCancel(String taskId) {
        log.info("TaskStatusChangeCallback.onCancel." + taskId);
    }

    public void onFail(String taskId) {
        log.info("TaskStatusChangeCallback.onFail." + taskId);
    }

    /**
     * 任务执行超时，触发告警入口
     *
     * @param taskId
     */
    public void onExpired(String taskId) {
        log.info("TaskStatusChangeCallback.onExpired." + taskId);
        // TODO: 2016/9/12 告警方式
    }
}
