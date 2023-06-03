package com.manage.qq.config;

import com.manage.qq.websocket.QQWebsocketHandler;
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

    @Bean
    public WebSocketClient webSocketClient() {
        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
        standardWebSocketClient.doHandshake(qqWebsocketHandler, String.format("ws://%s:%s", config.getQqIp(), config.getQqWebSocketPort()));
        return standardWebSocketClient;
    }
}
