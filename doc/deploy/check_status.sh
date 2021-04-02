#!/bin/bash
echo "checking----------"
#如果wiki.jar已經在執行  就kill process awk, '{pattern   action}'
echo "wiki backend java"
ps -ef | grep wiki.jar | grep -v grep >&1
echo "rocketMQ name server:"
ps -ef | grep namesrv | grep -v grep >&1
echo "rocketMQ broker server"
ps -ef | grep broker | grep -v grep >&1
echo "end checking"
