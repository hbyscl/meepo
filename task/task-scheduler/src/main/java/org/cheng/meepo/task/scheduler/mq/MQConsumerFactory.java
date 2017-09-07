package org.cheng.meepo.task.scheduler.mq;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import org.cheng.meepo.task.util.PropertiesParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * Created by ChengLi on 2016/9/8.
 * 通用MQ消费者配置
 */
public class MQConsumerFactory {

    private static Logger log = LoggerFactory.getLogger(MQConsumerFactory.class);


    private static MQConsumerFactory ourInstance = new MQConsumerFactory();

    public static MQConsumerFactory getInstance() {
        return ourInstance;
    }

    private MQConsumerFactory() {
    }

    /**
     * 启动全量自定义配置消费者
     * @param consumerGroup
     * @param topic
     * @param subTopic
     * @param fromWhere
     * @param messageModel
     * @param callback
     */
    public void startConsumer(String consumerGroup,String topic, String subTopic,
                              ConsumeFromWhere fromWhere,
                              MessageModel messageModel,
                              MessageListenerOrderly callback) {
        try {

            log.info("MQConsumerFactory.initConsumer."+topic);
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
            consumer.setNamesrvAddr(PropertiesParse.getProperty("rocketmq.namesrvAddr"));
            consumer.setInstanceName(consumerGroup+"-" + InetAddress.getLocalHost().getHostAddress());
            consumer.setConsumeFromWhere(fromWhere);
            consumer.setMessageModel(messageModel);
            consumer.subscribe(
                    topic,
                    null == subTopic || "".equals(subTopic) ? "*":subTopic
            );
            consumer.registerMessageListener(callback);
            consumer.start();
        } catch (Exception e) {
            log.error("启动topick{"+topic+"}消费者出错",e);
        }

    }

    /**
     * 启动集群消费
     * @param consumerGroup
     * @param topic
     * @param tags
     * @param callback
     */
    public void startClusterConsumer(String consumerGroup,String topic, String tags,
                              MessageListenerOrderly callback) {

        startConsumer(consumerGroup,topic,
                tags,ConsumeFromWhere.CONSUME_FROM_TIMESTAMP,
                MessageModel.CLUSTERING,
                callback);
    }



}
