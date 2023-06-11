package com.manage.qq.websocket;

import com.manage.qq.config.Config;
import com.manage.qq.enums.CQEnum;
import com.manage.qq.gateway.ArkGateway;
import com.manage.qq.gateway.N2NGateway;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.QQWsMessage;
import com.manage.qq.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.util.Objects;

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

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            // 处理收到的消息
            String payload = message.getPayload();
            QQWsMessage qqWsMessage = JsonUtil.fromJson(payload, QQWsMessage.class);
            if (qqWsMessage == null
                    || !Objects.equals(config.getArkNoticeGroupId(), String.valueOf(qqWsMessage.getGroup_id()))
                    || !Objects.equals(qqWsMessage.getPost_type(), "message")
                    || StringUtils.isBlank(qqWsMessage.getMessage())) {
                return;
            }
            String qqMsg = qqWsMessage.getMessage();
            String atMe = String.format(CQEnum.AT.getSendFormat(), config.getQqNumber());
            if (!qqMsg.contains(atMe)) {
                return;
            }
            String msgContent = StringUtils.strip(qqMsg.replace(atMe, ""));
            System.out.println("收到@我的QQ消息: " + msgContent);
            switch (msgContent) {
                case "重启n2n":
                    boolean restart = n2NGateway.restart();
                    qqGateway.sendGroupMsg("开启n2n" + (restart ? "成功" : "失败"), config.getArkNoticeGroupId());
                    break;
                case "查看n2n":
                    boolean run = n2NGateway.isRun();
                    qqGateway.sendGroupMsg(run ? "n2n正在运行" : "n2n未在运行", config.getArkNoticeGroupId());
                    break;
                case "关闭n2n":
                    boolean close = n2NGateway.close();
                    qqGateway.sendGroupMsg(close ? "n2n被成功关闭" : "n2n关闭失败", config.getArkNoticeGroupId());
                    break;
                case "重启方舟":

                    break;
                case "查看方舟":
                    break;
                case "关闭方舟":
                    break;
                default:
                    qqGateway.sendGroupMsg("老毕登，👴听不懂你讲话", config.getArkNoticeGroupId());
                    break;
            }
        } catch (Throwable e) {
            System.out.println("WS处理消息异常");
            e.printStackTrace();
        }
    }
}
