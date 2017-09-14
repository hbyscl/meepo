package org.cheng.meepo.task.executor.mq;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import org.cheng.meepo.task.constant.TaskConstants;
import org.cheng.meepo.task.executor.TaskExecuteInvoker;
import org.cheng.meepo.task.executor.dubbo.IFoundProviderService;
import org.cheng.meepo.task.util.PropertiesParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.List;

/**
 * Created by ChengLi on 2016/6/30.
 * 任务执行RocketMQ消息接收
 * <p>
 * 需要扫描Dubbo提供的服务
 */
@Service
public class ExecuteMessageListener implements MessageListenerOrderly {

    private static Logger log = LoggerFactory.getLogger(ExecuteMessageListener.class);


    @Autowired
    private TaskExecuteInvoker executeCallback;

    @Autowired
    private IFoundProviderService foundProviderService;

    /**
     * 初始化监听
     */
    @PostConstruct
    private void initConsumer() {
        try {
            log.info("TaskExecuteListener.initConsumer");

            // 获取本机提供的Dubbo服务列表
            List<String> dubboServiceList = foundProviderService.list();
            int serviceCount = dubboServiceList.size();

            // 只有在本机有提供Dubbo服务的情形下才开启监听服务
            if(serviceCount > 0){
                DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("TASK_EXECUTEOR");
                consumer.setNamesrvAddr(PropertiesParse.getProperty("rocketmq.namesrvAddr"));
                consumer.setInstanceName("TaskExecuteListener-" + InetAddress.getLocalHost().getHostAddress());
                consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_TIMESTAMP);
                consumer.setMessageModel(MessageModel.CLUSTERING);
                StringBuilder sub = new StringBuilder();
                for (int i = 0; i < serviceCount; i++) {
                    if(i > 0){
                        sub.append(" || ");
                    }
                    sub.append(dubboServiceList.get(i));
                }
                String subExpression = sub.toString();
                String topic = TaskConstants.TASK_EXECUTE_MESSAGE_TOPIC;
                log.info("RocketMQ Consumer初始化，添加监听服务："+topic+"="+subExpression);

                consumer.subscribe(
                        topic,
                        // 通过读取Zookeeper获取当前jvm发布的Dubbo服务，作为子话题添加订阅
                        subExpression
                );
                consumer.registerMessageListener(this);
                consumer.start();
            }
            else {
                log.info("TaskExecuteListenerImpl取消初始化，没有找到本地提供的Dubbo服务");
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 接收消息回调
     *
     * @param msgs    TaskId 字符串序列化对象byte[]
     * @param context
     * @return
     */
    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        context.setAutoCommit(true);
        for (final MessageExt msg : msgs) {
            new Runnable() {
                @Override
                public void run() {
                    try {
                        executeCallback.exec(new String(msg.getBody(), "utf-8"),msg.getMsgId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.run();
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }
}
