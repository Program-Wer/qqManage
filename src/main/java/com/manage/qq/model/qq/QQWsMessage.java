package com.manage.qq.model.qq;

import lombok.Data;

@Data
public class QQWsMessage {
    private String post_type;
    private String message_type;
    private long time;
    private long self_id;
    private String sub_type;
    private int message_id;
    private long user_id;
    private long target_id;
    private String message;
    private String raw_message;
    private int font;
    private Sender sender;
    private int message_seq;
    private String anonymous;
    private long group_id;
}