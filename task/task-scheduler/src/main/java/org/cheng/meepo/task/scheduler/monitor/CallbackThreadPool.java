package org.cheng.meepo.task.scheduler.monitor;

import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by ChengLi on 2016/9/8.
 * 通用的异步回调执行线程池
 */
@Component
public class CallbackThreadPool {
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public void executeCallback(Runnable runnable){
        executorService.execute(runnable);
    }

    public <T> Future<T> submitCallback(Callable<T> callbak){
        return executorService.submit(callbak);
    }
}
