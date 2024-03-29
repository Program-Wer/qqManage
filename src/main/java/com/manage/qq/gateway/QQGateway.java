package com.manage.qq.gateway;

import com.fasterxml.jackson.core.type.TypeReference;
import com.manage.qq.config.Config;
import com.manage.qq.model.CommonRes;
import com.manage.qq.model.qq.QQInteractiveDTO;
import com.manage.qq.model.qq.QQMsgSendRequest;
import com.manage.qq.task.QQAliveMonitor;
import com.manage.qq.util.HttpUtil;
import com.manage.qq.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class QQGateway {
    @Resource
    private Config config;
    @Resource
    private QQAliveMonitor qqAliveMonitor;
    @Setter
    private volatile WebSocketSession webSocketSession;
    @Setter
    private volatile WebSocketClient webSocketClient;

    private volatile String wsUrl = null;

    private final String HTTP_FORMATE = "http://%s:%s/%s";

    public static void main(String[] args) {
        try {
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenshot = robot.createScreenCapture(screenRect);

            // 保存到本地文件
            File outputFile = new File("screenshot.png");
            ImageIO.write(screenshot, "png", outputFile);

            System.out.println("Screenshot saved to: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        HttpUtil.sendGetQQ(url, map, new TypeReference<CommonRes<Object>>() {
        });
    }

    public void sendPrivateMsg(String msg, String privateId) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", privateId);
        map.put("message", msg);
        String url = String.format(HTTP_FORMATE, config.getQqIp(), config.getQqHttpPort(), URL.SEND_PRIVATE_MSG.getUrl());
        HttpUtil.sendGetQQ(url, map, new TypeReference<CommonRes<Object>>() {
        });
    }

    public String sendMsg(QQMsgSendRequest qqMsgSendRequest, String channelId) {
        String url = String.format("https://sandbox.api.sgroup.qq.com/channels/%s/messages", channelId);
        Map<String, Object> params = JsonUtil.fromJson(JsonUtil.toJson(qqMsgSendRequest), new TypeReference<Map<String, Object>>() {
        });
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bot 102074177.DMv72JnbE790odRw1VHyNWdjoi9lpn0H");
        return HttpUtil.sendPost(url, params, headers);
    }

    public String sendMsg(QQMsgSendRequest qqMsgSendRequest, String channelId, String imagePath) {
        String url = String.format("https://sandbox.api.sgroup.qq.com/channels/%s/messages", channelId);
        Map<String, Object> params = JsonUtil.fromJson(JsonUtil.toJson(qqMsgSendRequest), new TypeReference<Map<String, Object>>() {
        });
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bot 102074177.DMv72JnbE790odRw1VHyNWdjoi9lpn0H");
        return HttpUtil.sendPostContainsImage(url, params, headers, imagePath);
    }

    public boolean connectWebSocket(WebSocketHandler webSocketHandler) {
        try {
            if (webSocketSession != null) {
                webSocketSession.close();
            }
            webSocketClient = new StandardWebSocketClient();

            // 获取QQ的连接
            HashMap<String, String> params = new HashMap<>();
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bot 102074177.DMv72JnbE790odRw1VHyNWdjoi9lpn0H");
            String websocketRes = HttpUtil.sendGet("https://sandbox.api.sgroup.qq.com/gateway/bot", params, headers);
            String newWsUrl = JsonUtil.parseFromPath(websocketRes, "$.url", String.class);
            if (StringUtils.isBlank(newWsUrl)) {
                newWsUrl = wsUrl;
            }
            wsUrl = newWsUrl;

            // 创建鉴权,开始连接
            webSocketSession = webSocketClient.doHandshake(webSocketHandler, this.wsUrl).get();
            QQInteractiveDTO discordRequest = new QQInteractiveDTO();
            discordRequest.setOp(2);
            QQInteractiveDTO.Message d = new QQInteractiveDTO.Message();
            d.setToken("Bot 102074177.DMv72JnbE790odRw1VHyNWdjoi9lpn0H");
            d.setIntents((1 << 30) | 0 | (1 << 12) | (1 << 9));
            d.setShard(Arrays.asList(0, 1));
            discordRequest.setD(d);
            TextMessage webSocketMessage = new TextMessage(JsonUtil.toJson(discordRequest));
            sessionSendMessage(webSocketMessage);

            return true;
        } catch (Exception e) {
            log.error("连接QQ WebSocket失败", e);
            return false;
        }
    }

    public boolean keepAlive(int lastMessageId) {
        if (webSocketSession == null) {
            return false;
        }

        QQInteractiveDTO aliveReq = new QQInteractiveDTO();
        aliveReq.setOp(1);
        aliveReq.setS(lastMessageId);

        try {
            sessionSendMessage(new TextMessage(JsonUtil.toJson(aliveReq)));
            return true;
        } catch (Exception e) {
            log.error("保活请求发送失败", e);
            return false;
        }
    }

    @SneakyThrows
    private synchronized void sessionSendMessage(TextMessage webSocketMessage) {
        webSocketSession.sendMessage(webSocketMessage);
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
