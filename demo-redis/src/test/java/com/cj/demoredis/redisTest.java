//package com.cj.demoredis;
//
//import com.cj.demoredis.domain.MfrsPlctemplate;
//import com.cj.demoredis.domain.MfrsPlctemplateInfo;
//import com.cj.demoredis.service.plctemplate.PlcTemplateService;
//import com.cj.demoredis.service.redis.RedisService;
//import com.cj.demoredis.utils.MockConfiguration;
//import com.cj.demoredis.utils.ThreadUtils;
//import com.forte.util.Mock;
//import com.forte.util.mockbean.MockObject;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.HashMap;
//import java.util.List;
//
//public class redisTest extends DemoRedisApplicationTests {
//    @Autowired
//    PlcTemplateService plcTemplateService;
//
//    @Autowired
//    RedisService redisService;
//
//    @Test
//    public void s() throws InterruptedException {
//        MockConfiguration.setEnableJsScriptEngine(false);
////        while (true) {
//            try {
//                // 所有模板
//                List<MfrsPlctemplateInfo> plctemplates = plcTemplateService.queryPlcTemplateList();
//                // 查询qit的
//                List<MfrsPlctemplateInfo> templateList = plcTemplateService.queryPlcTemplateTempList();
//                // 消耗的模板
//                List<MfrsPlctemplateInfo> plcValue = plcTemplateService.queryRefeValue();
//                ThreadUtils.exec(plctemplates, plcValue, templateList, plcTemplateService);
////                Thread.sleep(60000);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
////        }
//    }
//
//    @Test
//    public void s2() throws Exception {
//        MockConfiguration.setEnableJsScriptEngine(false);
//        Mock.scan("com.cj.demoredis.domain");
////        Mock.set(MfrsPlctemplate.class,new HashMap<String,Object>(){{
////            put("plctemId")
////        }});
//        MockObject<MfrsPlctemplate> mo = Mock.get(MfrsPlctemplate.class);
//        long start = System.currentTimeMillis();
//        List<MfrsPlctemplate> list = mo.getList(10_00000);
//        for (int i = 0; i < list.size(); i++) {
//            System.out.println(list.get(i).getPlctempId() + "\n");
//        }
//        long end = System.currentTimeMillis();
//        System.out.println("用时：" + (end - start) / 1000 + "秒");
////        List<MfrsPlctemplateInfo> plctemplates = plcTemplateService.queryPlcTemplateList();
////        ThreadUtils.exec(plctemplates);
//
//    }
//
//
//    @Test
//    public void s3() throws Exception {
//        MockConfiguration.setEnableJsScriptEngine(false);
////        Mock.scan("com.cj.demoredis.domain");
//        Mock.set(MfrsPlctemplateInfo.class, new HashMap<String, Object>() {{
//            put("refeValue|0-10", 0);
//        }});
//        MockObject<MfrsPlctemplateInfo> mo = Mock.get(MfrsPlctemplateInfo.class);
//        long start = System.currentTimeMillis();
//        System.out.println(mo.getOne().getRefeValue());
//        long end = System.currentTimeMillis();
//        System.out.println("用时：" + (end - start) / 1000 + "秒");
////        List<MfrsPlctemplateInfo> plctemplates = plcTemplateService.queryPlcTemplateList();
////        ThreadUtils.exec(plctemplates);
//
//    }
//
//
////
////    CountDownLatch countDownLatch = new CountDownLatch(10);
////
////    @Test
////    public void TestThread() {
////        for (int i = 0; i < 10; i++) {
////            new Thread(new Runnable() {
////                @Override
////                public void run() {
////                    countDownLatch.countDown();
////                    userService.queryAll();
////                }
////            }).start();
////
////        }
////        try {
////            countDownLatch.await();
////            Thread.currentThread().join();
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////
////    }
////
////
////    @Test
////    public void a() {
////        String a = "&";
////        System.out.println(StringEscapeUtils.escapeJava(a));
////    }
////
////    @Test
////    public void as() {
////        //集合一
////        List<String> _first = new ArrayList<String>();
////        _first.add("jim");
////        _first.add("tom");
////        _first.add("jack");
//////集合二
////        List<String> _second = new ArrayList<String>();
////        _second.add("jack");
////        _second.add("happy");
////        _second.add("sun");
////        _second.add("good");
////
////        Collection exists = new ArrayList<String>(_second);
////        Collection notexists = new ArrayList<String>(_second);
////
////        exists.removeAll(_first);
////        System.out.println("_second中不存在于_set中的：" + exists);
////        _first.removeAll(exists);
////        System.out.println("_second中存在于_set中的：" + _first);
////    }
////
////
////    @Test
////    public void testdate() {
////        System.out.println(Date2Utils.getNowDate());
////    }
//
//}
