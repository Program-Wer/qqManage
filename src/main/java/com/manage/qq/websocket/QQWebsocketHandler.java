package com.manage.qq.websocket;

import com.manage.qq.config.Config;
import com.manage.qq.gateway.ArkGateway;
import com.manage.qq.gateway.N2NGateway;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.QQInteractiveDTO;
import com.manage.qq.service.socket.qq.QQMsgHandler;
import com.manage.qq.task.QQAliveMonitor;
import com.manage.qq.util.GptUtil;
import com.manage.qq.util.HttpUtil;
import com.manage.qq.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class QQWebsocketHandler extends TextWebSocketHandler {
    @Resource
    private Config config;
    @Resource
    private QQGateway qqGateway;
    @Resource
    private N2NGateway n2NGateway;
    @Resource
    private ArkGateway arkGateway;
    @Resource
    private List<QQMsgHandler> qqMsgHandlerList;
    @Resource
    private QQAliveMonitor qqAliveMonitor;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            // 处理收到的消息
            String payload = message.getPayload();
            QQInteractiveDTO qqInteractiveDTO = JsonUtil.fromJson(payload, QQInteractiveDTO.class);
            if (qqInteractiveDTO == null) {
                return;
            }

            // 设置最近收到的消息,用于保活
            qqAliveMonitor.setWebSocketSession(session);
            if (qqInteractiveDTO.getS() > 0) {
                qqAliveMonitor.setLastMessageId(qqInteractiveDTO.getS());
            }

            // 处理消息
            for (QQMsgHandler qqMsgHandler : qqMsgHandlerList) {
                qqMsgHandler.handle(qqInteractiveDTO);
            }
        } catch (Throwable e) {
            log.error("WS处理消息异常", e);
        }
    }
}
