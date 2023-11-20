package com.manage.qq.service.socket.qq;

import com.manage.qq.config.Config;
import com.manage.qq.gateway.N2NGateway;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.qq.QQInteractiveDTO;
import com.manage.qq.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class CommandReplayMsgHandler extends QQMsgHandler {
    @Resource
    private QQGateway qqGateway;
    @Resource
    private N2NGateway n2NGateway;
    @Resource
    private Config config;

    static final ExecutorService executor = Executors.newFixedThreadPool(5);

    @Override
    public void handleMsg(QQInteractiveDTO qqInteractiveDTO) {
//        if (qqInteractiveDTO.getId() == null) {
//            return;
//        }
//
//        String content = Optional.of(qqInteractiveDTO).map(QQInteractiveDTO::getD).map(QQInteractiveDTO.Message::getContent).orElse(null);
//        if (StringUtils.isBlank(content)) {
//            return;
//        }
//
//        if (StringUtils.startsWith(content, " ") && !StringUtils.startsWith(content, "  ")) {
//            executor.submit(() -> {
//                HashMap<String, Object> params = new HashMap<>();
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Authorization", "Bot 102074177.DMv72JnbE790odRw1VHyNWdjoi9lpn0H");
//                params.put("msg_id", qqInteractiveDTO.getId());
//                switch (StringUtils.substringAfter(content, " ")) {
//                    case "重启n2n":
//                        boolean restart = n2NGateway.restart();
//                        params.put("content", "开启n2n" + (restart ? "成功" : "失败"));
//                        HttpUtil.sendPost("https://sandbox.api.sgroup.qq.com/channels/634091544/messages", params, headers);
//                        break;
//                    case "查看n2n":
//                        boolean run = n2NGateway.isRun();
//                        params.put("content", (run ? "n2n正在运行" : "n2n未在运行"));
//                        HttpUtil.sendPost("https://sandbox.api.sgroup.qq.com/channels/634091544/messages", params, headers);
//                        break;
//                    case "关闭n2n":
//                        boolean close = n2NGateway.close();
//                        params.put("content", (close ? "n2n被成功关闭" : "n2n关闭失败"));
//                        HttpUtil.sendPost("https://sandbox.api.sgroup.qq.com/channels/634091544/messages", params, headers);
//                        break;
//                    case "重启方舟":
//                        break;
//                    case "查看方舟":
//                        break;
//                    case "关闭方舟":
//                        break;
//                    default:
//                        int index = RandomUtils.nextInt(0, 3);
//                        List<String> texts = Arrays.asList("啊？", "啥？", "为啥？", "老毕登，👴听不懂你讲话");
//                        params.put("content", texts.get(index));
//                        HttpUtil.sendPost("https://sandbox.api.sgroup.qq.com/channels/634091544/messages", params, headers);
//                        break;
//                }
//            });
//        }
    }
}
