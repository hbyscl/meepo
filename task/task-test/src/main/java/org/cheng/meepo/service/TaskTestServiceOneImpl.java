package org.cheng.meepo.service;

import org.springframework.stereotype.Service;

/**
 * Created by ChengLi on 2016/6/17.
 */
@Service
public class TaskTestServiceOneImpl implements TaskTestServiceOne {
    @Override
    public String createBo(String a, String b, Integer c) {
        System.out.println("TaskTestServiceOneImpl.createBo");
        return a+","+b+","+c;
    }

    @Override
    public Boolean updateBo(String a, String b, Integer c) {
        System.out.println("TaskTestServiceOneImpl.updateBo");
        return true;
    }
}
