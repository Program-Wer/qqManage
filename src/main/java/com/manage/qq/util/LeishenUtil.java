package com.manage.qq.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Objects;

@Slf4j
public class LeishenUtil {
    private static final String no = "18779193755";
    private static final String pwd = "77d713f8e64d75d954dcb02b3299b736";
    private static String accountToken = "iqinKQq9nBRmGq268dvdXa6cyhXhmoeOyJ7lyFimSc5DEayTZlBwwEAXiukascXB";

    /**
     * 获取雷神信息
     * @return
     */
    public static String getLeishenInfo() {
        checkLogin();
        HashMap<String, Object> params = new HashMap<>();
        params.put("account_token", accountToken);
        params.put("lang", "zh_CN");
        params.put("os_type", 4);
        String res = HttpUtil.sendPost("https://webapi.leigod.com/api/user/info", params, new HashMap<>());
        String pauseStatus = JsonUtil.parseFromPath(res, "$.data.pause_status", String.class);
        String expiryTime = JsonUtil.parseFromPath(res, "$.data.expiry_time", String.class);
        String msg = JsonUtil.parseFromPath(res, "$.msg", String.class);
        return StringUtils.isBlank(pauseStatus) ? "获取失败：" + msg : String.format("【%s】%s", pauseStatus, expiryTime);
    }

    public static boolean isPause() {
        checkLogin();
        HashMap<String, Object> params = new HashMap<>();
        params.put("account_token", accountToken);
        params.put("lang", "zh_CN");
        params.put("os_type", 4);
        String res = HttpUtil.sendPost("https://webapi.leigod.com/api/user/info", params, new HashMap<>());
        String pauseStatus = JsonUtil.parseFromPath(res, "$.data.pause_status", String.class);
        if (StringUtils.isBlank(pauseStatus)) {
            throw new RuntimeException("雷神暂停状态获取失败");
        }
        return "暂停中".equals(pauseStatus);
    }

    private static void checkLogin() {
        if (StringUtils.isBlank(accountToken)) {
            String login = loginLeishen();
            if (StringUtils.isNotBlank(login)) {
                throw new RuntimeException(login);
            }
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("account_token", accountToken);
        params.put("lang", "zh_CN");
        params.put("os_type", 4);
        String res = HttpUtil.sendPost("https://webapi.leigod.com/api/user/info", params, new HashMap<>());
        Integer code = JsonUtil.parseFromPath(res, "$.code", Integer.class);
        if(!Objects.equals(0, code)) {
            String login = loginLeishen();
            if (StringUtils.isNotBlank(login)) {
                throw new RuntimeException(login);
            }
        }
    }

    /**
     * 暂停雷神
     * @return
     */
    public static String pauseLeishen() {
        checkLogin();
        HashMap<String, Object> params = new HashMap<>();
        params.put("account_token", accountToken);
        params.put("lang", "zh_CN");
        params.put("os_type", 4);
        String res = HttpUtil.sendPost("https://webapi.leigod.com/api/user/pause", params, new HashMap<>());
        Integer code = JsonUtil.parseFromPath(res, "$.code", Integer.class);
        String msg = JsonUtil.parseFromPath(res, "$.msg", String.class);
        return Objects.equals(code, 0) ? "暂停成功" : "暂停失败：" + msg;
    }

    /**
     * 登录雷神
     * @return
     */
    public static synchronized String loginLeishen() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("username", no);
        params.put("password", pwd);

        params.put("os_type", 4);
        params.put("mobile_num", "18779193755");
        params.put("src_channel", "guanwang");
        params.put("country_code", 86);
        params.put("lang", "zh_CN");
        params.put("region_code", 1);
        params.put("account_token", null);
        String res = HttpUtil.sendPost("https://webapi.leigod.com/api/auth/login", params, new HashMap<>());
        String token = JsonUtil.parseFromPath(res, "$.data.login_info.account_token", String.class);
        if (StringUtils.isNotBlank(token)) {
            accountToken = token;
            return null;
        }

        return "登陆雷神失败：" + res;
    }
}
