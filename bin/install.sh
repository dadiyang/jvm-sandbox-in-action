#!/bin/bash
## 用于编译安装模块到默认的 sandbox 用户模块路径下
cd ..
mvn clean package -DskipTests -s ~/.m2/ali.xml 
cp target/jvm-sandbox-in-action-0.0.1-SNAPSHOT-jar-with-dependencies.jar ~/.opt/sandbox/module/
cd bin
