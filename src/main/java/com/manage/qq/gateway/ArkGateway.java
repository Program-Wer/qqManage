package com.manage.qq.gateway;

import com.manage.qq.config.Config;
import com.manage.qq.util.SystemUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Service
public class ArkGateway {
    @Resource
    private Config config;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(10);

    public boolean isRun() {
        return SystemUtil.getTaskList().stream().anyMatch(systemTask -> Objects.equals(systemTask.getName(), config.getArkName()));
    }

    public boolean restart() {
        SystemUtil.killProcess(config.getN2nName());
        SystemUtil.runBat(config.getN2nRun());
        return isRun();
    }

    public boolean close() {
        SystemUtil.killProcess(config.getN2nName());
        return !isRun();
    }
}
