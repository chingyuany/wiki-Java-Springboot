#!/bin/bash
echo "publish----------"
#如果wiki.jar已經在執行  就kill process awk, '{pattern   action}'
process_id=`ps -ef | grep wiki.jar | grep -v grep |awk '{print $2}'`
if [ $process_id ] ; then
sudo kill -9 $process_id
fi

#source /etc/profile
#nohup 背景執行  2>&1 將標準出錯重定向到標準輸出，這裡的標準輸出已經重定向到了dev/null，即將標準出錯也輸出到dev/null中。最後一個&  是讓該命令在後臺執行。
nohup java -jar -Dspring.profiles.active=prod ~/wiki/wiki.jar > /dev/null 2>&1 &

echo "end publish"
