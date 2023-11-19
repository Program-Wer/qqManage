package com.manage.qq.config;

import com.manage.qq.gateway.QQGateway;
import com.manage.qq.task.QQAliveMonitor;
import com.manage.qq.websocket.QQWebsocketHandler;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import javax.annotation.Resource;

@Configuration
public class WebSocketClientConfig {
    @Resource
    private Config config;
    @Resource
    private QQWebsocketHandler qqWebsocketHandler;
    @Resource
    private QQAliveMonitor qqAliveMonitor;
    @Resource
    private QQGateway qqGateway;

    @SneakyThrows
    @Bean
    public WebSocketClient webSocketClient() {
        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();

        // 连接QQ
        qqGateway.connectWebSocket(standardWebSocketClient, qqWebsocketHandler);

        return standardWebSocketClient;
    }

    public static void main(String[] args) {
        System.out.println((1 << 30) | 0 |(1 << 12) | (1 << 9));
    }
}
