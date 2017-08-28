<h1>这是一个基于Dubbo的分布式任务调度系统。</h1>
<h3>meepo：</h3>
来自于Dota次元的英雄，它最多拥有5个分身，可以同时执行（Gank）多项任务<br/>

<h3>模块划分：</h3>
meepo<br/>
&nbsp;&nbsp;&nbsp;&nbsp;dubbo-api--------------API定义及必须的公共工具类<br/>
&nbsp;&nbsp;&nbsp;&nbsp;task-------------------调度核心<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;task-executor-------------------任务执行模块<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;task-scheduler-------------------任务调度模块<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;task-sdk-------------------SDK封装<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;task-storage-------------------调度信息存储<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;task-storage-api-------------------调度信息存储API定义<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;task-storage-mongodb-------------------调度信息存储MongoDB实现<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;task-storage-redis-------------------调度信息存储redis实现<br/>
