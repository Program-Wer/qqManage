package com.manage.qq.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * json工具类封装
 */
@Slf4j
public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    private static Configuration configuration = Configuration.defaultConfiguration()
            .addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL)
            .addOptions(Option.SUPPRESS_EXCEPTIONS);

    static {
        // 忽略空属性
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        // 忽略未知属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 忽略默认值
        objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT);
    }


    public static String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> reference) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, reference);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T parseFromPath(String jsonString, String jsonPath, Class<T> valueType) {
        return parseFromPath(parseDocumentContext(jsonString), jsonPath, valueType);
    }

    public static DocumentContext parseDocumentContext(String jsonString) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            return JsonPath.using(configuration).parse(jsonString);
        } catch (Exception e) {
            log.error("parseDocumentContext error json:{}", jsonString, e);
            return null;
        }
    }

    public static <T> T parseFromPath(DocumentContext documentContext, String jsonPath, Class<T> valueType) {
        if (StringUtils.isEmpty(jsonPath) || documentContext == null) {
            return null;
        }
        try {
            return documentContext.read(jsonPath, valueType);
        } catch (Exception e) {
            log.error("parseFromPath error json:{} path:{}", documentContext, jsonPath, e);
            return null;
        }
    }
}
