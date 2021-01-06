package com.cj.demoredis.controller;

import com.cj.demoredis.domain.MfrsPlctemplateInfo;
import com.cj.demoredis.service.plctemplate.PlcTemplateService;
import com.cj.demoredis.service.redis.RedisService;
import com.cj.demoredis.utils.MockConfiguration;
import com.cj.demoredis.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class TestController {

    @Autowired
    PlcTemplateService plcTemplateService;

    @Autowired
    RedisService redisService;
//
//    static ScheduledExecutorService scheduledExecutorService =
//            Executors.newScheduledThreadPool(1);

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
            List<MfrsPlctemplateInfo> plctemplates = plcTemplateService.queryPlcTemplateList();
            // 查询qit的
            List<MfrsPlctemplateInfo> templateList = plcTemplateService.queryPlcTemplateTempList();
            // 消耗的模板
            List<MfrsPlctemplateInfo> plcValue = plcTemplateService.queryRefeValue();
            ThreadUtils.exec(plctemplates, plcValue, templateList, plcTemplateService, redisService);
            System.out.println("耗时：" + (System.currentTimeMillis() - s));
        } catch (RuntimeException r) {
            throw new RuntimeException();
        } catch (Exception e) {
            exec();
        }
    }

    @RequestMapping("/")
    public String test(ModelMap map) {
        String end = redisService.get("isEnd2");
        if ("1".equals(end)) {
            map.put("isStart", 1);
        } else {
            map.put("isStart", 0);
        }
        return "index";
    }

    @ResponseBody
    @RequestMapping("/clearRedis")
    public String clearRedis() {
        boolean clearPlc = redisService.clearRedis("list_plc");
        boolean clearEnd = redisService.clearRedis("isEnd2");
        if (clearPlc && clearEnd) {
            return "1";
        } else {
            return "0";
        }
    }


    @ResponseBody
    @RequestMapping("/end")
    public String end() {
        String end2 = redisService.get("isEnd2");
        boolean isEnd2 = redisService.set("isEnd2", "0");
        if ("1".equals(end2)) {
            return "0";
        }
        if ("0".equals(end2)) {
            return "1";
        }
        if (isEnd2) {
            System.out.println("停止成功");
            return "0";
        } else {
            System.out.println("停止失败");
            return "-1";
        }
    }


}
