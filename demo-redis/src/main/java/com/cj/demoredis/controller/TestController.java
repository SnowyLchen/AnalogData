package com.cj.demoredis.controller;

import com.alibaba.fastjson.JSON;
import com.cj.demoredis.domain.MfrsPlctemplateInfo;
import com.cj.demoredis.service.data.DataService;
import com.cj.demoredis.service.plctemplate.PlcTemplateService;
import com.cj.demoredis.service.redis.RedisService;
import com.cj.demoredis.utils.MockConfiguration;
import com.cj.demoredis.utils.ThreadUtils;
import com.forte.util.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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

    static ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(1);

    @ResponseBody
    @RequestMapping("/rundata")
    public String runtime() {
        String ie = redisService.get("isEnd2");
        if ("1".equals(ie)) {
//            redisService.set("isEnd2", "0");
            return "0";
        } else {
            redisService.set("isEnd2", "1");
        }
//        new Thread(() -> {
//            try {
//                if (isEnd2) {
//                    throw new RuntimeException("主线程停止");
//                }
//                exec(s);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).start();
//        scheduledExecutorService.schedule(() -> {
//            try {
//                exec(s);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }, 35, TimeUnit.SECONDS);
        while (true) {
            MockConfiguration.setEnableJsScriptEngine(false);
            try {
                exec();
                Thread.sleep(40000);
            } catch (Exception e) {
                return "1";
            }
        }
    }

    void exec() {
        try {
            long s = System.currentTimeMillis();
            // 所有模板
            List<MfrsPlctemplateInfo> plctemplates = plcTemplateService.queryPlcTemplateList(null);
            // 查询qit的
            List<MfrsPlctemplateInfo> templateList = plcTemplateService.queryPlcTemplateTempList();
            // 消耗的模板
            List<MfrsPlctemplateInfo> plcValue = plcTemplateService.queryRefeValue(String.join(",", Arrays.toString(dataService.getConsumption())));
//            ThreadUtils.exec(plctemplates, plcValue, templateList, plcTemplateService, redisService);
            System.out.println("耗时：" + (System.currentTimeMillis() - s));
        } catch (RuntimeException r) {
            throw new RuntimeException();
        } catch (Exception e) {
            exec();
        }
    }

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
    @RequestMapping("/aa")
    public String aa() {
        try {
//            // 所有模板
//            List<MfrsPlctemplateInfo> plctemplates = plcTemplateService.queryPlcTemplateList();
//            List<MfrsPlctemplateInfo> p1 = plctemplates.subList(0, 200);
//            List<MfrsPlctemplateInfo> p2 = plctemplates.subList(200, 400);
//            List<MfrsPlctemplateInfo> p3 = plctemplates.subList(400, 600);
//            List<MfrsPlctemplateInfo> p4 = plctemplates.subList(600, plctemplates.size());
//            // 查询qit的
//            List<MfrsPlctemplateInfo> templateList = plcTemplateService.queryPlcTemplateTempList();
//            // 消耗的模板
//            List<MfrsPlctemplateInfo> plcValue = plcTemplateService.queryRefeValue();
//            ThreadUtils.exec(plctemplates, plcValue, templateList, plcTemplateService, redisService);
            MockConfiguration.setEnableJsScriptEngine(false);
            long s = System.currentTimeMillis();
            int i = 0;
            while (true) {
                Map<String, Object> map = new HashMap<>();
                map.put("refeValue|1-3.2", 0);
                Mock.reset(MfrsPlctemplateInfo.class, map);
                Mock.get(MfrsPlctemplateInfo.class).getOne();
//                threadUtils.createValue(plctemplates.get(i), templateList);
//                threadUtils.judge(plctemplates.get(i), plcValue, templateList, plcTemplateService, null);
                System.out.println("---->用时：" + (System.currentTimeMillis() - s) + "毫秒");
                i++;
                if (i >= 1000) {
                    break;
                }
            }
//            for (int i = 0; i < 1000; i++) {
//                Map<String, Object> map = new HashMap<>();
//                map.put("refeValue|1-3.2", 0);
//                Mock.reset(MfrsPlctemplateInfo.class, map);
//                Mock.get(MfrsPlctemplateInfo.class).getOne();
////                threadUtils.createValue(plctemplates.get(i), templateList);
////                threadUtils.judge(plctemplates.get(i), plcValue, templateList, plcTemplateService, null);
//                System.out.println("---->用时：" + (System.currentTimeMillis() - s) + "毫秒");
//            }
//            MultiThread<MfrsPlctemplateInfo, Boolean> m1 = new MultiThread<MfrsPlctemplateInfo, Boolean>(plctemplates) {
//                @Override
//                public Boolean outExecute(int currentThread, MfrsPlctemplateInfo data) throws InterruptedException {
//                    boolean flag = true;
//                    try {
//                        threadUtils.judge(data, plcValue, templateList, plcTemplateService, threadUtils.createValue(data, templateList));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        flag = false;
//                    } finally {
//                        long e = System.currentTimeMillis();
//                        System.out.println("线程:" + Thread.currentThread().getName() + "---->用时：" + (e - s) + "毫秒");
//                    }
//                    return flag;
//                }
//            };
            long e = System.currentTimeMillis();
//            List<Boolean> list = m1.getResult();
//            List<ResultVo> list2 = m2.getResult();
//            List<ResultVo> list3 = m3.getResult();
//            List<ResultVo> list4 = m4.getResult();
//            List<ResultVo> list1 = multiThread1.getResult();
            // 获取每一批次处理结果
//            System.out.println("获取list处理结果........................");
//            for (Boolean vo : list) {
//                System.out.println(vo);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1";
    }


    @ResponseBody
    @RequestMapping("/gpd")
    public void aaa(Integer count) throws InterruptedException {
        if (isEnd(null)) {
            return;
        }
        while (true) {
            if (isEnd("loop")) {
                return;
            }
            long s = System.currentTimeMillis();
            try {
                ThreadUtils.execute(plcTemplateService, dataService);
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
                System.out.println("出错");
                ThreadUtils.execute(plcTemplateService, dataService);
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
