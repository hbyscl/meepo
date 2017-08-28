package org.cheng.meepo.task.scheduler.mq;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.common.message.MessageExt;
import org.cheng.meepo.task.constant.TaskConstants;
import org.cheng.meepo.task.constant.TaskStatus;
import org.cheng.meepo.task.scheduler.monitor.CallbackThreadPool;
import org.cheng.meepo.task.scheduler.monitor.TaskStatusChangeCallback;
import org.cheng.meepo.task.storage.TaskParamStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by ChengLi on 2016/9/12.
 * 任务执行超时RocketMQ消息接收
 */
@Component
public class TaskExpiredListener implements MessageListenerOrderly {

    private static Logger log = LoggerFactory.getLogger(TaskExpiredListener.class);

    // 设置任务状态变化回调
    @Autowired
    private TaskStatusChangeCallback taskStatusChangeCallback;

    @Autowired
    private TaskParamStore taskParamStore;

    @Autowired
    private CallbackThreadPool callbackThreadPool;

    @PostConstruct
    private void initConsumer() {
        MQConsumerFactory.getInstance().startClusterConsumer("TASK_EXPIRED",
                TaskConstants.TASK_EXPIRED_TOPIC,
                "*",
                this);
    }

    /**
     * 接收消息回调
     *
     * @param msgs    {TASK_ID}-{PROGRESS_ID}字符转byte[]
     * @param context
     * @return
     */
    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        context.setAutoCommit(true);
        for (MessageExt msg : msgs) {
            try {
                String[] body = new String(msg.getBody(), "utf-8").split("-");
                final String taskId = body[0];
                String progressId = body[1];
                // 查询STORE中任务信息,如果状态是Running并且ProgressId相同的情况下触发告警
                TaskStatus status = taskParamStore.getStatus(taskId);
                String inProgressId = taskParamStore.getInProgressId(taskId);

                if (status.equals(TaskStatus.RUNNING) && progressId.equals(inProgressId)) {
                    log.info(String.format("TaskExpiredListener:监听到任务%s执行失败", taskId));
                    callbackThreadPool.executeCallback(new Runnable() {
                        @Override
                        public void run() {
                            taskStatusChangeCallback.onExpired(taskId);
                        }
                    });
                } else {
                    log.info(String.format("TaskExpiredListener:监听到任务%s执行成功", taskId));
                }

            } catch (Exception e) {
                log.error("任务执行超时RocketMQ消息接收回调出错", e);
            }
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }
}
