package org.cheng.meepo.task.scheduler;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.common.message.Message;
import org.cheng.meepo.task.constant.TaskStatus;
import org.cheng.meepo.task.constant.TaskConstants;
import org.cheng.meepo.task.dto.TaskStatusDTO;
import org.cheng.meepo.task.util.SerializerUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by ChengLi on 2016/6/17.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {
//        "classpath:application-mq.xml"
//})
public class TestMsgSender {

    private static DefaultMQProducer defaultMQProducer;

    private static void initProducer() {
        try {
            System.out.println("TaskMessageSender.initProducer");
            defaultMQProducer = new DefaultMQProducer("TaskMessageSender");
            defaultMQProducer.setNamesrvAddr("192.168.0.224:9876");
            defaultMQProducer.setInstanceName("TaskMessageSender-" +
                    InetAddress.getLocalHost().getHostAddress());
            defaultMQProducer.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        initProducer();
        int i = 0;
        while (true) {
            System.out.println(++i);
            TaskStatusDTO taskStatus = new TaskStatusDTO(
                    "" + i, randomStatus(i)
            );
            Message message = new Message(TaskConstants.TASK_EXECUTE_FEEDBACK_TOPIC,
                    SerializerUtil.object2byte(taskStatus));
            defaultMQProducer.send(message);
            Thread.sleep(2000l);
        }

    }

    public static TaskStatus randomStatus(int i) {
        if (i % 3 == 0) {
            return TaskStatus.CANCEL;
        }
        if (i % 2 == 0) {
            return TaskStatus.DONE;
        }
        return TaskStatus.FAIL;
    }

}
