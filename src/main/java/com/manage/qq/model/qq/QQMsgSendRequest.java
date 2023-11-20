package com.manage.qq.model.qq;

import lombok.Data;


@Data
public class QQMsgSendRequest {
    private String content;
    private Object embed;
    private Object ark;
    private Object messageReference;
    private String image;
    private String msgId;
    private String eventId;
    private Object markdown;
}
