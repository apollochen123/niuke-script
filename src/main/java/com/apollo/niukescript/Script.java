package com.apollo.niukescript;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import static com.apollo.niukescript.SerializationUtil.deserialize;

/**
 * @Desc
 * @Author Apollo
 * @Date 2018/8/14 20:39
 * @Fix
 */
@Service
public class Script {
    //我是小牛客D
    //private static final String COOKIE = "NOWCODERUID=A3F2F8CFA9F3701317809105C90C2513; NOWCODERCLINETID=CB04790E750EDF82F1147317C03A354C; Hm_lvt_a808a1326b6c06c437de769d1b85b870=1534912888; callBack=; t=2A6FF8EA7E476E057EB2708287DF3137; dc_pid_set_next_pre=83857_88226_93479_96308_96856_97186_97029_87134_97185_86645_97173_97184_96144_24037_85611_97183_97058_97126_96264_97113_97146_96323_97176_96667_97063_65751_94358_97179_97178_97027; Hm_lpvt_a808a1326b6c06c437de769d1b85b870=1534912918; SERVERID=547d00d82311952605c62ceac64f21fd|1534912917|1534912886";
    //飞奔的蜗牛2333
    //private static final String COOKIE2 = "NOWCODERUID=FBEF094A8A5A729A1E15D559EBDE75C9; NOWCODERCLINETID=CB04790E750EDF82F1147317C03A354C; Hm_lvt_a808a1326b6c06c437de769d1b85b870=1534861100; callBack=; t=8E434DD47209945DDA1C8980D72EDC26; dc_pid_set_next_pre=83857_88226_93479_96308_96856_96959_96737_96901_96165_96971_96956_96958_96534_96970_96947_96957_96746_96949_96834_96964_82693_96619_92851_93485_96918_96915_92656_96668_96787_96897; SERVERID=aff739a092fc0d444b24c3a30d4864b6|1534861119|1534861098; Hm_lpvt_a808a1326b6c06c437de769d1b85b870=1534861120";
    //妈妈的话
    private static final String COOKIE3 = "NOWCODERUID=90118AED5391C4459D49E1B214E70E2E; NOWCODERCLINETID=CB04790E750EDF82F1147317C03A354C; Hm_lvt_a808a1326b6c06c437de769d1b85b870=1535958366; dc_pid_set_next_pre=88226_93479_99748_101389_102409_102395_102471_102406_97515_99907_79879_98996_102276_102450_102387_86327_101664_102464_99416_101743_102469_101592_102470_101967_102318_101030_101489_101747_101191_102445; callBack=https://www.nowcoder.com/discuss; t=0F7056E5B76EC5E47AEC62CCC69986D8; Hm_lpvt_a808a1326b6c06c437de769d1b85b870=1535958381; gdxidpyhxdE=XU7n8yg7QAev6EddX6Ju8YyLVKSIOiHR4jspZHKD0cWAypAZ1nLrfuxB5EsfU4XEcjqBjUHXmqYoujtsVnuZ6%2BP7aUSoi2weAN8l%5Ct6KQC%5C%2FI%2F3fs5C%5CraPg6XCUbfQ9bNHouQhHUJy6HkpsQsnt0OM%2Bl277Vukkg6JbPLUeE%5C%2FN%2F3kc%3A1535959300971; _9755xjdesxxd_=32; SERVERID=aff739a092fc0d444b24c3a30d4864b6|1535958414|1535958364";

    private static final String CREATE_URL = "https://www.nowcoder.com/comment/create?token=";
    private static final String DELETE_URL = "https://www.nowcoder.com/comment/delete?token=";

    @Autowired
    private RestTemplate restTemplate;

    @Scheduled(initialDelay = 10000, fixedDelay = 1_000 * 60)
    public void schedules() throws InterruptedException {
        System.out.println("\n\n\n" + "===================开始刷帖任务======================" + "         " + new Date());

        //凌晨不发
        if (check1_8()) {
            return;
        }

        String comment = callForCreate("97724", "简历已经发给大佬你了，谢谢了", "451107316", COOKIE3);
        System.out.println("=====================发帖成功===========================" + comment);

        TimeUnit.MINUTES.sleep(15);
        callForDelete(comment, COOKIE3);
        System.out.println("=====================删除成功===========================");

        System.out.println("===================end刷帖任务======================");
    }

    /**
     * @param entityId       帖子id
     * @param commentContent 评论内容
     * @param cookie
     * @return 成功后的id
     */
    private String callForCreate(String entityId, String commentContent, String entityOwnerId, String cookie) {

        ResponseEntity<String> returnRes = restTemplate
                .exchange(CREATE_URL, HttpMethod.POST, getCreateEntity(entityId, entityOwnerId, commentContent, cookie),
                        String.class);

        String result = returnRes.getBody();
        System.out.println(" create success " + result);
        if (result == null || !result.contains("commentId")) {
            System.out.println("ERROR:" + new Date());
            System.exit(-1);
        }
        ResDto res = deserialize(result, ResDto.class);
        return res.getCommentId().toString();
    }

    private void callForDelete(String deleteConmentId, String cookies) {
        restTemplate.exchange(DELETE_URL, HttpMethod.POST, getDeleteEntity(deleteConmentId, cookies), String.class);
    }

    private HttpEntity<MultiValueMap<String, String>> getDeleteEntity(String id, String cookies) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("id", id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Cookie", cookies);
        return new HttpEntity<>(multiValueMap, headers);
    }

    private HttpEntity<MultiValueMap<String, String>> getCreateEntity(String entityId, String entityOwnerId,
            String commentContent, String cookies) {

        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();

        multiValueMap.add("entityType", "8");
        multiValueMap.add("entityOwnerId", entityOwnerId);
        multiValueMap.add("entityId", entityId);
        multiValueMap.add("commentContent", commentContent);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Cookie", cookies);
        return new HttpEntity<>(multiValueMap, headers);
    }

    private boolean check1_8() throws InterruptedException {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (1 < hour && hour < 8) {
            TimeUnit.MINUTES.sleep(30L);
            System.out.println("===================凌晨结束任务======================");
            return true;
        }
        return false;
    }

}
