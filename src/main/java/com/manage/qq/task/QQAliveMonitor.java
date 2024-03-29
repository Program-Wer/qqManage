package com.manage.qq.task;

import com.manage.qq.config.Config;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.websocket.QQWebsocketHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@Slf4j
public class QQAliveMonitor {
    @Resource
    private Config config;

    @Resource
    private QQGateway qqGateway;

    @Resource
    private QQWebsocketHandler qqWebsocketHandler;

    @Setter
    private volatile int lastMessageId;

    @Scheduled(fixedRate = 5000)
    public void keepAliveQQ() {
        try {
            boolean keepAlive = qqGateway.keepAlive(lastMessageId);
            if (!keepAlive) {
                log.warn("保活失败，进行重连");
                boolean connectWebSocket = qqGateway.connectWebSocket(qqWebsocketHandler);
                if (connectWebSocket) {
                    log.info("重连成功");
                } else {
                    log.info("重连失败");
                }
            }
        } catch (Exception e) {
            log.error("QQ保活失败", e);
            boolean connectWebSocket = qqGateway.connectWebSocket(qqWebsocketHandler);
            if (connectWebSocket) {
                log.info("重连成功");
            } else {
                log.info("重连失败");
            }
        }
    }
}
