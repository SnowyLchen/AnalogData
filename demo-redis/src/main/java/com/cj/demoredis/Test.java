//package com.cj.demoredis;
//
//import com.cj.demoredis.domain.MfrsPlctemplateInfo;
//import com.cj.demoredis.service.plctemplate.PlcTemplateService;
//import com.cj.demoredis.service.redis.RedisService;
//import com.cj.demoredis.utils.MockConfiguration;
//import com.cj.demoredis.utils.ThreadUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.List;
//
//@Component
//public class Test {
//
//    @Autowired
//    private PlcTemplateService plcTemplateService;
//
//    @PostConstruct
//    public void Test() {
//        MockConfiguration.setEnableJsScriptEngine(false);
//        while (true) {
//            try {
//                // 所有模板
//                List<MfrsPlctemplateInfo> plctemplates = plcTemplateService.queryPlcTemplateList();
//                // 查询qit的
//                List<MfrsPlctemplateInfo> templateList = plcTemplateService.queryPlcTemplateTempList();
//                // 消耗的模板
//                List<MfrsPlctemplateInfo> plcValue = plcTemplateService.queryRefeValue();
//                ThreadUtils.exec(plctemplates, plcValue, templateList, plcTemplateService);
//                Thread.sleep(30000);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
