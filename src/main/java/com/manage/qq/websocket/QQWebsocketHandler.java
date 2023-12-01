package com.manage.qq.websocket;

import com.jayway.jsonpath.DocumentContext;
import com.manage.qq.config.Config;
import com.manage.qq.gateway.ArkGateway;
import com.manage.qq.gateway.N2NGateway;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.QQMessageBO;
import com.manage.qq.repository.QuickCommonRepository;
import com.manage.qq.service.socket.qq.QQMsgHandler;
import com.manage.qq.task.QQAliveMonitor;
import com.manage.qq.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.util.List;

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
    @Resource
    private QuickCommonRepository quickCommonRepository;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            // 处理收到的消息
            qqGateway.setWebSocketSession(session);
            String payload = message.getPayload();
            QQMessageBO qqMessageBO = convertQQMessageBO(payload);
            if (qqMessageBO == null) {
                return;
            }

            // 设置最近收到的消息,用于保活
            if (qqMessageBO.getSerialNumber() > 0) {
                qqAliveMonitor.setLastMessageId(qqMessageBO.getSerialNumber());
            }

            // 替换快捷指令
            if (StringUtils.isNoneEmpty(qqMessageBO.getContent())) {
                quickCommonRepository.findById(qqMessageBO.getContent()).ifPresent(quickCommandDAO -> qqMessageBO.setContent(quickCommandDAO.getCommand()));
            }

            // 处理消息
            for (QQMsgHandler qqMsgHandler : qqMsgHandlerList) {
                qqMsgHandler.handle(qqMessageBO);
            }
        } catch (Throwable e) {
            log.error("WS处理消息异常", e);
        }
    }

    private QQMessageBO convertQQMessageBO(String msg) {
        DocumentContext documentContext = JsonUtil.parseDocumentContext(msg);
        if (documentContext == null) {
            return null;
        }
        QQMessageBO qqMessageBO = new QQMessageBO();
        qqMessageBO.setMessageId(JsonUtil.parseFromPath(documentContext, "$.id", String.class));
        qqMessageBO.setEventType(JsonUtil.parseFromPath(documentContext, "$.t", String.class));
        qqMessageBO.setContent(JsonUtil.parseFromPath(documentContext, "$.d.content", String.class));
        qqMessageBO.setAuthorId(JsonUtil.parseFromPath(documentContext, "$.d.author.id", String.class));
        qqMessageBO.setAuthorName(JsonUtil.parseFromPath(documentContext, "$.d.author.username", String.class));
        qqMessageBO.setChannelId(JsonUtil.parseFromPath(documentContext, "$.d.channel_id", String.class));
        qqMessageBO.setGuildId(JsonUtil.parseFromPath(documentContext, "$.d.guild_id", String.class));
        qqMessageBO.setOperateType(ObjectUtils.defaultIfNull(JsonUtil.parseFromPath(documentContext, "$.op", Integer.class), 0));
        Integer serialNumber = ObjectUtils.defaultIfNull(JsonUtil.parseFromPath(documentContext, "$.s", Integer.class), 0);
        qqMessageBO.setSerialNumber(serialNumber);
        return qqMessageBO;
    }
}
