package com.cj.demoredis.domain;

import com.cj.demoredis.service.data.DataService;
import com.cj.demoredis.service.plctemplate.PlcTemplateService;
import com.cj.demoredis.utils.MockConfiguration;
import com.cj.demoredis.utils.ThreadUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class Job implements Runnable {
    private List<MfrsPlctemplateInfo> plctemplates;
    private CountDownLatch countDownLatch;
    private List<String> strList;
    private List<String> gqsList;
    private List<Integer> list;
    private PlcTemplateService plcTemplateService;
    private DataService dataService;
    private int startIndex;
    private int endIndex;

    public Job(List<MfrsPlctemplateInfo> plctemplates, CountDownLatch countDownLatch, List<String> strList, List<String> gqsList, List<Integer> list, PlcTemplateService plcTemplateService, DataService dataService, int startIndex, int endIndex) {
        this.plctemplates = plctemplates;
        this.countDownLatch = countDownLatch;
        this.strList = strList;
        this.gqsList = gqsList;
        this.list = list;
        this.plcTemplateService = plcTemplateService;
        this.dataService = dataService;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public void run() {
        int i = 0;
        try {
            long s = System.currentTimeMillis();
            long e = 0L;
            if (null != plctemplates) {
                List<MfrsPlctemplateInfo> plcValue = plcTemplateService.queryRefeValue(String.join(",", strList));
                String ids = plcTemplateService.queryPlctempIds(String.join(",", gqsList));
                List<MfrsPlctemplateInfo> tempValue = plcTemplateService.queryRefeValue(String.join(",", ids));
                MockConfiguration.setEnableJsScriptEngine(false);
                for (MfrsPlctemplateInfo plctemplate : plctemplates) {
                    if (plctemplate.getPlcType() != null && list.contains(plctemplate.getPlcType())) {
                        ThreadUtils.commit(plctemplate, plcValue, tempValue, plcTemplateService, dataService);
                    }
                }
            }
            e = System.currentTimeMillis() - s;
            System.out.println(Thread.currentThread().getName() + "线程大小：：" + plctemplates.size() + "：下标：" + startIndex + "-" + endIndex);
            System.out.println("---->用时：" + (e / 1000) + "秒");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();//发出线程任务完成的信号
        }
    }
}
