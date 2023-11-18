package com.manage.qq.service.socket.qq;

import com.manage.qq.config.Config;
import com.manage.qq.gateway.N2NGateway;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.QQInteractiveDTO;
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
public class ReplayMsgHandler extends QQMsgHandler {
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

        if (StringUtils.startsWith(content, "  ") && !StringUtils.startsWith(content, "   ")) {
            executor.submit(() -> {
                HashMap<String, Object> params = new HashMap<>();
                params.put("msg_id", qqInteractiveDTO.getId());
                params.put("content", GptUtil.chat(StringUtils.substringAfter(content, "  ")));
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bot 102074177.DMv72JnbE790odRw1VHyNWdjoi9lpn0H");
                String res = HttpUtil.sendPost("https://sandbox.api.sgroup.qq.com/channels/634091544/messages", params, headers);
                log.info(res);
            });
        }
    }
}
