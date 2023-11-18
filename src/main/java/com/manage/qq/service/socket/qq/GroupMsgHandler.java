package com.manage.qq.service.socket.qq;

import com.manage.qq.config.Config;
import com.manage.qq.gateway.N2NGateway;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.QQInteractiveDTO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class GroupMsgHandler extends QQMsgHandler {
    @Resource
    private QQGateway qqGateway;
    @Resource
    private N2NGateway n2NGateway;
    @Resource
    private Config config;

    @Override
    public void handleMsg(QQInteractiveDTO qqWsMessage) {
        if (qqWsMessage != null) {
            return;
        }

        switch (qqWsMessage.getT()) {
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
