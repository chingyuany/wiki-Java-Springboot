#!/bin/bash
echo "killing----------"
#如果wiki.jar已經在執行  就kill process awk, '{pattern   action}'
process_id=`ps -ef | grep wiki.jar | grep -v grep |awk '{print $2}'`
if [ $process_id ] ; then
sudo kill -9 $process_id
fi

echo "end kill"
