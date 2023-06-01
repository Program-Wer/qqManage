package com.manage.qq.ark;

import com.manage.qq.config.Config;
import com.manage.qq.mq.QQMqHandler;
import com.manage.qq.service.monitor.FileMonitor;
import com.manage.qq.service.monitor.MyTailerListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


@Component
public class ArkLogMonitor {
    @Resource
    private Config config;
    @Resource
    private QQMqHandler qqMqHandler;

    @PostConstruct
    public void openMonitor() {
        MyTailerListener myTailerListener = new MyTailerListener(line -> {
            String text = TextUtil.handleLogLine(line);
            if (StringUtils.isBlank(text)) {
                return;
            }
            qqMqHandler.push(config.getArkNoticeGroupId(), text);
        });
        new FileMonitor(config.getArkLogDir(), config.getArkLogFile(), myTailerListener).start();
    }
}
