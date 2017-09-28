package org.cheng.meepo.task.scheduler.mq;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.common.message.Message;
import org.cheng.meepo.task.constant.TaskConstants;
import org.cheng.meepo.task.dto.ServiceInvokeParam;
import org.cheng.meepo.task.scheduler.monitor.CallbackThreadPool;
import org.cheng.meepo.task.scheduler.monitor.TaskStatusChangeCallback;
import org.cheng.meepo.task.scheduler.remoting.InvokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by ChengLi on 2016/6/19.
 * 任务RocketMQ消息发送
 */
@Service("taskMessageSender")
public class TaskMessageSender implements InvokerService {
    private static Logger log = LoggerFactory.getLogger(TaskMessageSender.class);
    //设置任务状态变化回调
    @Autowired
    private TaskStatusChangeCallback taskStatusChangeCallback;

    @Autowired
    private CallbackThreadPool callbackThreadPool;

    private DefaultMQProducer defaultMQProducer;

    public TaskMessageSender() {
        defaultMQProducer = MQProducerFactory.getInstance().getDefaultMQProducer();
    }


    /**
     * 由任务调度被动触发发送消息
     *
     * @param params 任务执行参数：被调用的Dubbo接口名+#+任务ID
     */
    public void sendMsg(String params) {
        try {
            String[] strParams = params.split("#");
            String interfaceName = strParams[0];
            final String taskId = strParams[1];

            //向MQ中发送消息
            Message message = new Message(TaskConstants.TASK_EXECUTE_MESSAGE_TOPIC,
                    interfaceName, taskId.getBytes("utf-8"));
            final String messageId = defaultMQProducer.send(message).getMsgId();
            // 触发发送成功事件
            callbackThreadPool.executeCallback(new Runnable() {
                @Override
                public void run() {
                    taskStatusChangeCallback.onSend(taskId, messageId);
                }
            });


        } catch (Exception e) {
            log.error("由任务调度被动触发发送消息出错",e);
        }

    }

    /**
     * 由程序主动调用发送消息
     *
     * @param taskParams 调用Dubbo服务所需参数
     * @param taskId     任务ID
     */
    @Override
    public void doit(ServiceInvokeParam taskParams, final String taskId) {
        try {
            //向MQ中发送消息
            Message message = new Message(TaskConstants.TASK_EXECUTE_MESSAGE_TOPIC,
                    taskParams.getInterfaceName(), taskId.getBytes("utf-8"));
            final String messageId = defaultMQProducer.send(message).getMsgId();
            // 触发发送成功事件
            callbackThreadPool.executeCallback(new Runnable() {
                @Override
                public void run() {
                    taskStatusChangeCallback.onSend(taskId, messageId);
                }
            });

        } catch (Exception e) {
            log.error("由程序主动调用发送消息出错",e);
        }

    }

    /**
     * 发送自定义消息
     *
     * @param topic 话题
     * @param tags  子话题
     * @param body  内容
     */
    public String sendCustomMessage(String topic, String tags, byte[] body) {
        return sendCustomMessage(topic,tags,body,null);
    }

    /**
     * 发送自定义延时消息
     *
     * @param topic 话题
     * @param tags  子话题
     * @param body  内容
     * @param delayLevel  延时级别(1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h)
     */
    public String sendCustomMessage(String topic, String tags, byte[] body,Integer delayLevel) {
        try {
            //向MQ中发送消息
            Message message = new Message(topic,
                    tags, body);
            if(null != delayLevel){
                message.setDelayTimeLevel(delayLevel);
            }
            return defaultMQProducer.send(message).getMsgId();
        } catch (Exception e) {
            log.error("发送自定义消息出错",e);
        }
        return "";
    }

}
