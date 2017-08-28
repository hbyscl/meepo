任务执行方，与Dubbo服务配合使用

----provider XML配置示例,该示例是使用MongoDB作为任务数据存储----

<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mongo="http://www.springframework.org/schema/data/mongo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
	http://code.alibabatech.com/schema/dubbo http://code.alibabatech.com/schema/dubbo/dubbo.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/data/mongo http://www.springframework.org/schema/data/mongo/spring-mongo.xsd">

    <context:component-scan base-package="org.cheng.meepo"/>

    <!--载入配置文件，可供程序内读取-->
    <bean id="configBean" class="org.cheng.meepo.util.PropertiesParse">
        <property name="locations">
            <list>
                <!--<value>classpath:redis.properties</value>-->
                <value>classpath:dubbo.properties</value>
                <value>classpath:rocketmq.properties</value>
                <value>classpath:mongodb.properties</value>
            </list>
        </property>
    </bean>


    <!--配置REDIS数据存储方式-->
    <!--<bean id="redisTaskParamStore" class="org.cheng.meepo.task.storage.RedisTaskParamStore"/>-->

    <!--配置MongoDB数据存储方式-->
    <mongo:mongo host="${mongo.host}" port="${mongo.port}">
    </mongo:mongo>
    <bean id="taskMongoTemplate" class="org.springframework.data.mongodb.core.MongoTemplate">
        <constructor-arg ref="mongo" />
        <constructor-arg value="${mongo.db.name}" />
    </bean>
    <bean id="taskParamStore" class="org.cheng.meepo.task.storage.MongodbTaskParamStore"/>

    <!--配置通过读取Dubbo Provider XML配置文件方式发现本地Dubbo服务-->
    <bean id="foundProviderService" class="org.cheng.meepo.task.executor.dubbo.FoundProviderXMLService"/>

    <dubbo:service interface="org.cheng.meepo.service.TaskTestServiceOne" ref="taskTestServiceOneImpl"/>
    <dubbo:service interface="org.cheng.meepo.service.TaskTestServiceTwo" ref="taskTestServiceTwoImpl"/>

    <dubbo:reference id="taskService" interface="org.cheng.meepo.task.service.TaskService" />

</beans>