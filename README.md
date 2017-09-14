# 这是一个基于Dubbo的分布式任务调度系统。
<h3>meepo：</h3>
来自于Dota次元的英雄，它最多拥有5个分身，可以同时执行（Gank）多项任务<br/>

![image](https://github.com/hbyscl/meepo/blob/master/design/%E6%9E%B6%E6%9E%84.png)

## 1.1 目标

分布式统一任务调度系统，为基础服务平台提供统一的任务注册管理，运行监控和任务干涉等相关功能，以任务的方式提供服务异步调用。 

## 1.2 名词解释

异步：调用方将需要调用的Dubbo接口相关参数提交给TaskService，TaskService组织好消息后发布到MQ，任务执行方监听到相关消息后以injvm方式执行具体Dubbo服务

发布：由TaskService提供Dubbo接口的方式，负责任务的注册维护和任务执行情况的监控。

执行：在Dubbo服务提供方添加MQ监听模块，实现类AOP功能，动态调用当前提供的服务  

## 2  系统模型

系统总体架构为分布式系统，分为任务调度（TaskScheduler）端和任务执行（TaskExecutor）端。 TaskScheduler提供以Dubbo接口的方式提供API给其它模块调用 TaskExecutor嵌入至普通Dubbo服务中，监听到服务调用消息后，调用本地服务。

### 2.1	架构图解

### 2.2	角色分析

TaskScheduler：负责任务信息组织、任务调度。 TaskExecutor：运行具体任务，执行Dubbo服务 TaskMonitor:通过Redis及Zookeeper的数据分析，提供监控告警服务 TaskAdmin:通过操作Redis及Zookeeper节点数据，实现节点及任务的人工操作

### 2.3	场景分析

#### 即时任务：
立即执行，可作为异步调用服务

#### 阶梯任务：

整体流程与即时任务相同，不同的是在TaskService会在（3，5，10，15，30时间单位）后各发送一次消息至MQ，TaskExecutor多次执行指定服务。

#### 定时任务：
整体流程与即时任务相同，不同的是在TaskService会在指定的时间节点（yyyyMMddHHmmss格式）发送一次消息至MQ，TaskExecutor执行指定服务。

#### 计划任务： 
整体流程与即时任务相同，不同的是在TaskService使用CRON表达式（0 0 1 * * ?格式）在指定的时间节点发送一次消息至MQ，TaskExecutor执行指定服务。

### 2.4	实现关键

双方通过MQ进行通讯，TaskScheduler将需要执行的任务发布到MQ，TaskExecutor监听到后执行指定Service，将执行结果发布到MQ由TaskService监听处理。

#### 2.4.1	TaskScheduler

API:提供Dubbo服务给其它模块调用

TaskManager：向ZK（Redis）中添加任务节点数据，不直接参入调度逻辑 节点名为：ip+uuid+ZK序列号 节点内容为：心跳参数

ScheduleManager：调度核心模块，通过定时向ZK发送心跳数据实现集群功能。	根据任务节点数据创建本地SpringTask任务 任务节点名为：本地消息发送Bean+方法名+任务类型+任务ID 如：sendTaskMsg#senMsg#PLAN#EB37B0 内容为：任务调度信息，执行次数，执行服务节点等信息

SpringTask：本地任务触发后向MQ发送服务执行消息， 发送的任务消息话题为任务目标接口名如： （org.cheng.meepo.task.service.TaskService） 内容为TaskId 2.4.2	TaskExecutor

Listener：向MQ中监听本容器提供的服务消息 如：当前容器只启动了TaskService服务，则只会收到Topic为	TaskService的消息

Invoker：通过监听拿到的TaskID，向Redis中查询任务执行情况，实现初步幂	等性判断。从Redis中获取到服务执行参数以后，泛化调用指定服务。	将调用结果（成功与否）发送至MQ，内容为执行的TaskId。


### 3.1	创建任务

根据传入的调用服务参数、任务类型、执行方式定义等参数创建任务，返回任务ID </br>

### 3.2	取消任务

根据指定任务ID取消该任务  </br>

### 3.3	查询任务

根据任务ID查询任务执行情况  </br>

## 4 服务监控
TODO:尚未实现

监控服务通过查询zookeeper节点及Redis中存储的数据，动态提供任务执行数据，供图表显示。 根据监听任务执行状态，发布告警信息。
## 5 运行Demo
**依赖Dubbo运行环境以及RocketMQ**
- 运行task-schedule 启动调度模块
task-schedule/test/org.cheng.meepo.task.scheduler.RunSchedule
- 启动带有task-executor的Dubbo服务
task-test/test/org.cheng.meepo.RunExecutor
- 运行JUnit测试用例
task-test/test/org.cheng.meepo.TestTaskService
