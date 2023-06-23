package com.manage.qq.service.socket.qq;

import com.manage.qq.model.qq.QQWsMessage;

public interface QQMsgHandler {
    default void handle(QQWsMessage qqWsMessage) {
        try {
            handleMsg(qqWsMessage);
        } catch (Throwable e) {
            System.out.println("QQMsgHandler 处理异常：");
            e.printStackTrace();
        }
    }
    void handleMsg(QQWsMessage qqWsMessage);
}
