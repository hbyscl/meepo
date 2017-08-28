package org.cheng.meepo.service;

import org.springframework.stereotype.Service;

/**
 * Created by ChengLi on 2016/6/17.
 */
@Service
public class TaskTestServiceTwoImpl implements TaskTestServiceTwo {
    @Override
    public Boolean transform(String a, String b) {
        System.out.println("TaskTestServiceTwoImpl.transform");
        return true;
    }

    @Override
    public String approval(String a) {
        System.out.println("TaskTestServiceTwoImpl.approval");
        return "approval success";
    }
}
