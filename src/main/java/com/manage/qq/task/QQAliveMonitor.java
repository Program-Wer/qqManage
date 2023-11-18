package com.manage.qq.task;

import com.manage.qq.config.Config;
import com.manage.qq.model.qq.QQInteractiveDTO;
import com.manage.qq.util.JsonUtil;
import lombok.Setter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.io.IOException;


@Component
public class QQAliveMonitor {
    @Resource
    private Config config;

    @Setter
    private volatile WebSocketSession webSocketSession;

    @Setter
    private volatile int lastMessageId;

    @Scheduled(fixedRate = 5000)
    public void keepAliveQQ() {
        try {
            if (webSocketSession != null) {
                QQInteractiveDTO aliveReq = new QQInteractiveDTO();
                aliveReq.setOp(1);
                aliveReq.setS(lastMessageId);
                try {
                    webSocketSession.sendMessage(new TextMessage(JsonUtil.toJson(aliveReq)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
