package com.cj.demoredis.controller;

import com.alibaba.fastjson.JSON;
import com.cj.demoredis.service.data.DataService;
import com.cj.demoredis.service.plctemplate.PlcTemplateService;
import com.cj.demoredis.service.redis.RedisService;
import com.cj.demoredis.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

@Controller
public class TestController {

    @Autowired
    PlcTemplateService plcTemplateService;

    @Autowired
    RedisService redisService;
    @Autowired
    ThreadUtils threadUtils;
    @Autowired
    DataService dataService;


    @RequestMapping("/")
    public String test(ModelMap map) throws UnsupportedEncodingException {
        String end = redisService.get("isEnd2");
        if ("1".equals(end)) {
            map.put("isStart", 1);
        } else {
            map.put("isStart", 0);
        }
        if (redisService.hasRedisKey("map")) {
            map.put("temp", URLEncoder.encode(redisService.get("map"), "utf-8"));
        }
        return "index";
    }

    @ResponseBody
    @RequestMapping("/clearRedis")
    public String clearRedis() {
        boolean clearPlc = redisService.clearRedis("list_plc");
        boolean clearEnd = redisService.clearRedis("isEnd2");
        boolean map = redisService.clearRedis("map");
        if (clearPlc && clearEnd && map) {
            return "1";
        } else {
            return "0";
        }
    }

    @ResponseBody
    @RequestMapping("/seeRedis")
    public String seeRedis(ModelMap map) {
        map.put("list_plc", redisService.get("list_plc"));
        map.put("isEnd2", redisService.get("isEnd2"));
        map.put("map", redisService.get("map"));
        return redisService.get("map");
    }


    @ResponseBody
    @RequestMapping("/end")
    public String end() {
        redisService.set("isEnd2", "0");
        redisService.clearRedis("map");
        String e = redisService.get("isEnd2");
        if ("0".equals(e)) {
            System.out.println("停止成功");
            return "0";
        } else {
            System.out.println("停止失败");
            return "-1";
        }
    }


    @ResponseBody
    @RequestMapping("/gpd")
    public void aaa(Integer count, Integer siteId) throws InterruptedException {
        if (isEnd(null)) {
            return;
        }
        while (true) {
            if (isEnd("loop")) {
                return;
            }
            long s = System.currentTimeMillis();
            try {
                ThreadUtils.execute(plcTemplateService, dataService, siteId);
                if (count == null) {
                    count = 1;
                }
                count++;
                long e = System.currentTimeMillis();
                Integer finalCount = count;
                HashMap<String, String> hashMap = new HashMap<String, String>() {{
                    put("count", finalCount.toString());
                    put("time", (e - s) / 1000 + "秒");
                }};
                redisService.set("map", JSON.toJSONString(hashMap));
                Thread.sleep(30000 - (e - s));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("出错");
                ThreadUtils.execute(plcTemplateService, dataService, siteId);
                Thread.sleep(10000);
            }
        }
    }

    private boolean isEnd(String loop) {
        String ie = redisService.get("isEnd2");
        if (loop == null) {
            if (!"1".equals(ie)) {
                redisService.set("isEnd2", "1");
                return false;
            } else {
                return true;
            }
        } else {
            if (!"1".equals(ie)) {
                return true;
            } else {
                return false;
            }
        }
    }
}
