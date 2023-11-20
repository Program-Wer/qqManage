package com.manage.qq.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.manage.qq.model.CommonRes;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HttpUtil {
    static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .build();

    public static String sendGet(String url, Map<String, String> params) {
        return sendGet(url, params, new HashMap<>());
    }

    public static String sendGet(String url, Map<String, String> params, Map<String, String> headers) {
        String result = "";
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (String key : params.keySet()) {
            urlBuilder.addQueryParameter(key, params.get(key));
        }
        String requestUrl = urlBuilder.build().toString();
        Request.Builder builder = new Request.Builder();
        for (String key : headers.keySet()) {
            builder.header(key, headers.get(key));
        }
        Request request = builder
                .url(requestUrl)
                .build();
        try (Response response = client.newCall(request).execute()) {
            result = response.body().string();
        } catch (Exception e) {
            log.error("sendGet error req:{}", JsonUtil.toJson(request), e);
        }
        return result;
    }

    public static <T> CommonRes<T> sendGetQQ(String url, Map<String, String> params, TypeReference<CommonRes<T>> reference) {
        String res = sendGet(url, params);
        CommonRes<T> commonRes = JsonUtil.fromJson(res, reference);
        if (commonRes == null || !commonRes.isSuccess()) {
            log.error("sendGetQQ error req:{}", JsonUtil.toJson(commonRes));
        }
        return commonRes;
    }

    public static String sendPost(String url, Map<String, Object> params, Map<String, String> headers) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), JsonUtil.toJson(params));
        Request.Builder builder = new Request.Builder()
                .url(url)
                .method("POST", requestBody);
        for (String key : headers.keySet()) {
            builder.addHeader(key, headers.get(key));
        }
        Request request = builder.addHeader("Content-Type", "application/json").build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            log.error("sendPost error req:{}", JsonUtil.toJson(request), e);
            return null;
        }
    }

    public static String sendPostContainsImage(String url, Map<String, Object> params, Map<String, String> headers, String imagePath) {
        Request request = null;
        try {
            File file = new File(imagePath);
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file_image", file.getName(),
                            RequestBody.create(MediaType.parse("image/jpeg"), file));

            // 添加参数
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                builder.addFormDataPart(entry.getKey(), String.valueOf(entry.getValue()));
            }

            RequestBody requestBody = builder.build();
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .method("POST", requestBody);
            for (String key : headers.keySet()) {
                requestBuilder.addHeader(key, headers.get(key));
            }
            request = requestBuilder.build();
        } catch (Exception e) {
            log.error("sendPost error req:{}", JsonUtil.toJson(request), e);
            return null;
        }

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            log.error("sendPost error req:{}", JsonUtil.toJson(request), e);
            return null;
        }
    }

    public static void main(String[] args) {
        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bot 102074177.DMv72JnbE790odRw1VHyNWdjoi9lpn0H");
        System.out.println(sendGet("https://sandbox.api.sgroup.qq.com/gateway/bot", params, headers));
    }
}
