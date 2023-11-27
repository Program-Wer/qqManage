package com.manage.qq.service.socket.qq;

import com.manage.qq.config.Config;
import com.manage.qq.enums.CommandEnum;
import com.manage.qq.gateway.N2NGateway;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.QQInteractiveDTO;
import com.manage.qq.model.qq.QQMessageBO;
import com.manage.qq.util.GptUtil;
import com.manage.qq.util.HttpUtil;
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
public class GptReplayMsgHandler extends QQMsgHandler {
    @Resource
    private QQGateway qqGateway;
    @Resource
    private N2NGateway n2NGateway;
    @Resource
    private Config config;

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

        if (CommandEnum.COMMAND_GPT.judgeCommand(content)) {
            executor.submit(() -> {
                HashMap<String, Object> params = new HashMap<>();
                params.put("msg_id", qqWsMessage.getMessageId());
                params.put("content", GptUtil.chat(CommandEnum.COMMAND_GPT.handleCommand(content)));
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bot 102074177.DMv72JnbE790odRw1VHyNWdjoi9lpn0H");
                String res = HttpUtil.sendPost("https://sandbox.api.sgroup.qq.com/channels/634091544/messages", params, headers);
                log.info(res);
            });
        }
    }
}
