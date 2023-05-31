package com.manage.qq.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Config {
    @Value("${ark.notice.groupId}")
    private String arkNoticeGroupId;
    @Value("${ark.log.dir}")
    private String arkLogDir;
    @Value("${ark.log.file}")
    private String arkLogFile;
    @Value("${qq.ip}")
    private String qqIp;
    @Value("${qq.httpPort}")
    private String qqHttpPort;
    @Value("${qq.webSocketPort}")
    private String qqWebSocketPort;
}
