package com.manage.qq.dao.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.manage.qq.util.FileUtil;
import com.manage.qq.util.JsonUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.manage.qq.constant.SystemConstant.DB_DIR;

@Component
public class PrivateSubscriptionDAO {
    private final static String PRIVATE_SUBSCRIPTION_DB_PATH = DB_DIR + "privateSubscription.json";

    private Set<String> privateSubscriptionsIds;

    @PostConstruct
    public void init() {
        privateSubscriptionsIds = Collections.synchronizedSet(readMaster());
    }

    public Set<String> read() {
        return new HashSet<>(privateSubscriptionsIds);
    }

    public Set<String> readMaster() {
        String content = FileUtil.readFileAsString(PRIVATE_SUBSCRIPTION_DB_PATH);
        Set<String> set = JsonUtil.fromJson(content, new TypeReference<Set<String>>() {
        });
        return set == null ? new HashSet<>() : set;
    }

    public boolean delete(String content) {
        try {
            privateSubscriptionsIds.remove(content);
            return save(privateSubscriptionsIds);
        } catch (Throwable e) {
            System.out.println("删除订阅失败");
            e.printStackTrace();
            return false;
        }
    }

    public boolean add(String content) {
        try {
            privateSubscriptionsIds.add(content);
            return save(privateSubscriptionsIds);
        } catch (Throwable e) {
            System.out.println("添加订阅失败");
            e.printStackTrace();
            return false;
        }
    }

    public boolean save(Set<String> content) {
        return FileUtil.writeFile(JsonUtil.toJson(content), PRIVATE_SUBSCRIPTION_DB_PATH);
    }
}
