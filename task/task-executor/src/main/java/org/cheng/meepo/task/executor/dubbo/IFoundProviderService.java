package org.cheng.meepo.task.executor.dubbo;

import java.util.List;

/**
 * Created by ChengLi on 2016/9/5.
 * 获取本机发布的Dubbo服务方法
 */
public interface IFoundProviderService {
    /**
     * 获取服务列表
     * @return
     */
    public List<String> list();
}
