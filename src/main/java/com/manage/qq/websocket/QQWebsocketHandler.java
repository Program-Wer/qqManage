package com.manage.qq.websocket;

import com.manage.qq.config.Config;
import com.manage.qq.gateway.ArkGateway;
import com.manage.qq.gateway.N2NGateway;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.QQWsMessage;
import com.manage.qq.service.socket.qq.QQMsgHandler;
import com.manage.qq.util.JsonUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.util.List;

@Component
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

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            // 处理收到的消息
            String payload = message.getPayload();
            QQWsMessage qqWsMessage = JsonUtil.fromJson(payload, QQWsMessage.class);

            for (QQMsgHandler qqMsgHandler : qqMsgHandlerList) {
                qqMsgHandler.handle(qqWsMessage);
            }
        } catch (Throwable e) {
            System.out.println("WS处理消息异常");
            e.printStackTrace();
        }
    }
}
