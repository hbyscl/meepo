任务数据存储方案

以jar或源文件的方式提供给task-schedule、task-executor使用

------配置数据存储方案示例-----


<!--载入配置文件，可供程序内读取-->
    <bean id="configBean" class="org.cheng.meepo.util.PropertiesParse">
        <property name="locations">
            <list>
                <!--<value>classpath:redis.properties</value>-->
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