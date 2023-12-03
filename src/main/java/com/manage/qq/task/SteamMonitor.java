package com.manage.qq.task;

import com.jayway.jsonpath.DocumentContext;
import com.manage.qq.enums.CommonKvEnum;
import com.manage.qq.gateway.QQGateway;
import com.manage.qq.gateway.SteamGateway;
import com.manage.qq.model.qq.QQMsgSendRequest;
import com.manage.qq.service.CommonKvService;
import com.manage.qq.util.HttpUtil;
import com.manage.qq.util.JsonUtil;
import com.manage.qq.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Component
@Slf4j
public class SteamMonitor {

    @Resource
    private SteamGateway steamGateway;
    @Resource
    private CommonKvService commonKvService;
    @Resource
    private QQGateway qqGateway;

    static List<String> steamIds = Arrays.asList("76561198149901382", "76561198834008439", "76561198838955035", "76561198834453776");

    @Scheduled(fixedRate = 300000)
    public void findPlayGame() {
        try {
            List resList = steamGateway.getUsersInfo(steamIds);
            if (CollectionUtils.isEmpty(resList)) {
                log.info("获取Steam好友状态失败 res:{}", resList);
                return;
            }
            for (Object res : resList) {
                String player = JsonUtil.toJson(res);
                DocumentContext documentContext = JsonUtil.parseDocumentContext(player);
                String steamId = JsonUtil.parseFromPath(documentContext, "$.steamid", String.class);
                if (StringUtils.isEmpty(steamId)) {
                    continue;
                }
                String personaName = JsonUtil.parseFromPath(documentContext, "$.personaname", String.class);
                String gameExtraInfo = JsonUtil.parseFromPath(documentContext, "$.gameextrainfo", String.class);

                String dbPlayer = (String) commonKvService.getKeyOrDefault(CommonKvEnum.STEAM_PLAYER_INFO, steamId, null);
                String dbGameExtraInfo = JsonUtil.parseFromPath(dbPlayer, "$.gameextrainfo", String.class);

                if (!Objects.equals(gameExtraInfo, dbGameExtraInfo)) {
                    QQMsgSendRequest qqMsgSendRequest = new QQMsgSendRequest();
                    String content = StringUtils.isNotBlank(gameExtraInfo)
                            ? String.format("%s偷偷启动了%s", personaName, gameExtraInfo)
                            : String.format("%s从%s下线了", personaName, dbGameExtraInfo);
                    qqMsgSendRequest.setContent(content);
                    qqGateway.sendMsg(qqMsgSendRequest, "634091544");
                }

                commonKvService.setValue(CommonKvEnum.STEAM_PLAYER_INFO, steamId, player);
            }
        } catch (Exception e) {
            log.error("查询steam好友状态失败", e);
        }
    }

    @Scheduled(fixedRate = 3600000)
    public void findRecentlyPlayGame() {
        try {
            List resList = steamGateway.getUsersInfo(steamIds);
            if (CollectionUtils.isEmpty(resList)) {
                log.info("获取Steam好友状态失败 res:{}", resList);
                return;
            }
            for (Object res : resList) {
                String player = JsonUtil.toJson(res);
                DocumentContext documentContext = JsonUtil.parseDocumentContext(player);
                String steamId = JsonUtil.parseFromPath(documentContext, "$.steamid", String.class);
                if (StringUtils.isEmpty(steamId)) {
                    continue;
                }
                String personaName = JsonUtil.parseFromPath(documentContext, "$.personaname", String.class);
                // 查找最近玩的游戏
                findRecentlyPlayGame(steamId, personaName);
            }
        } catch (Exception e) {
            log.error("查询steam好友状态失败", e);
        }
    }

    static final long steamRefreshMinute = 30;
    static final long steamRefreshLossMinute = 2;

    public void findRecentlyPlayGame(String steamId, String personaName) {
        try {
            List resList = steamGateway.getRecentlyPlayGames(steamId);
            if (CollectionUtils.isEmpty(resList)) {
//                log.info("获取Steam最近游玩的游戏失败 res:{}", resList);
                return;
            }

            for (Object res : resList) {
                String game = JsonUtil.toJson(res);
                DocumentContext documentContext = JsonUtil.parseDocumentContext(game);
                Long appId = JsonUtil.parseFromPath(documentContext, "$.appid", Long.class);
                Long playTime = JsonUtil.parseFromPath(documentContext, "$.playtime_forever", Long.class);
                String gameName = JsonUtil.parseFromPath(documentContext, "$.name", String.class);

                String key = String.format("%s_%s", steamId, appId);
                String playGame = commonKvService.getKeyOrDefault(CommonKvEnum.STEAM_PLAYER_INFO, key, null);
                commonKvService.setValue(CommonKvEnum.STEAM_PLAYER_INFO, key, game);
                if (playGame == null) {
                    continue;
                }
                Long dbPlayTime = JsonUtil.parseFromPath(playGame, "$.playtime_forever", Long.class);
                dbPlayTime = dbPlayTime == null ? 0 : dbPlayTime;

                if (!Objects.equals(playTime, dbPlayTime) && playTime != null && playTime > 0) {
                    String content = Math.abs((playTime - dbPlayTime) - steamRefreshMinute) < steamRefreshLossMinute
                            ? String.format("%s偷偷在玩%s，快去逮他！", personaName, gameName)
                            : String.format("%s偷偷玩了%s，已经溜了！", personaName, gameName);
                    QQMsgSendRequest qqMsgSendRequest = new QQMsgSendRequest();
                    qqMsgSendRequest.setContent(content);
                    qqGateway.sendMsg(qqMsgSendRequest, "634091544");
                }
            }
        } catch (Exception e) {
            log.error("查询steam好友状态失败", e);
        }
    }

    public static void main(String[] args) {
        String s = HttpUtil.sendGet("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002?key=185DD2AC372EF7181DD8629AACAA5B04&steamids=76561198149901382,76561198834008439", new HashMap<>());
        System.out.println(s);
    }
}
