package com.manage.qq.gateway;

import com.fasterxml.jackson.core.type.TypeReference;
import com.manage.qq.config.Config;
import com.manage.qq.model.CommonRes;
import com.manage.qq.util.HttpUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class QQGateway {
    @Resource
    private Config config;

    private final String HTTP_FORMATE = "http://%s:%s/%s";

    public void sendMsg(String msg, String groupId, String privateId) {
        if (StringUtils.isNotBlank(groupId)) {
            sendGroupMsg(msg, groupId);
        }
        if (StringUtils.isNotBlank(privateId)) {
            sendPrivateMsg(msg, privateId);
        }
    }

    public void sendGroupMsg(String msg, String groupId) {
        Map<String, String> map = new HashMap<>();
        map.put("group_id", groupId);
        map.put("message", msg);
        String url = String.format(HTTP_FORMATE, config.getQqIp(), config.getQqHttpPort(), URL.SEND_GROUP_MSG.getUrl());
        HttpUtil.sendGetQQ(url, map, new TypeReference<CommonRes<Object>>() {});
    }

    public void sendPrivateMsg(String msg, String privateId) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", privateId);
        map.put("message", msg);
        String url = String.format(HTTP_FORMATE, config.getQqIp(), config.getQqHttpPort(), URL.SEND_PRIVATE_MSG.getUrl());
        HttpUtil.sendGetQQ(url, map, new TypeReference<CommonRes<Object>>() {});
    }

    @Getter
    @AllArgsConstructor
    enum URL {
        SEND_PRIVATE_MSG("send_private_msg"),
        SEND_GROUP_MSG("send_group_msg"),
        ;
        private String url;
    }
}
