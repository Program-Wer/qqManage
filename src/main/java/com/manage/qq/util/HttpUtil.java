package com.manage.qq.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.model.CommonRes;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.HashMap;
import java.util.Map;

public class HttpUtil {
    static OkHttpClient client = new OkHttpClient();
    public static String sendGet(String url, Map<String, String> params) {
        String result = "";
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (String key : params.keySet()) {
            urlBuilder.addQueryParameter(key, params.get(key));
        }
        String requestUrl = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(requestUrl)
                .build();
        try (Response response = client.newCall(request).execute()) {
            result = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> CommonRes<T> sendGetQQ(String url, Map<String, String> params, TypeReference<CommonRes<T>> reference) {
        String res = sendGet(url, params);
        CommonRes<T> commonRes = JsonUtil.fromJson(res, reference);
        if (commonRes == null || !commonRes.isSuccess()) {
            System.out.println("sendGetQQ error:" + JsonUtil.toJson(commonRes));
        }
        return commonRes;

    }

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", "1695807914");
        map.put("message", "[CQ:tts,text=你在干什么？]");
        String s = null;
//         s = sendGet("http://127.0.0.1:5700/send_private_msg", map);
        System.out.println(s);
        map = new HashMap<>();
        map.put("group_id", "637485030");
//        map.put("message", "[CQ:tts,text=老油条，你这个傻逼？]");
        map.put("message", "123[CQ:image,file=file:/D:/software/cqhttp/data/images/1.png]");
        s = sendGet("http://127.0.0.1:5700/send_group_msg", map);
        System.out.println(s);
    }
}
