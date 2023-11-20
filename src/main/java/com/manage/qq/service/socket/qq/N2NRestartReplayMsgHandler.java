package com.manage.qq.service.socket.qq;

import com.manage.qq.config.Config;
import com.manage.qq.enums.CommandEnum;
import com.manage.qq.gateway.N2NGateway;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.QQInteractiveDTO;
import com.manage.qq.model.qq.QQMsgSendRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class N2NRestartReplayMsgHandler extends QQMsgHandler {
    @Resource
    private QQGateway qqGateway;
    @Resource
    private N2NGateway n2NGateway;
    @Resource
    private Config config;

    static final ExecutorService executor = Executors.newFixedThreadPool(5);

    @Override
    public void handleMsg(QQInteractiveDTO qqInteractiveDTO) {
        if (qqInteractiveDTO.getId() == null) {
            return;
        }

        String content = Optional.of(qqInteractiveDTO).map(QQInteractiveDTO::getD).map(QQInteractiveDTO.Message::getContent).orElse(null);
        if (StringUtils.isBlank(content)) {
            return;
        }

        if (CommandEnum.COMMAND_N2N_RESTART.judgeCommand(content)) {
            executor.submit(() -> {
                boolean restart = n2NGateway.restart();
                QQMsgSendRequest qqMsgSendRequest = new QQMsgSendRequest();
                qqMsgSendRequest.setMsgId(qqInteractiveDTO.getId());
                qqMsgSendRequest.setContent("开启n2n" + (restart ? "成功" : "失败"));
                qqGateway.sendMsg(qqMsgSendRequest, "634091544");
            });
        }
    }
}
