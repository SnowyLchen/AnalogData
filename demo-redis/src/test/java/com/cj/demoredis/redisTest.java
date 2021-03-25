//package com.cj.demoredis;
//
//import com.cj.demoredis.service.data.DataService;
//import com.cj.demoredis.service.plctemplate.PlcTemplateService;
//import com.cj.demoredis.service.redis.RedisService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class redisTest extends DemoRedisApplicationTests {
//    @Autowired
//    PlcTemplateService plcTemplateService;
//
//    @Autowired
//    RedisService redisService;
//    @Autowired
//    private DataService dataService;
////    @Test
////    public void s() throws InterruptedException {
////        MockConfiguration.setEnableJsScriptEngine(false);
//////        while (true) {
////            try {
////                // 所有模板
////                List<MfrsPlctemplateInfo> plctemplates = plcTemplateService.queryPlcTemplateList();
////                // 查询qit的
////                List<MfrsPlctemplateInfo> templateList = plcTemplateService.queryPlcTemplateTempList();
////                // 消耗的模板
////                List<MfrsPlctemplateInfo> plcValue = plcTemplateService.queryRefeValue();
////                ThreadUtils.exec(plctemplates, plcValue, templateList, plcTemplateService);
//////                Thread.sleep(60000);
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
//////        }
////    }
////
////    @Test
////    public void s2() throws Exception {
////        MockConfiguration.setEnableJsScriptEngine(false);
////        Mock.scan("com.cj.demoredis.domain");
//////        Mock.set(MfrsPlctemplate.class,new HashMap<String,Object>(){{
//////            put("plctemId")
//////        }});
////        MockObject<MfrsPlctemplate> mo = Mock.get(MfrsPlctemplate.class);
////        long start = System.currentTimeMillis();
////        List<MfrsPlctemplate> list = mo.getList(10_00000);
////        for (int i = 0; i < list.size(); i++) {
////            System.out.println(list.get(i).getPlctempId() + "\n");
////        }
////        long end = System.currentTimeMillis();
////        System.out.println("用时：" + (end - start) / 1000 + "秒");
//////        List<MfrsPlctemplateInfo> plctemplates = plcTemplateService.queryPlcTemplateList();
//////        ThreadUtils.exec(plctemplates);
////
////    }
//
//
////    @Test
////    public void s3() throws Exception {
////        MockConfiguration.setEnableJsScriptEngine(false);
//////        Mock.scan("com.cj.demoredis.domain");
////        Mock.set(MfrsPlctemplateInfo.class, new HashMap<String, Object>() {{
////            put("refeValue|0-10", 0);
////        }});
////        MockObject<MfrsPlctemplateInfo> mo = Mock.get(MfrsPlctemplateInfo.class);
////        long start = System.currentTimeMillis();
////        System.out.println(mo.getOne().getRefeValue());
////        long end = System.currentTimeMillis();
////        System.out.println("用时：" + (end - start) / 1000 + "秒");
//////        List<MfrsPlctemplateInfo> plctemplates = plcTemplateService.queryPlcTemplateList();
//////        ThreadUtils.exec(plctemplates);
////
////    }
//
//
////    @Test
////    public void s3() throws Exception {
////        List<MfrsPlctemplateInfo> plctemplates = plcTemplateService.queryPlcTemplateList(181);
////        for (MfrsPlctemplateInfo plctemplate : plctemplates) {
////            getMap(plctemplate);
////        }
////
////    }
//
////    private Map<String, Object> getMap(MfrsPlctemplateInfo plctemplateInfo) {
////        Integer plctempId = plctemplateInfo.getPlctempId();
////        Map<String, Object> map = new HashMap<>();
////        map.put("plctempId", plctempId);
////        map.put("siteId", plctemplateInfo.getSiteId());
////        map.put("modbus", plctemplateInfo.getModbus());
////        map.put("registerLowercase", plctemplateInfo.getRegisterLowercase());
////        map.put("delFlag", plctemplateInfo.getDelFlag());
////        map.put("refeKey", plctemplateInfo.getRefeKey());
////        map.put("plcInfoTime", new Date());
////        map.put("updateTime", new Date());
////        map.put("createTime", new Date());
////        map.put("status", plctemplateInfo.getStatus());
////        map.put("mfrsId", plctemplateInfo.getMfrsId());
////        if (plctemplateInfo.getRegex() == null) {
////            map.put("refeValue", 0);
////        } else {
////            map.put("refeValue|" + plctemplateInfo.getRegex(), 0);
////        }
////        return map;
////    }
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
