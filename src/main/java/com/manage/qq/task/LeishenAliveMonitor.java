package com.manage.qq.task;

import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.QQMsgSendRequest;
import com.manage.qq.util.LeishenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@Slf4j
public class LeishenAliveMonitor {
    @Resource
    private QQGateway qqGateway;

    // 工作日定时任务，每小时执行一次，2点到19点
    @Scheduled(cron = "0 0 2-19 ? * MON-FRI")
    public void workdayTask() {
        checkPause();
    }

//    // 工作日定时任务，每小时执行一次，2点到19点
//    @Scheduled(fixedRate = 5000)
//    public void workxdayTask() {
//        checkPause();
//    }

    // 周末定时任务，每小时执行一次，2点到7点
    @Scheduled(cron = "0 0 2-7 ? * SAT,SUN")
    public void weekendTask() {
        // 执行周末定时任务的逻辑
        checkPause();
    }

    private void checkPause() {
        log.info("检测雷神状态任务开始");
        try {
            boolean pause = LeishenUtil.isPause();
            if (pause) {
                return;
            }
            QQMsgSendRequest qqMsgSendRequest = new QQMsgSendRequest();
            qqMsgSendRequest.setContent("小助手检测到您的雷神账号未暂停，已经帮您暂停：" + LeishenUtil.pauseLeishen());
            qqGateway.sendMsg(qqMsgSendRequest, "634091544");
            log.info("检测到雷神状态未停止，已经停止雷神");
        } catch (Exception e) {
            log.error("雷神定时检测暂停定时任务失败", e);
            QQMsgSendRequest qqMsgSendRequest = new QQMsgSendRequest();
            qqMsgSendRequest.setContent("小助手检测到您的雷神账号未暂停，帮您暂停时出现异常：" + ExceptionUtils.getStackTrace(e));
            qqGateway.sendMsg(qqMsgSendRequest, "634091544");
        }
    }
}
