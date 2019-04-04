# JVM-SANDBOX 实战项目

本项目基于 [JVM-SANDBOX](https://github.com/alibaba/jvm-sandbox) 开发扩展模块

# 模块目录

|模块名|功能|
|----|----|
|time-tunnel|时间隧道，记录方法的调用，支持根据 id 进行方法调用回放|
|call-stack-time-consume|调用堆栈及耗时|
|time-consume|调用栈中每个方法调用的耗时|

# 脚本

bin 目录中提供了快速启动的脚本

使用的前提是，根据官方建议的方法将 sandbox 安装在默认的路径下，即 ~/.opt/sandbox

|脚本|功能|
|----|----|
|install.sh|安装模块|
|shutdown.sh.sh|卸载 jvm-sandbox|
|recordtimetunnel.sh pid classPattern behaviorPattern|记录方法调用上下文|
|reinvoke.sh pid id|重放指定id的方法调用|
|listRecords.sh.sh pid|查看方法调用的记录|
|callStackTimeConsume.sh pid classPattern behaviorPattern|开启调用堆栈及耗时|
|timeconsume.sh pid classPattern behaviorPattern|打印方法调用耗时|

先执行 install.sh 会自动编译并将模块安装到 ~/.opt/sandbox/module 目录下

注：在脚本传参使用通配符记得加引号，如：

`./recordtimetunnel.sh 47166 Clock '*'`

开启模块功能的脚本会用到的参数：
 
1. pid 所有脚本必须
2. 类匹配模式，除了 shutdown 和 listRecords 其他都要
3. 方法匹配模式，除了 shutdown 和 listRecords 其他都要

# 测试效果

用于测试效果，可以在 bin/demo 里执行 java Clock 启动一个 java 进程

然后执行 jps 查看进程 id

选择要测试的模块对这个进程id进行测试，观察其输出