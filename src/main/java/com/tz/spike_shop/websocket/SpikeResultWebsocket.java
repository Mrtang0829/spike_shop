package com.tz.spike_shop.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 将秒杀信息推送给用户
 */
@Component
@ServerEndpoint("/spikeResult/{userId}")
@Slf4j
public class SpikeResultWebsocket {

    private static AtomicInteger onlineCount = new AtomicInteger(0);

    // userId -> session
    private static Map<Long, Session> websocketPool = new ConcurrentHashMap<>();


    // 发送消息
    public void sendMessage(Session session, String message) throws IOException {
        if (session == null) return;
        synchronized (session) {
            System.out.println("发送数据：" + message);
            session.getBasicRemote().sendText(message);
        }
    }

    // 给指定用户发送消息
    public void sendInfo(Long userId, String message) {
        Session session = websocketPool.get(userId);
        try {
            sendMessage(session, message);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        if (!websocketPool.containsKey(userId)) {
            addOnlineCount();
        }
        websocketPool.put(userId, session);
        log.info("新连接: " + userId + " 现在连接数: " + onlineCount.get());
    }

    @OnClose
    public void close(@PathParam("userId") Long userId) {
        if (websocketPool.containsKey(userId)) {
            subOnlineCount();
        }
        websocketPool.remove(userId);
        log.info("退出: " + userId + " 现在连接数: " + onlineCount.get());
    }

    @OnError
    public void error(Session session, Throwable ex) {
        System.out.println("error");
        ex.printStackTrace();
    }

    /**
     * 接收客户端信息，可执行转发操作
     * @param message
     */
    @OnMessage
    public void message(String message) {

    }

    public static void addOnlineCount(){
        onlineCount.incrementAndGet();
    }

    public static void subOnlineCount() {
        onlineCount.decrementAndGet();
    }
}
