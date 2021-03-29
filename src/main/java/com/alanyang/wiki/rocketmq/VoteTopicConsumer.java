package com.alanyang.wiki.rocketmq;

 import com.alanyang.wiki.websocket.WebSocketServer;
 import org.apache.rocketmq.common.message.MessageExt;
 import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
 import org.apache.rocketmq.spring.core.RocketMQListener;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.stereotype.Service;

 import javax.annotation.Resource;

 @Service
// DocService topic, application properties consumerGroup
 @RocketMQMessageListener(consumerGroup = "default", topic = "VOTE_TOPIC")
 public class VoteTopicConsumer implements RocketMQListener<MessageExt> {

     private static final Logger LOG = LoggerFactory.getLogger(VoteTopicConsumer.class);

     @Resource
     public WebSocketServer webSocketServer;

     @Override
     public void onMessage(MessageExt messageExt) {
         byte[] body = messageExt.getBody();
         LOG.info("ROCKETMQ get MSG：{}", new String(body));
         webSocketServer.sendInfo(new String(body));
     }
 }
