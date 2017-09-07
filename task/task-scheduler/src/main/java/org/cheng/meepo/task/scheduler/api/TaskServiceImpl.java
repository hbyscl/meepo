package org.cheng.meepo.task.scheduler.api;

import org.cheng.meepo.task.exception.TaskDataException;
import org.cheng.meepo.task.exception.TaskServiceException;
import org.cheng.meepo.task.constant.TaskStatus;
import org.cheng.meepo.task.constant.TaskType;
import org.cheng.meepo.task.dto.ServiceInvokeParam;
import org.cheng.meepo.task.dto.TaskContext;
import org.cheng.meepo.task.scheduler.ScheduleMain;
import org.cheng.meepo.task.scheduler.exception.TaskOperationException;
import org.cheng.meepo.task.scheduler.monitor.CallbackThreadPool;
import org.cheng.meepo.task.scheduler.monitor.TaskStatusChangeCallback;
import org.cheng.meepo.task.scheduler.mq.TaskMessageSender;
import org.cheng.meepo.task.scheduler.taskmanager.TaskDefine;
import org.cheng.meepo.task.scheduler.taskmanager.TaskOperationManager;
import org.cheng.meepo.task.service.TaskService;
import org.cheng.meepo.task.storage.TaskParamStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by ChengLi on 2016/6/19.
 */
@Service("taskService")
public class TaskServiceImpl implements TaskService {

    private static Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private TaskMessageSender taskMessageSender;

    @Autowired
    private TaskParamStore taskParamStore;

    @Autowired
    private ScheduleMain manager;

    @Autowired
    private TaskStatusChangeCallback taskStatusChangeCallback;

    @Autowired
    private CallbackThreadPool callbackThreadPool;

    @Override
    public String create(ServiceInvokeParam serviceInvokeParam, TaskType taskType, String executeDefine) throws TaskServiceException {
        // TODO: 2016/6/19 生成全局唯一递增的TASKID,目前使用UUID
        String taskId = UUID.randomUUID().toString();
        // 保存任务执行参数
        TaskContext taskContext = new TaskContext();
        taskContext.setExecuteDefine(executeDefine);
        taskContext.setServiceInvokeParam(serviceInvokeParam);
        taskContext.setTaskId(taskId);
        taskContext.setTaskType(taskType);
        try {
            if (!taskParamStore.save(taskId, taskContext)) {
                throw new TaskServiceException("保存任务执行参数失败");
            }
        } catch (TaskDataException e) {
            throw new TaskServiceException("保存任务执行参数失败", e);
        }
        try {
            switch (taskType) {
                case REAL:
                    // 即时任务，直接发送消息
                    taskMessageSender.sendMsg(serviceInvokeParam, taskId);
                    break;
                case TIMING:
                    // 创建定时任务
                    TaskDefine taskDefine = getTaskDefine(serviceInvokeParam.getInterfaceName()
                            + "." + serviceInvokeParam.getMethodName() + "#" + taskId);
                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    taskDefine.setStartTime(dateFormat.parse(executeDefine));
                    taskDefine.setTaskTag("TIMING-" + taskId);
                    TaskOperationManager.addScheduleTask(taskDefine);
                    break;
                case LADDER:
                    // LADDER("阶梯任务",2): 0，1，3，5，10 字符串以逗号分隔，秒作为时间单位
                    String[] arrLadder = executeDefine.split(",");
                    // 获取Zookeeper时间
                    Long nowTime = manager.getZkManager().getZKTime();
                    for (String ladder : arrLadder) {
                        taskDefine = getTaskDefine(serviceInvokeParam.getInterfaceName()
                                + "." + serviceInvokeParam.getMethodName() + "#" + taskId);
                        taskDefine.setStartTime(new Date(nowTime + Integer.valueOf(ladder) * 1000));
                        taskDefine.setTaskTag("LADDER-" + taskId + "-" + ladder);
                        TaskOperationManager.addScheduleTask(taskDefine);
                    }
                    break;
                case PLAN:
                    // 根据CRON表达式创建循环任务
                    taskDefine = getTaskDefine(serviceInvokeParam.getInterfaceName()
                            + "." + serviceInvokeParam.getMethodName() + "#" + taskId);
                    taskDefine.setCronExpression(executeDefine);
                    taskDefine.setTaskTag("PLAN-" + taskId);
                    TaskOperationManager.addScheduleTask(taskDefine);
                    break;
                default:
                    log.error("不支持的任务类型");
            }
        } catch (Exception e) {
            try {
                taskParamStore.delete(taskId);
            } catch (TaskDataException e1) {
                throw new TaskServiceException("创建任务调度出错，删除任务信息失败", e);
            }
            throw new TaskServiceException("创建任务调度出错", e);
        }
        return taskId;
    }

    @Override
    public Boolean cancel(final String taskId) throws TaskServiceException {
        try {
            TaskContext taskContext = taskParamStore.get(taskId);
            taskParamStore.setStatus(taskId, TaskStatus.CANCEL);
            if (null == taskContext) {
                return true;
            }
            TaskOperationManager.delScheduleTask(taskId);
            callbackThreadPool.executeCallback(new Runnable() {
                @Override
                public void run() {
                    taskStatusChangeCallback.onCancel(taskId);
                }
            });
            return true;
        } catch (Exception e) {
            log.error("取消任务{" + taskId + "}出错", e);
            throw new TaskServiceException("取消任务出错", e);
        }

    }

    @Override
    public List<TaskContext> list() throws TaskServiceException {
        // TODO: 2016/9/12 获取所有任务列表
        try {
            List<TaskDefine> taskDefines = TaskOperationManager.queryScheduleTask();
            for (TaskDefine taskDefine : taskDefines) {
                String params = taskDefine.getParams();
                String[] strParams = params.split("#");
                String interfaceName = strParams[0];
                String taskId = strParams[1];
            }
        } catch (TaskOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public TaskContext get(String taskId) throws TaskServiceException {
        return null;
    }

    @Override
    public List<TaskContext> search(ServiceInvokeParam serviceInvokeParam) throws TaskServiceException {
        return null;
    }

    @Override
    public Boolean saveResult(String taskId, Object result) throws TaskServiceException {
        return taskParamStore.setResult(taskId, result);
    }

    @Override
    public Object getResult(String taskId) throws TaskServiceException {
        return taskParamStore.get(taskId).getExecuteResult();
    }

    private TaskDefine getTaskDefine(String scheduleParam) {
        TaskDefine taskDefine = new TaskDefine();
        taskDefine.setTargetBean("taskMessageSender");
        taskDefine.setTargetMethod("sendMsg");
        taskDefine.setParams(scheduleParam);
        return taskDefine;
    }
}
