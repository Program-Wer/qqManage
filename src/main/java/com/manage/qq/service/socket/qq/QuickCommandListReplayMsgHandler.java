package com.manage.qq.service.socket.qq;

import com.manage.qq.enums.CommandEnum;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.QQMessageBO;
import com.manage.qq.model.qq.QQMsgSendRequest;
import com.manage.qq.repository.QuickCommonRepository;
import com.manage.qq.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@Slf4j
public class QuickCommandListReplayMsgHandler extends QQMsgHandler {
    @Resource
    private QQGateway qqGateway;
    @Resource
    private QuickCommonRepository quickCommonRepository;

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

        if (CommandEnum.COMMAND_QUICK_COMMAND_LIST.judgeCommand(content)) {
            executor.submit(() -> {
                QQMsgSendRequest qqMsgSendRequest = new QQMsgSendRequest();
                qqMsgSendRequest.setMsgId(qqWsMessage.getMessageId());
                String commandListStr = quickCommonRepository.findAll().stream()
                        .map(command -> String.format("【%s】:%s", command.getName(), command.getCommand()))
                        .collect(Collectors.joining("\n"));
                qqMsgSendRequest.setContent("快捷指令如下");
                String textImageFilePath = FileUtil.genUniqueFileNameContainsPath("cmd", ".png");
                FileUtil.textToImage(commandListStr, textImageFilePath);
                qqGateway.sendMsg(qqMsgSendRequest, "634091544", textImageFilePath);
            });
        }
    }
}
