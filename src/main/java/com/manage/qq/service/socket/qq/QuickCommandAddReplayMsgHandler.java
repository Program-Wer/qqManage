package com.manage.qq.service.socket.qq;

import com.manage.qq.dao.json.QuickCommandDAO;
import com.manage.qq.enums.CommandEnum;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.QQMessageBO;
import com.manage.qq.model.qq.QQMsgSendRequest;
import com.manage.qq.repository.QuickCommonRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class QuickCommandAddReplayMsgHandler extends QQMsgHandler {
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

        if (CommandEnum.COMMAND_QUICK_COMMAND_ADD.judgeCommand(content)) {
            executor.submit(() -> {
                QQMsgSendRequest qqMsgSendRequest = new QQMsgSendRequest();
                qqMsgSendRequest.setMsgId(qqWsMessage.getMessageId());

                String resultCommand = CommandEnum.COMMAND_QUICK_COMMAND_ADD.handleCommand(content);
                String name = StringUtils.substringBefore(resultCommand, "=");
                String command = StringUtils.substringAfter(resultCommand, "=");
                if (StringUtils.isBlank(name) || StringUtils.isBlank(command)) {
                    qqMsgSendRequest.setContent("添加快捷命令失败，请检查您的格式");
                } else {
                    QuickCommandDAO quickCommandDAO = new QuickCommandDAO();
                    quickCommandDAO.setName(name);
                    quickCommandDAO.setCommand(command);
                    quickCommonRepository.save(quickCommandDAO);
                    qqMsgSendRequest.setContent("添加快捷命令成功");
                }
                qqGateway.sendMsg(qqMsgSendRequest, "634091544");
            });
        }
    }
}
