package com.manage.qq.config;

import com.manage.qq.gateway.QQGateway;
import com.manage.qq.task.QQAliveMonitor;
import com.manage.qq.websocket.QQWebsocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Configuration
@Slf4j
public class WebSocketClientConfig {
    @Resource
    private Config config;
    @Resource
    private QQWebsocketHandler qqWebsocketHandler;
    @Resource
    private QQAliveMonitor qqAliveMonitor;
    @Resource
    private QQGateway qqGateway;

    @PostConstruct
    public void createQQConnect() {
        // 连接QQ
        qqGateway.connectWebSocket(qqWebsocketHandler);
    }

    public static void main(String[] args) {
        System.out.println((1 << 30) | 0 |(1 << 12) | (1 << 9));
    }
}
