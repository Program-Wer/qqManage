package com.manage.qq.service.socket.qq;

import com.manage.qq.enums.CommandEnum;
import com.manage.qq.enums.CommonKvEnum;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.QQMessageBO;
import com.manage.qq.model.qq.QQMsgSendRequest;
import com.manage.qq.service.CommonKvService;
import com.manage.qq.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class LeishenSilentReplayMsgHandler extends QQMsgHandler {
    @Resource
    private QQGateway qqGateway;
    @Resource
    private CommonKvService commonKvService;

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

        if (CommandEnum.COMMAND_LEISHEN_SILENT.judgeCommand(content)) {
            executor.submit(() -> {
                long time = NumberUtils.toLong(CommandEnum.COMMAND_LEISHEN_SILENT.handleCommand(content), -1) * TimeUtil.HOUR_MS;
                String res;
                if (time < 0) {
                    res = "输入小时数错误";
                } else {
                    long silentTime = System.currentTimeMillis() + time;
                    String datetime = TimeUtil.formatTime(silentTime, TimeUtil.DATETIME_FORMAT);
                    res = String.format("设置成功,您可以游玩到【%s】", datetime);
                    commonKvService.setValue(CommonKvEnum.LEISHEN_SILENT_TIME, silentTime);
                }
                QQMsgSendRequest qqMsgSendRequest = new QQMsgSendRequest();
                qqMsgSendRequest.setMsgId(qqWsMessage.getMessageId());
                qqMsgSendRequest.setContent(res);
                qqGateway.sendMsg(qqMsgSendRequest, "634091544");
            });
        }
    }
}
