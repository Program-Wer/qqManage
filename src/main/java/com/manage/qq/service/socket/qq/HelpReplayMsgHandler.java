package com.manage.qq.service.socket.qq;

import com.manage.qq.enums.CommandEnum;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.QQInteractiveDTO;
import com.manage.qq.model.qq.QQMsgSendRequest;
import com.manage.qq.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HelpReplayMsgHandler extends QQMsgHandler {
    @Resource
    private QQGateway qqGateway;

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

        if (CommandEnum.COMMAND_HELP.judgeCommand(content)) {
            executor.submit(() -> {
                QQMsgSendRequest qqMsgSendRequest = new QQMsgSendRequest();
                qqMsgSendRequest.setMsgId(qqInteractiveDTO.getId());
                String helpStr = Arrays.stream(CommandEnum.values()).map(CommandEnum::getDesc).collect(Collectors.joining("\n"));
                String textImageFilePath = FileUtil.genUniqueFileNameContainsPath("cmd", ".png");
                FileUtil.textToImage(helpStr, textImageFilePath);
                qqGateway.sendMsg(qqMsgSendRequest, "634091544", textImageFilePath);
            });
        }
    }
}
