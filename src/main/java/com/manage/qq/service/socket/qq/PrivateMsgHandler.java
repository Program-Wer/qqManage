package com.manage.qq.service.socket.qq;

import com.manage.qq.dao.json.PrivateSubscriptionDAO;
import com.manage.qq.gateway.N2NGateway;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.QQWsMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
public class PrivateMsgHandler implements QQMsgHandler {
    @Resource
    private QQGateway qqGateway;
    @Resource
    private N2NGateway n2NGateway;
    @Resource
    private PrivateSubscriptionDAO privateSubscriptionDAO;

    @Override
    public void handleMsg(QQWsMessage qqWsMessage) {
        if (qqWsMessage == null
                || !Objects.equals(qqWsMessage.getPost_type(), "message")
                || !Objects.equals(qqWsMessage.getMessage_type(), "private")
                || !Objects.equals(qqWsMessage.getSub_type(), "friend")
                || qqWsMessage.getUser_id() <= 0
                || StringUtils.isBlank(qqWsMessage.getMessage())) {
            return;
        }
        String senderId = String.valueOf(qqWsMessage.getUser_id());
        String msgContent = qqWsMessage.getMessage();
        switch (msgContent) {
            case "重启n2n":
                boolean restart = n2NGateway.restart();
                qqGateway.sendPrivateMsg("开启n2n" + (restart ? "成功" : "失败"), senderId);
                break;
            case "查看n2n":
                boolean run = n2NGateway.isRun();
                qqGateway.sendPrivateMsg(run ? "n2n正在运行" : "n2n未在运行", senderId);
                break;
            case "关闭n2n":
                boolean close = n2NGateway.close();
                qqGateway.sendPrivateMsg(close ? "n2n被成功关闭" : "n2n关闭失败", senderId);
                break;
            case "DD":
                boolean add = privateSubscriptionDAO.add(senderId);
                qqGateway.sendPrivateMsg(add ? "订阅成功" : "订阅失败", senderId);
                break;
            case "TD":
                boolean delete = privateSubscriptionDAO.delete(senderId);
                qqGateway.sendPrivateMsg(delete ? "退订成功" : "退订失败", senderId);
                break;
            case "关闭方舟":
                break;
            default:
                qqGateway.sendPrivateMsg("老毕登，👴听不懂你讲话", senderId);
                break;
        }
    }
}
