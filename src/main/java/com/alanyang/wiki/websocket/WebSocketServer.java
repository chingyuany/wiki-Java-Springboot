package com.alanyang.wiki.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;

@Component
@ServerEndpoint("/ws/{token}")
public class WebSocketServer {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketServer.class);

    /**
     * 每个客户端一个token
     */
    private String token = "";

    private static HashMap<String, Session> map = new HashMap<>();

    /**
     * 连接成功
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
//        推送訊息需要session
        map.put(token, session);
        this.token = token;
        LOG.info("New connection：token：{}，session id：{}，Current connect amount：{}", token, session.getId(), map.size());
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose(Session session) {
        map.remove(this.token);
        LOG.info("Connection close，token：{}，session id：{}！Current connect amount：{}", this.token, session.getId(), map.size());
    }

    /**
     * 服務端收到消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        LOG.info("Receive msg：{}，content：{}", token, message);
    }

    /**
     * 连接错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        LOG.error("Error:", error);
    }

    /**
     * 群发消息
     */
    public void sendInfo(String message) {
        for (String token : map.keySet()) {
            Session session = map.get(token);
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                LOG.error("Send msg failed：{}，content：{}", token, message);
            }
            LOG.info("Send msg：{}，content：{}", token, message);
        }
    }

}
