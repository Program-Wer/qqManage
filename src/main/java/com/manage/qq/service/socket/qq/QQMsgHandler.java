package com.manage.qq.service.socket.qq;

import com.manage.qq.model.qq.QQMessageBO;
import com.manage.qq.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class QQMsgHandler {
    public void handle(QQMessageBO qqWsMessage) {
        try {
            handleMsg(qqWsMessage);
        } catch (Throwable e) {
            log.error("QQMsgHandler 处理异常：{}", JsonUtil.toJson(qqWsMessage), e);
        }
    }

    abstract void handleMsg(QQMessageBO qqWsMessage);
}
