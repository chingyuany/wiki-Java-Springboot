package com.alanyang.wiki.service;

import com.alanyang.wiki.websocket.WebSocketServer;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WsService {
//異步化寫在同一個class不起作用　所以開一個新Ｃｌａｓｓ
    @Resource
    public WebSocketServer webSocketServer;

    @Async
    public void sendInfo(String message,String logId) {
//        把Doc service的流水號放到 新線程的ws service的流水號
        MDC.put("LOG_ID", logId+"-1");
        webSocketServer.sendInfo(message);
    }
}
