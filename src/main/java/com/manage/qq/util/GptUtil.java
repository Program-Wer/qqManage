package com.manage.qq.util;


import java.util.HashMap;

public class GptUtil {
    public static String chat(String content) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("model", "Mistral OpenOrca");
        params.put("prompt", content);
        params.put("max_tokens", 3000);
        params.put("temperature", 0);
        params.put("stream", true);
        String s = HttpUtil.sendPost("http://localhost:4891/v1/completions", params, new HashMap<>());
        return JsonUtil.parseFromPath(s, "$.choices[0].text", String.class);
    }
}
