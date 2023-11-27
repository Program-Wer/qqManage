package com.manage.qq.service.socket.qq;

import com.manage.qq.config.Config;
import com.manage.qq.enums.CommandEnum;
import com.manage.qq.gateway.N2NGateway;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.QQMessageBO;
import com.manage.qq.model.qq.QQMsgSendRequest;
import com.manage.qq.util.GptUtil;
import com.manage.qq.util.HttpUtil;
import com.manage.qq.util.LeishenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class LeishenInfoReplayMsgHandler extends QQMsgHandler {
    @Resource
    private QQGateway qqGateway;

    static final ExecutorService executor = Executors.newFixedThreadPool(5);

    @Override
    public void handleMsg(QQMessageBO qqWsMessage) {
        if (qqWsMessage.getMessageId() == null) {
            return;
        }

        String content = Optional.of(qqWsMessage).map(QQMessageBO::getContent).orElse(null);
        if (StringUtils.isBlank(content)) {
            return;
        }

        if (CommandEnum.COMMAND_LEISHEN_INFO.judgeCommand(content)) {
            executor.submit(() -> {
                QQMsgSendRequest qqMsgSendRequest = new QQMsgSendRequest();
                qqMsgSendRequest.setMsgId(qqWsMessage.getMessageId());
                qqMsgSendRequest.setContent(LeishenUtil.getLeishenInfo());
                qqGateway.sendMsg(qqMsgSendRequest, "634091544");
            });
        }
    }
}
