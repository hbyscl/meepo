package org.cheng.meepo.task.executor.mq;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.common.message.Message;
import org.cheng.meepo.task.constant.TaskConstants;
import org.cheng.meepo.task.dto.TaskStatusDTO;
import org.cheng.meepo.task.util.PropertiesParse;
import org.cheng.meepo.task.util.SerializerUtil;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by ChengLi on 2016/9/2.
 *
 * RocketMQ消息发送
 */
@Service
public class ExecuteFeedbackMessageSender {

    private DefaultMQProducer defaultMQProducer;

    public ExecuteFeedbackMessageSender() {
        initProducer();
    }

    /**
     * 初始化RocketMQ消息发送
     */
    private void initProducer() {
        try {
            System.out.println("TaskExecuteFeedbackSender.initProducer");
            defaultMQProducer = new DefaultMQProducer("TaskExecuteFeedbackSender");
            defaultMQProducer.setNamesrvAddr(PropertiesParse.getProperty("rocketmq.namesrvAddr"));
            defaultMQProducer.setInstanceName("TaskExecuteFeedbackSender" +
                    InetAddress.getLocalHost().getHostAddress());
            defaultMQProducer.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送任务执行回执
     * @param taskStatusDTO
     */
    public void sendMsg(TaskStatusDTO taskStatusDTO) {
        try {
            //向MQ中发送消息
            Message message = new Message(TaskConstants.TASK_EXECUTE_FEEDBACK_TOPIC,
                    SerializerUtil.object2byte(taskStatusDTO));
            defaultMQProducer.send(message);
        } catch (Exception e) {
            // TODO: 2016/6/19 LOG
            e.printStackTrace();
        }

    }
}
