package com.manage.qq.mq;

import com.manage.qq.config.Config;
import com.manage.qq.enums.CQEnum;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.GroupMsg;
import com.manage.qq.util.FileUtil;
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

    private final static ConcurrentLinkedDeque<GroupMsg> groupMsgs = new ConcurrentLinkedDeque<>();
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

    @PostConstruct
    public void arkQQInit() {
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            try {
                if (CollectionUtils.isEmpty(groupMsgs)) {
                    return;
                }

                ArrayList<GroupMsg> msgList = new ArrayList<>();
                while (!groupMsgs.isEmpty()) {
                    msgList.add(groupMsgs.pollFirst());
                }

                msgList.stream().filter(Objects::nonNull)
                        .collect(Collectors.groupingBy(GroupMsg::getGroupId))
                        .forEach((groupId, sameIdMsgs) -> {
                            int[] i = new int[]{1};
                            String title = "【方舟捷豹通知】";
                            String combineMsg = sameIdMsgs.stream().map(msg -> String.format("【%s】%s", i[0]++, msg.getMsg())).collect(Collectors.joining("\n"));
                            if (msgList.size() <= config.getArkNoticeCombineLine()) {
                                qqGateway.sendGroupMsg(title + "\n" + combineMsg, groupId);
                            } else {
                                String textImageFilePath = FileUtil.genUniqueFileNameContainsPath("textImage", ".png");
                                FileUtil.textToImage(combineMsg, textImageFilePath);
                                String uriPath = FileUtil.convertUriPath(textImageFilePath);
                                qqGateway.sendGroupMsg(title + String.format(CQEnum.IMAGE.getSendFormat(), uriPath), groupId);
                            }
                        });
            } catch (Throwable e) {
                System.out.println("ARK轮询发送QQ出错");
                e.printStackTrace();
            }

        }, 0, config.getQqGroupPeriod(), TimeUnit.SECONDS);
    }

    public void push(String groupId, String msg) {
        groupMsgs.push(new GroupMsg(groupId, msg));
    }
}
