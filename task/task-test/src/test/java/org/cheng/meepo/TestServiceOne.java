package org.cheng.meepo;

import org.cheng.meepo.service.TaskTestServiceOne;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by ChengLi on 2016/6/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:dubbo-demo-consumer.xml"
})
public class TestServiceOne {

    @Autowired
    TaskTestServiceOne taskTestServiceOne;

    @Test
    public void testCreateBO(){
        String bo = taskTestServiceOne.createBo("1", "b", 1);
        System.out.println("bo = " + bo);
    }
}
