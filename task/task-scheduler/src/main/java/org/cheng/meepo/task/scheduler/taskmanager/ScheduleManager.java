package org.cheng.meepo.task.scheduler.taskmanager;

import org.cheng.meepo.task.scheduler.ScheduleMain;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 调度管理器
 * Created by ChengLi on 2016/6/21
 */
public class ScheduleManager {

    private static final transient Logger log = LoggerFactory.getLogger(ScheduleManager.class);


    private static final Map<String, ScheduledFuture<?>> SCHEDULE_FUTURES = new ConcurrentHashMap<>();


    /**
     * 启动定时任务
     *
     * @param taskDefine
     * @param currentTime
     */
    public static void scheduleTask(TaskDefine taskDefine, Date currentTime) {
        scheduleTask(taskDefine.getTargetBean(), taskDefine.getTargetMethod(),
                taskDefine.getCronExpression(), taskDefine.getStartTime(), taskDefine.getPeriod(), taskDefine.getParams(), taskDefine.getTaskTag());
    }

    /**
     * 清除本地调度任务
     *
     * @param existsTaskName
     */
    public static void clearLocalTask(List<String> existsTaskName) {
        for (String name : SCHEDULE_FUTURES.keySet()) {
            if (!existsTaskName.contains(name)) {
                SCHEDULE_FUTURES.get(name).cancel(true);
                SCHEDULE_FUTURES.remove(name);
            }
        }
    }

    /**
     * 启动定时任务
     * 支持：
     * 1 cron时间表达式，立即执行
     * 2 startTime + period,指定时间，定时进行
     * 3 period，定时进行，立即开始
     * 4 startTime，指定时间执行
     *
     * @param targetBean
     * @param targetMethod
     * @param cronExpression
     * @param startTime
     * @param period
     */
    public static void scheduleTask(String targetBean, String targetMethod, String cronExpression, Date startTime, long period, String params, String taskTag) {
        String scheduleKey = buildScheduleKey(targetBean, targetMethod, taskTag);
        try {
            ScheduledFuture<?> scheduledFuture = null;
            ScheduledMethodRunnable scheduledMethodRunnable = buildScheduledRunnable(targetBean, targetMethod, params, taskTag);
            if (scheduledMethodRunnable != null) {
                if (!SCHEDULE_FUTURES.containsKey(scheduleKey)) {
                    if (StringUtils.isNotEmpty(cronExpression)) {
                        Trigger trigger = new CronTrigger(cronExpression);
                        scheduledFuture = TaskOperationManager.getScheduleManager().schedule(scheduledMethodRunnable, trigger);
                    } else if (startTime != null) {
                        if (period > 0) {
                            scheduledFuture = TaskOperationManager.getScheduleManager().scheduleAtFixedRate(scheduledMethodRunnable, startTime, period);
                        } else {
                            scheduledFuture = TaskOperationManager.getScheduleManager().schedule(scheduledMethodRunnable, startTime);
                        }
                    } else if (period > 0) {
                        scheduledFuture = TaskOperationManager.getScheduleManager().scheduleAtFixedRate(scheduledMethodRunnable, period);
                    }
                    SCHEDULE_FUTURES.put(scheduleKey, scheduledFuture);
                    log.debug("Building new schedule task, target bean " + targetBean + " target method " + targetMethod + ".");
                }
            } else {
                log.debug("Bean name is not exists.");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * 组装任务名称（节点名）
     * @param targetBean
     * @param targetMethod
     * @param taskTag
     * @return
     */
    public static String buildScheduleKey(String targetBean, String targetMethod, String taskTag) {
        return targetBean + "#" + targetMethod + (null == taskTag || taskTag.trim().equals("") ? "" : ("#" + taskTag));
    }

    /**
     * 封装任务对象
     *
     * @param targetBean
     * @param targetMethod
     * @return
     */
    private static ScheduledMethodRunnable buildScheduledRunnable(String targetBean, String targetMethod, String params, String taskTag) {
        Object bean;
        ScheduledMethodRunnable scheduledMethodRunnable = null;
        try {
            bean = ScheduleMain.getApplicationcontext().getBean(targetBean);
            scheduledMethodRunnable = _buildScheduledRunnable(bean, targetMethod, params, taskTag);
        } catch (Exception e) {
            log.debug(e.getLocalizedMessage(), e);
        }
        return scheduledMethodRunnable;
    }

    private static ScheduledMethodRunnable buildScheduledRunnable(Object bean, String targetMethod, String params, String taskTag) {
        ScheduledMethodRunnable scheduledMethodRunnable = null;
        try {
            scheduledMethodRunnable = _buildScheduledRunnable(bean, targetMethod, params, taskTag);
        } catch (Exception e) {
            log.debug(e.getLocalizedMessage(), e);
        }
        return scheduledMethodRunnable;
    }


    private static ScheduledMethodRunnable _buildScheduledRunnable(Object bean, String targetMethod, String params, String taskTag) throws Exception {

        Assert.notNull(bean, "target object must not be null");
        Assert.hasLength(targetMethod, "Method name must not be empty");

        Method method;
        ScheduledMethodRunnable scheduledMethodRunnable;

        Class<?> clazz;
        if (AopUtils.isAopProxy(bean)) {
            clazz = AopProxyUtils.ultimateTargetClass(bean);
        } else {
            clazz = bean.getClass();
        }
        if (params != null) {
            method = ReflectionUtils.findMethod(clazz, targetMethod, String.class);
        } else {
            method = ReflectionUtils.findMethod(clazz, targetMethod);
        }

        Assert.notNull(method, "can not find method named " + targetMethod);
        scheduledMethodRunnable = new ScheduledMethodRunnable(bean, method, params, taskTag);
        return scheduledMethodRunnable;
    }
}
