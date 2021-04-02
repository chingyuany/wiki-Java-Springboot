#!/bin/bash
echo "RocketMQ starting----------"
#如果wiki.jar已經在執行  就kill process awk, '{pattern   action}'
#process_id=`ps -ef | grep namesrv | grep -v grep |awk '{print $2}'`
#if [ $process_id ] ; then
#sudo kill -9 $process_id
#fi
#process_id=`ps -ef | grep broker | grep -v grep |awk '{print $2}'`
#if [ $process_id ] ; then
#sudo kill -9 $process_id
#fi
export JAVA_HOME='/usr/lib/jvm/jdk1.8.0_281/'
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar

#source /etc/profile
#nohup 背景執行  2>&1 將標準出錯重定向到標準輸出，這裡的標準輸出已經重定向到了dev/null，即將標準出錯也輸出到dev/null中。最後一個&  是讓該命令在後臺執行。
nohup sh /root/rocketmq-all-4.8.0-source-release/distribution/target/rocketmq-4.8.0/rocketmq-4.8.0/bin/mqnamesrv &
nohup sh /root/rocketmq-all-4.8.0-source-release/distribution/target/rocketmq-4.8.0/rocketmq-4.8.0/bin/mqbroker -n localhost:9876 autoCreateTopicEnable=true &

echo "end RocketMQ"
