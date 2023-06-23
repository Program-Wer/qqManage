package com.manage.qq.ark;

import com.manage.qq.config.Config;
import com.manage.qq.dao.json.PrivateSubscriptionDAO;
import com.manage.qq.mq.QQInnerMqHandler;
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
    private QQInnerMqHandler qqMqHandler;
    @Resource
    private PrivateSubscriptionDAO privateSubscriptionDAO;

    @PostConstruct
    public void openMonitor() {
        MyTailerListener myTailerListener = new MyTailerListener(line -> {
            String text = TextUtil.handleLogLine(line);
            if (StringUtils.isBlank(text)) {
                return;
            }
            qqMqHandler.push(config.getArkNoticeGroupId(), null, text);
            for (String privateSubscriptionId : privateSubscriptionDAO.read()) {
                qqMqHandler.push(null, privateSubscriptionId, text);
            }
        });
        new FileMonitor(config.getArkLogDir(), config.getArkLogFile(), myTailerListener).start();
    }
}
