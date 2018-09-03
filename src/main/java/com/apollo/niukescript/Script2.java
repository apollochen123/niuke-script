package com.apollo.niukescript;

import static com.apollo.niukescript.SerializationUtil.deserialize;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
public class Script2 {

    // 我是小牛客D
    private static final String COOKIE = "NOWCODERUID=E79B56A5DB1B49FBC74A66F27F33DEBF; NOWCODERCLINETID=CB04790E750EDF82F1147317C03A354C; Hm_lvt_a808a1326b6c06c437de769d1b85b870=1535958254; callBack=; t=87B0C6D5556C89ACD639DE073358A4D9; dc_pid_set_next_pre=88226_93479_99748_101389_102409_102276_102450_102387_86327_101664_102464_99416_101743_102469_101592_102470_102406_101967_102318_101030_101489_101747_101191_102445_100614_102302_97389_102468_102423_100292; Hm_lpvt_a808a1326b6c06c437de769d1b85b870=1535958279; SERVERID=547d00d82311952605c62ceac64f21fd|1535958307|1535958252; gdxidpyhxdE=ss9zWVGBz6rLnG51WmB%2Brza16kJJrt3cmCBS0aVdsiAvnqyDLG%2BaJO7kUakaLRuu5VclIZD5eDZkfIiWJq%5CKiHXVgy8dhtOs4A%2BG%5CqruPeXhL%5C6ChV95Y1bo%2FKBjPRZSq6036K0WQzKz6TWpvXavtnSTm2zU%2FPE4N3KkoAhYD3XN%2BD%2Bx%3A1535959208251; _9755xjdesxxd_=32";

    private static final String CREATE_URL = "https://www.nowcoder.com/comment/create?token=";
    private static final String DELETE_URL = "https://www.nowcoder.com/comment/delete?token=";

    @Autowired
    private RestTemplate restTemplate;

    @Scheduled(initialDelay = 600*1000, fixedDelay = 1_000 * 60)
    public void scripthuya() throws InterruptedException {
        System.out.println("\n\n\n" + "===================开始刷帖任务======================" + "         " + new Date());
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (0 < hour && hour < 8) {
            System.out.println("===================凌晨结束任务======================");
            TimeUnit.MINUTES.sleep(30);
            return;
        }

        System.out.println("=====================HUYA发帖成功===========================");
        String huyaCommentContentId = callForCreate("97515", "简历已投，谢谢大佬内推", COOKIE);

        TimeUnit.MINUTES.sleep(15L);

        System.out.println("=============================delete task=============================" + new Date());
        restTemplate.exchange(DELETE_URL, HttpMethod.POST, getDeleteEntity(huyaCommentContentId, COOKIE), String.class);
    }

    private String callForCreate(String entityId, String commentContent, String cookie) {
        ResponseEntity<String> returnRes = restTemplate.exchange(CREATE_URL, HttpMethod.POST,
                getCreateEntity(entityId, commentContent, cookie), String.class);
        String result = returnRes.getBody();
        System.out.println("create success " + result);
        if (result == null || !result.contains("commentId")) {
            System.exit(-1);
        }
        ResDto res = deserialize(result, ResDto.class);
        return res.getCommentId().toString();
    }

    private HttpEntity<MultiValueMap<String, String>> getDeleteEntity(String id, String cookies) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("id", id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Cookie", cookies);
        return new HttpEntity<>(multiValueMap, headers);
    }

    private HttpEntity<MultiValueMap<String, String>> getCreateEntity(String entityId, String commentContent,
            String cookies) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        // multiValueMap.add("entityType", "8");
        // multiValueMap.add("entityOwnerId", "451107316");

        // 虎牙直播-金牌推荐人
        multiValueMap.add("entityType", "8");
        multiValueMap.add("entityOwnerId", "981431200");
        multiValueMap.add("entityId", entityId);
        multiValueMap.add("commentContent", commentContent  + "。");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Cookie", cookies);
        return new HttpEntity<>(multiValueMap, headers);
    }

}
