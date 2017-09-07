package org.cheng.meepo.task.scheduler.mq;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import org.cheng.meepo.task.util.PropertiesParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * Created by ChengLi on 2016/9/8.
 */
public class MQProducerFactory {
    private static Logger log = LoggerFactory.getLogger(MQProducerFactory.class);


    private static MQProducerFactory ourInstance = new MQProducerFactory();

    public static MQProducerFactory getInstance() {
        return ourInstance;
    }

    private MQProducerFactory() {
    }



    private DefaultMQProducer defaultMQProducer;
    /**
     * 初始化RocketMQ消息发送
     */
    private void initProducer() {
        try {
            log.info("TaskMessageSender.initProducer");
            defaultMQProducer = new DefaultMQProducer("TaskMessageSender");
            defaultMQProducer.setNamesrvAddr(PropertiesParse.getProperty("rocketmq.namesrvAddr"));
            defaultMQProducer.setInstanceName("TaskMessageSender-" +
                    InetAddress.getLocalHost().getHostAddress());
            defaultMQProducer.start();
        } catch (Exception e) {
            log.error("初始化RocketMQ消息发送出错",e);
        }
    }

    public DefaultMQProducer getDefaultMQProducer() {
        synchronized (MQProducerFactory.class){
            if(null ==  defaultMQProducer){
                initProducer();
           }
        }
        return defaultMQProducer;
    }
}
