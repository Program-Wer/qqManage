package com.manage.qq.model.qq;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QQInteractiveDTO {
    private int op;
    private int s;
    private String t;
    private Message d;
    private String id;

    @Data
    public static class Message {
        private String token;
        private int intents;
        private List<Integer> shard;
        private Map<String, String> properties;
        private Author author;
        private String channel_id;
        private String content;
        private String guild_id;
        private String id;
        private Member member;
        private int seq;
        private String seq_in_channel;
        private String timestamp;
    }

    @Data
    public static class Author {
        private String avatar;
        private boolean bot;
        private String id;
        private String username;
    }

    @Data
    public static class Member {
        private String joined_at;
        private String nick;
        private List<String> roles;
    }

}
