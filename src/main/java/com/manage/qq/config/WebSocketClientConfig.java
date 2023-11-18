package com.manage.qq.config;

import com.manage.qq.model.qq.QQInteractiveDTO;
import com.manage.qq.task.QQAliveMonitor;
import com.manage.qq.util.HttpUtil;
import com.manage.qq.util.JsonUtil;
import com.manage.qq.websocket.QQWebsocketHandler;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;

@Configuration
public class WebSocketClientConfig {
    @Resource
    private Config config;
    @Resource
    private QQWebsocketHandler qqWebsocketHandler;
    @Resource
    private QQAliveMonitor qqAliveMonitor;

    @SneakyThrows
    @Bean
    public WebSocketClient webSocketClient() {
        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();

        // 获取QQ的连接
        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bot 102074177.DMv72JnbE790odRw1VHyNWdjoi9lpn0H");
        String websocketRes = HttpUtil.sendGet("https://sandbox.api.sgroup.qq.com/gateway/bot", params, headers);
        String wsUrl = JsonUtil.parseFromPath(websocketRes, "$.url", String.class);

        // 创建鉴权,开始连接
        WebSocketSession webSocketSession = standardWebSocketClient.doHandshake(qqWebsocketHandler, wsUrl).get();
        QQInteractiveDTO discordRequest = new QQInteractiveDTO();
        discordRequest.setOp(2);
        QQInteractiveDTO.Message d = new QQInteractiveDTO.Message();
        d.setToken("Bot 102074177.DMv72JnbE790odRw1VHyNWdjoi9lpn0H");
        d.setIntents((1 << 30) | 0 |(1 << 12) | (1 << 9));
        d.setShard(Arrays.asList(0,1));
        discordRequest.setD(d);
        TextMessage webSocketMessage = new TextMessage(JsonUtil.toJson(discordRequest));
        webSocketSession.sendMessage(webSocketMessage);

        // 创建保活任务
        qqAliveMonitor.setWebSocketSession(webSocketSession);

        return standardWebSocketClient;
    }

    public static void main(String[] args) {
        System.out.println((1 << 30) | 0 |(1 << 12) | (1 << 9));
    }
}
