package org.cheng.meepo.task.scheduler.mq;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.common.message.MessageExt;
import org.cheng.meepo.task.constant.TaskStatus;
import org.cheng.meepo.task.constant.TaskConstants;
import org.cheng.meepo.task.dto.TaskStatusDTO;
import org.cheng.meepo.task.scheduler.monitor.CallbackThreadPool;
import org.cheng.meepo.task.scheduler.monitor.TaskStatusChangeCallback;
import org.cheng.meepo.util.SerializerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by ChengLi on 2016/6/30.
 * 任务执行情况反馈RocketMQ消息接收
 */
@Component
public class TaskExecuteFeedbackListener implements MessageListenerOrderly {

    private static Logger log = LoggerFactory.getLogger(TaskExecuteFeedbackListener.class);

    // 设置任务状态变化回调
    @Autowired
    private TaskStatusChangeCallback taskStatusChangeCallback;

    @Autowired
    private CallbackThreadPool callbackThreadPool;


    @PostConstruct
    private void initConsumer() {
            MQConsumerFactory.getInstance().startClusterConsumer("TASK_EXECUTE_FEEDBACK",
                    TaskConstants.TASK_EXECUTE_FEEDBACK_TOPIC,
                    "*",
                    this);
    }

    /**
     * 接收消息回调
     *
     * @param msgs    TaskStatusDTO序列化对象byte[]
     * @param context
     * @return
     */
    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        context.setAutoCommit(true);
        for (MessageExt msg : msgs) {
            try {
                TaskStatusDTO statusDTO = (TaskStatusDTO) SerializerUtil.byte2object(msg.getBody());
                final String id = statusDTO.getTaskId();
                TaskStatus status = statusDTO.getTaskStatus();
                switch (status) {
                    case DONE:
                        callbackThreadPool.executeCallback(new Runnable() {
                            @Override
                            public void run() {
                                taskStatusChangeCallback.onDone(id);
                            }
                        });

                        break;
                    case SUCCESS:
                        callbackThreadPool.executeCallback(new Runnable() {
                            @Override
                            public void run() {
                                taskStatusChangeCallback.onSuccess(id);
                            }
                        });

                        break;
                    case RUNNING:
                        callbackThreadPool.executeCallback(new Runnable() {
                            @Override
                            public void run() {
                                taskStatusChangeCallback.onRunning(id);
                            }
                        });

                        break;
                    case FAIL:
                        callbackThreadPool.executeCallback(new Runnable() {
                            @Override
                            public void run() {
                                taskStatusChangeCallback.onFail(id);
                            }
                        });

                        break;
                    case CANCEL:
                        callbackThreadPool.executeCallback(new Runnable() {
                            @Override
                            public void run() {
                                taskStatusChangeCallback.onCancel(id);
                            }
                        });

                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                log.error("TaskExecuteFeedbackListener接收消息回调出错",e);
            }
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }
}
