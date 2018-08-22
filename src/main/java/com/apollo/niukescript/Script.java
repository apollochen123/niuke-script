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
    private static final String COOKIE = "NOWCODERUID=A3F2F8CFA9F3701317809105C90C2513; NOWCODERCLINETID=CB04790E750EDF82F1147317C03A354C; Hm_lvt_a808a1326b6c06c437de769d1b85b870=1534912888; callBack=; t=2A6FF8EA7E476E057EB2708287DF3137; dc_pid_set_next_pre=83857_88226_93479_96308_96856_97186_97029_87134_97185_86645_97173_97184_96144_24037_85611_97183_97058_97126_96264_97113_97146_96323_97176_96667_97063_65751_94358_97179_97178_97027; Hm_lpvt_a808a1326b6c06c437de769d1b85b870=1534912918; SERVERID=547d00d82311952605c62ceac64f21fd|1534912917|1534912886";
    //飞奔的蜗牛2333
    //private static final String COOKIE2 = "NOWCODERUID=FBEF094A8A5A729A1E15D559EBDE75C9; NOWCODERCLINETID=CB04790E750EDF82F1147317C03A354C; Hm_lvt_a808a1326b6c06c437de769d1b85b870=1534861100; callBack=; t=8E434DD47209945DDA1C8980D72EDC26; dc_pid_set_next_pre=83857_88226_93479_96308_96856_96959_96737_96901_96165_96971_96956_96958_96534_96970_96947_96957_96746_96949_96834_96964_82693_96619_92851_93485_96918_96915_92656_96668_96787_96897; SERVERID=aff739a092fc0d444b24c3a30d4864b6|1534861119|1534861098; Hm_lpvt_a808a1326b6c06c437de769d1b85b870=1534861120";
    //妈妈的话
    //private static final String COOKIE3 = "NOWCODERUID=2031A3ED933AC7E917F3520A3E2D12DD; NOWCODERCLINETID=CB04790E750EDF82F1147317C03A354C; Hm_lvt_a808a1326b6c06c437de769d1b85b870=1534938794; callBack=; t=58A309F0B3F0864637AA0825D1636713; dc_pid_set_next_pre=83857_88226_93479_96308_96856_97234_91343_97303_97037_97256_97058_94350_89212_97116_97411_96216_96934_97415_97386_97321_97262_97328_96951_96901_97412_95037_97395_95109_97416_97384; Hm_lpvt_a808a1326b6c06c437de769d1b85b870=1534938869; SERVERID=547d00d82311952605c62ceac64f21fd|1534938869|1534938792";

    private static final List<String> cookies = new ArrayList<>();

    private static final String CREATE_URL = "https://www.nowcoder.com/comment/create?token=";
    private static final String DELETE_URL = "https://www.nowcoder.com/comment/delete?token=";
    private static volatile int flag = 0;

    public static final Map<String, String> idAndCookie = new ConcurrentHashMap<>();

    static {
        cookies.add(COOKIE);
        //        cookies.add(COOKIE2);
        //cookies.add(COOKIE3);
    }

    @Autowired
    private RestTemplate restTemplate;

    @Scheduled(initialDelay = 10000, fixedDelay = 1_000 * 1800)
    public void schedules() throws InterruptedException {
        System.out.println(
                "\n\n\n" + "===================开始刷帖任务======================" + flag + "         " + new Date());
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (1 < hour && hour < 8) {
            System.out.println("===================凌晨结束任务======================" + flag);
            return;
        }
        //        System.out.println("=====================YY发帖成功===========================" + flag + "         " + new Date());
        //        String yyCommentContentId = callForCreate("88554", "YY福利怎么样？", getCookie());
        //        if (yyCommentContentId.equals("error")) {
        //            flag++;
        //            return;
        //        }
        //        idAndCookie.put(yyCommentContentId, getCookie());
        //
        //        TimeUnit.MINUTES.sleep(15L);

        System.out.println("=====================HUYA发帖成功===========================" + flag);
        String huyaCommentContentId = callForCreate("97367", "简历已投，谢谢大佬内推。", getCookie());
        if (huyaCommentContentId.equals("error")) {
            flag++;
            return;
        }
        idAndCookie.put(huyaCommentContentId, getCookie());

        System.out.println("===================end刷帖任务======================" + flag);
        flag++;
    }

    @Scheduled(initialDelay = 5000, fixedDelay = 1_000 * 1700)
    public void deleteTask() {
        System.out.println("=============================delete task=============================");
        idAndCookie.entrySet().stream().peek(System.out::println).forEach((it) -> {
            restTemplate
                    .exchange(DELETE_URL, HttpMethod.POST, getDeleteEntity(it.getKey(), it.getValue()), String.class);
        });
    }

    private String getCookie() {
        if (CollectionUtils.isEmpty(cookies)) {
            System.out.println("==============cookies耗尽=========================");
            if (!idAndCookie.isEmpty()) {
                deleteTask();
            }
            System.exit(0);
        }
        return cookies.get(flag % cookies.size());
    }

    /**
     * @param entityId       帖子id
     * @param commentContent 评论内容
     * @param cookie
     * @return 成功后的id
     */
    private String callForCreate(String entityId, String commentContent, String cookie) {
        ResponseEntity<String> returnRes = restTemplate
                .exchange(CREATE_URL, HttpMethod.POST, getCreateEntity(entityId, commentContent, cookie),
                        String.class);
        String result = returnRes.getBody();
        System.out.println(flag + ":   create success " + result);
        if (result == null || !result.contains("commentId")) {
            System.out.println("ERROR:" + new Date());
            //移除这个cookie
            cookies.remove(cookie);
            //停止脚本
            if (CollectionUtils.isEmpty(cookies)) {
                if (!idAndCookie.isEmpty()) {
                    deleteTask();
                }
                System.exit(-1);
            }
            return "error";
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
        //        multiValueMap.add("entityType", "8");
        //        multiValueMap.add("entityOwnerId", "451107316");

        //妈妈话
        multiValueMap.add("entityType", "8");
        multiValueMap.add("entityOwnerId", "981431200");

        multiValueMap.add("entityId", entityId);
        multiValueMap.add("commentContent", commentContent + System.currentTimeMillis() + "。");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Cookie", cookies);
        return new HttpEntity<>(multiValueMap, headers);
    }

    public static void main(String[] args) {
        List<String> l = new ArrayList<>();
        l.add("a");
        l.add("b");
        l.add("c");
        for (int i = 0; i < 100; i++) {
            if (l.size() == 0) {
                System.exit(0);
            }
            System.out.println(l.get(i % l.size()));
            if (i % 10 == 0) {
                String rm = l.get(i % l.size());
                l.remove(rm);
            }
        }

    }
}
