package com.manage.qq.service.socket.qq;

import com.manage.qq.config.Config;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.QQInteractiveDTO;
import com.manage.qq.model.qq.QQMsgSendRequest;
import com.manage.qq.util.FileUtil;
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
public class CommandExecReplayMsgHandler extends QQMsgHandler {
    @Resource
    private QQGateway qqGateway;
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

        if (StringUtils.startsWith(content, "执行 ")) {
            executor.submit(() -> {
                String command = StringUtils.substringAfter(content, "执行 ");
                String execRes = SystemUtil.runAndReturn("cmd /c " + command);

                QQMsgSendRequest qqMsgSendRequest = new QQMsgSendRequest();
                qqMsgSendRequest.setMsgId(qqInteractiveDTO.getId());
                if (execRes == null) {
                    qqMsgSendRequest.setContent("命令执行异常");
                    qqGateway.sendMsg(qqMsgSendRequest, "634091544");
                } else {
                    qqMsgSendRequest.setContent("命令执行成功");
                    String textImageFilePath = FileUtil.genUniqueFileNameContainsPath("textImage", ".png");
                    FileUtil.textToImage(execRes, textImageFilePath);
                    qqGateway.sendMsg(qqMsgSendRequest, "634091544", textImageFilePath);
                }
            });
        }
    }
}
