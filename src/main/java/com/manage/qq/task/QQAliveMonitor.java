package com.manage.qq.task;

import com.manage.qq.config.Config;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.QQInteractiveDTO;
import com.manage.qq.util.JsonUtil;
import com.manage.qq.websocket.QQWebsocketHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;

import javax.annotation.Resource;
import java.io.IOException;


@Component
@Slf4j
public class QQAliveMonitor {
    @Resource
    private Config config;

    @Resource
    private QQGateway qqGateway;

    @Resource
    private QQWebsocketHandler qqWebsocketHandler;

    @Resource
    private WebSocketClient qqWebSocketClient;

    @Setter
    private volatile int lastMessageId;

    @Scheduled(fixedRate = 5000)
    public void keepAliveQQ() {
        try {
            qqGateway.keepAlive(lastMessageId);
        } catch (Exception e) {
            log.error("QQ保活失败", e);
            qqGateway.connectWebSocket(qqWebSocketClient, qqWebsocketHandler);
        }
    }
}
