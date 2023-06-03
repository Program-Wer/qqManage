package com.manage.qq.gateway;

import com.manage.qq.config.Config;
import com.manage.qq.util.SystemUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class N2NGateway {
    @Resource
    private Config config;

    public boolean isRun() {
        return SystemUtil.getTaskList().stream().anyMatch(systemTask -> Objects.equals(systemTask.getName(), config.getN2nName()));
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
