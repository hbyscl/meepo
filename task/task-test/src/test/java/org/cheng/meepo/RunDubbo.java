package org.cheng.meepo;

import com.alibaba.dubbo.container.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 根据resources/META-INF/spring下的配置
 * 启动Schedule
 * 启动Executor
 */
public class RunDubbo {

    private static Logger log = LoggerFactory.getLogger(RunDubbo.class);

    public static void main(String[] args) {
        Main.main(args);
    }
}
