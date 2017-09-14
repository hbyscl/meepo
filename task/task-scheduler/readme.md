任务组织者，任务API提供方

----------运行方法------------
1.使用maven-assembly-plugin打包生成task-scheduler-assembly.tar.gz文件
在使用package打包命令时需要添加profile参数，如：package -P dev   , dev对应到src/main/assembly中下的bin/dev及conf/dev目录
同理还有其它的test/pre/prd等环境配置
2.解压缩后运行bin/start.sh即可启动服务，发布任务调度Dubbo接口
3.使用IDE的RunAsApplication方式启动，Main方法指定为：com.alibaba.dubbo.container.Main
  将src/main/assembly/conf/test目录设置为Resource目录,连接的就是dev环境

