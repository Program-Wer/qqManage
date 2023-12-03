package com.manage.qq.gateway;

import com.manage.qq.util.HttpUtil;
import com.manage.qq.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class SteamGateway {
    private static final String key = "185DD2AC372EF7181DD8629AACAA5B04";

    public List<String> getUsersInfo(List<String> steamIds) {
        String url = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002";

        HashMap<String, String> params = new HashMap<>();
        params.put("key", key);
        params.put("steamids", String.join(",", steamIds));
        String res = HttpUtil.sendGet(url, params);
        List list = JsonUtil.parseFromPath(res, "$.response.players", List.class);

        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list;
    }

    public List<String> getRecentlyPlayGames(String steamId) {
        String url = "http://api.steampowered.com/IPlayerService/GetRecentlyPlayedGames/v0001";

        HashMap<String, String> params = new HashMap<>();
        params.put("key", key);
        params.put("steamid", steamId);
        params.put("format", "json");
        String res = HttpUtil.sendGet(url, params);
        List list = JsonUtil.parseFromPath(res, "$.response.games", List.class);

        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list;
    }
}
