package com.manage.qq.mq;

import com.manage.qq.config.Config;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.GroupMsg;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class QQMqHandler {
    @Resource
    private Config config;
    @Resource
    private QQGateway qqGateway;

    private final static ConcurrentLinkedDeque<GroupMsg> groupMsgs = new ConcurrentLinkedDeque<>();
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

    @PostConstruct
    public void init() {
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            GroupMsg groupMsg = groupMsgs.peek();
            if (groupMsg == null) {
                return;
            }
            qqGateway.sendGroupMsg(groupMsg.getMsg(), groupMsg.getGroupId());
        }, 0, config.getQqGroupPeriod(), TimeUnit.SECONDS);
    }

    public void push(String groupId, String msg) {
        groupMsgs.push(new GroupMsg(groupId, msg));
    }
}
