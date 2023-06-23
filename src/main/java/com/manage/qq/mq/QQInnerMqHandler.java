package com.manage.qq.mq;

import com.manage.qq.config.Config;
import com.manage.qq.enums.CQEnum;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.MsgSendContext;
import com.manage.qq.util.FileUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class QQInnerMqHandler {
    @Resource
    private Config config;
    @Resource
    private QQGateway qqGateway;

    private final static ConcurrentLinkedDeque<MsgSendContext> groupMsgs = new ConcurrentLinkedDeque<>();
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

    @PostConstruct
    public void arkQQInit() {
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            try {
                if (CollectionUtils.isEmpty(groupMsgs)) {
                    return;
                }

                ArrayList<MsgSendContext> msgList = new ArrayList<>();
                while (!groupMsgs.isEmpty()) {
                    msgList.add(groupMsgs.pollFirst());
                }

                msgList.stream().filter(Objects::nonNull)
                        .collect(Collectors.groupingBy(context -> ImmutablePair.of(context.getGroupId(), context.getUserId())))
                        .forEach((pair, sameIdMsgs) -> {
                            String groupId = pair.getLeft();
                            String privateId = pair.getRight();
                            int[] i = new int[]{1};
                            String title = "【方舟捷豹通知】";
                            String combineMsg = sameIdMsgs.stream().map(msg -> String.format("【%s】%s", i[0]++, msg.getMsg())).collect(Collectors.joining("\n"));
                            if (msgList.size() <= config.getArkNoticeCombineLine()) {
                                qqGateway.sendMsg(title + "\n" + combineMsg, groupId, privateId);
                            } else {
                                String textImageFilePath = FileUtil.genUniqueFileNameContainsPath("textImage", ".png");
                                FileUtil.textToImage(combineMsg, textImageFilePath);
                                String uriPath = FileUtil.convertUriPath(textImageFilePath);
                                qqGateway.sendMsg(title + String.format(CQEnum.IMAGE.getSendFormat(), uriPath), groupId, privateId);
                            }
                        });
            } catch (Throwable e) {
                System.out.println("ARK轮询发送QQ出错");
                e.printStackTrace();
            }

        }, 0, config.getQqGroupPeriod(), TimeUnit.SECONDS);
    }

    public void push(String groupId, String userId, String msg) {
        groupMsgs.add(new MsgSendContext(groupId, userId, msg));
    }
}
