package com.manage.qq.service.socket.qq;

import com.manage.qq.model.qq.QQMessageBO;
import org.springframework.stereotype.Component;

@Component
public class PrivateMsgHandler extends QQMsgHandler {
    @Override
    void handleMsg(QQMessageBO qqWsMessage) {

    }
//    @Resource
//    private QQGateway qqGateway;
//    @Resource
//    private N2NGateway n2NGateway;
//    @Resource
//    private PrivateSubscriptionDAO privateSubscriptionDAO;
//
//    @Override
//    public void handleMsg(QQInteractiveDTO qqWsMessage) {
//        if (qqWsMessage != null) {
//            return;
//        }
//        switch (qqWsMessage.getT()) {
//            case "重启n2n":
//                boolean restart = n2NGateway.restart();
//                qqGateway.sendPrivateMsg("开启n2n" + (restart ? "成功" : "失败"), "x");
//                break;
//            case "查看n2n":
//                boolean run = n2NGateway.isRun();
//                qqGateway.sendPrivateMsg(run ? "n2n正在运行" : "n2n未在运行", "x");
//                break;
//            case "关闭n2n":
//                boolean close = n2NGateway.close();
//                qqGateway.sendPrivateMsg(close ? "n2n被成功关闭" : "n2n关闭失败", "x");
//                break;
//            case "DD":
//                boolean add = privateSubscriptionDAO.add("x");
//                qqGateway.sendPrivateMsg(add ? "订阅成功" : "订阅失败", "x");
//                break;
//            case "TD":
//                boolean delete = privateSubscriptionDAO.delete("x");
//                qqGateway.sendPrivateMsg(delete ? "退订成功" : "退订失败", "x");
//                break;
//            case "关闭方舟":
//                break;
//            default:
//                qqGateway.sendPrivateMsg("老毕登，👴听不懂你讲话", "x");
//                break;
//        }
//    }
}
