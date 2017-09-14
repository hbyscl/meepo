package org.cheng.meepo.task.executor;

import org.cheng.meepo.task.constant.TaskStatus;
import org.cheng.meepo.task.constant.TaskType;
import org.cheng.meepo.task.dto.ServiceInvokeParam;
import org.cheng.meepo.task.dto.TaskContext;
import org.cheng.meepo.task.dto.TaskStatusDTO;
import org.cheng.meepo.task.executor.dubbo.GenericInvoke;
import org.cheng.meepo.task.executor.mq.ExecuteFeedbackMessageSender;
import org.cheng.meepo.task.service.TaskService;
import org.cheng.meepo.task.storage.TaskParamStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by ChengLi on 2016/9/1.
 * 具体任务执行处理
 */
@Component
public class TaskExecuteInvoker {

    private static Logger log = LoggerFactory.getLogger(TaskExecuteInvoker.class);

    @Autowired
    private TaskParamStore taskParamStore;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ExecuteFeedbackMessageSender sender;

    public TaskExecuteInvoker() {
        log.info("TaskExecuteCallback.TaskExecuteCallbackImpl初始化");
    }

    /**
     * 执行具体的Dubbo服务
     *
     * @param taskId
     */
    public void exec(String taskId, String messageId) {
        log.info("TaskExecuteCallback.exec." + taskId);
        TaskContext taskContext;
        try {
            taskContext = taskParamStore.get(taskId);
            // 阶梯任务如果设定只执行一次，并且已经有成功记录的情况下忽略二次执行请求
            if (idempotentCheck(taskId, taskContext, messageId)) {
                ServiceInvokeParam serviceInvokeParam = taskContext.getServiceInvokeParam();
                // 开始执行任务
                sender.sendMsg(new TaskStatusDTO(taskId, TaskStatus.RUNNING));

                // 泛化调用Dubbo服务
                Object invoke = GenericInvoke.getInstance().invoke(
                        serviceInvokeParam.getInterfaceName(),
                        serviceInvokeParam.getMethodName(),
                        serviceInvokeParam.getParamsType(),
                        serviceInvokeParam.getParams()
                );

                // 保存任务执行结果
                // TODO: 2016/9/29 是否应该重新考虑结果直接调用TaskService的这种保存策略？
                taskService.saveResult(taskId, invoke);

                // 有下一步任务需要执行时创建即时任务
                ServiceInvokeParam nextService = serviceInvokeParam.getNextService();
                if (null != nextService) {
                    taskService.create(nextService, TaskType.REAL, "");
                }

                // 任务执行成功,如果是阶梯或计划任务则发送执行成功消息，其它发送执行完成消息
                TaskType taskType = taskContext.getTaskType();
                sender.sendMsg(new TaskStatusDTO(taskId,
                        (TaskType.LADDER.equals(taskType) || TaskType.PLAN.equals(taskType))
                                ? TaskStatus.SUCCESS : TaskStatus.DONE));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 任务执行失败
            sender.sendMsg(new TaskStatusDTO(taskId, TaskStatus.FAIL));
        }

    }

    /**
     * 任务幂等性检查
     *
     * @param taskContext
     * @return
     */
    private boolean idempotentCheck(String taskId, TaskContext taskContext, String nowProgressId) {
        // TODO: 2016/9/2 简单判断任务状态
        if (null == taskContext) {
            log.error(String.format("存储中没有发现任务%s必须的执行参数,忽略本次执行请求", taskId));
            return false;
        }
        String progressId = taskContext.getProgressId();

        // 执行ID不为空，且与数据存储中的不对应时认为当前任务无效
        if (null != progressId && !"".equals(progressId) && !progressId.equals(nowProgressId)) {
            log.info(String.format("任务%s执行ID不匹配,忽略本次执行请求,存储中的执行ID为%s,当前为%s",
                    taskId, progressId, nowProgressId));
            return false;
        }

        // 状态为完成或者取消时，任务无效
        TaskStatus taskStatus = taskContext.getTaskStatus();
        if (taskStatus.equals(TaskStatus.DONE) || taskStatus.equals(TaskStatus.CANCEL)) {
            log.info(String.format("任务%s当前状态为%s,忽略本次执行请求", taskId, taskStatus.getDesc()));
            return false;
        }
        // 状态为成功且任务类型为只执行成功一次的情况下，任务无效
        TaskType taskType = taskContext.getTaskType();
        if (taskStatus.equals(TaskStatus.SUCCESS) &&
                (taskType.equals(TaskType.REAL) || taskType.equals(TaskType.TIMING) || taskContext.isRunOnce())) {
            log.info(String.format("任务%s只允许成功运行一次,忽略本次执行请求", taskId));
            return false;
        }

        return true;
    }
}
