package com.manage.qq.model.qq;

import lombok.Data;

/**
 * https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#message
 */
@Data
public class QQMessageBO {
    /**
     * 操作类型
     *
     */
    private int operateType;
    /**
     * 事件类型
     */
    private String eventType;
    /**
     * 消息序列号
     * 用于保活
     */
    private int serialNumber;
    /**
     * 消息ID
     */
    private String messageId;
    /**
     * 发送者ID
     */
    private String authorId;
    /**
     * 发送者名称
     */
    private String authorName;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 频道ID
     */
    private String guildId;
    /**
     * 子频道ID
     */
    private String channelId;
}