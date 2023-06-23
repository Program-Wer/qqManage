package com.manage.qq.service.socket.qq;

import com.manage.qq.config.Config;
import com.manage.qq.enums.CQEnum;
import com.manage.qq.gateway.N2NGateway;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.QQWsMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
public class GroupMsgHandler implements QQMsgHandler {
    @Resource
    private QQGateway qqGateway;
    @Resource
    private N2NGateway n2NGateway;
    @Resource
    private Config config;

    @Override
    public void handleMsg(QQWsMessage qqWsMessage) {
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
    }
}
