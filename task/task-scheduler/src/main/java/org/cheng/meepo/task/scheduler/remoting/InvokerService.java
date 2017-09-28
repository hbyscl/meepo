package org.cheng.meepo.task.scheduler.remoting;

import org.cheng.meepo.task.dto.ServiceInvokeParam;

/**
 * Created by Administrator on 2017/9/28.
 */
public interface InvokerService {
    /**
     * 执行远程服务
     * @param taskParams
     * @param taskId
     */
    void doit(ServiceInvokeParam taskParams, final String taskId);
}
