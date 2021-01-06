package com.cj.demoredis.utils;

import com.cj.demoredis.domain.MfrsPlctemplateInfo;
import com.cj.demoredis.service.plctemplate.PlcTemplateService;
import com.cj.demoredis.service.redis.RedisService;
import com.forte.util.Mock;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadUtils {


    public static void exec(List<MfrsPlctemplateInfo> list, List<MfrsPlctemplateInfo> plcValue, List<MfrsPlctemplateInfo> slist, PlcTemplateService plcTemplateService, RedisService redisService) throws InterruptedException {
        String ie = redisService.get("isEnd2");
        if (ie != null && ie.equals("0")) {
            throw new RuntimeException("主线程停止");
        }
        // 一个线程处理的数据大小
        int count = 100;
        // 数据集合大小
        int listSize = list.size();
        // 开启的线程数
        int runSize = (listSize / count) + 1;
        // 创建一个线程池，大小和开启线程一样
        ExecutorService executorService = ThreadPool.createPool("手动洗数据", runSize);
        AtomicInteger index = new AtomicInteger(listSize);
        //循环创建线程
        long s1 = System.currentTimeMillis();
        for (int j = 0; j < (listSize / runSize) + 1; j++) {
            for (int i = 0; i < runSize; i++) {
                long s = System.currentTimeMillis();
                executorService.execute(() -> {
                    try {
                        int idx = index.decrementAndGet();
                        if (idx < 0) {
                            System.out.println("线程:" + Thread.currentThread().getName() + "结束");
                        }
                        System.out.println(idx);
                        if (idx >= 0) {
                            judge(list.get(idx), plcValue, slist, plcTemplateService);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        long e = System.currentTimeMillis();
                        System.out.println("线程:" + Thread.currentThread().getName() + "---->用时：" + (e - s) + "毫秒");
                    }
                });
            }
        }
        long e1 = System.currentTimeMillis();
        System.out.println("线程总用时：" + (e1 - s1) + "毫秒");
        //执行完关闭线程池
        executorService.shutdown();
    }

    // 40-45.0-9
    private static Map<String, Object> createValue(MfrsPlctemplateInfo plctemplateInfo, List<MfrsPlctemplateInfo> plcValue) {
        Integer plcType = plctemplateInfo.getPlcType();
        // 故障 启停 手自动
        Integer[] gqs = new Integer[]{114, 119, 120};
        List<Integer> gqsList = Arrays.asList(gqs);
        // 集分水器压力 末端启停
        Integer[] oGqs = new Integer[]{49, 78, 81, 127, 136};
        List<Integer> ogqsList = Arrays.asList(oGqs);
        // 电，水气流量消耗
//        Integer[] con = new Integer[]{475, 476, 472, 477, 478, 490, 491, 492, 493, 494, 489};
//        List<Integer> conList = Arrays.asList(con);
        // plcid
        Integer plctempId = plctemplateInfo.getPlctempId();
        Map<String, Object> map = new HashMap<>();
        map.put("plctempId", plctempId);
        map.put("siteId", plctemplateInfo.getSiteId());
        map.put("modbus", plctemplateInfo.getModbus());
        map.put("registerLowercase", plctemplateInfo.getRegisterLowercase());
        map.put("delFlag", plctemplateInfo.getDelFlag());
        map.put("refeKey", plctemplateInfo.getRefeKey());
        if (gqsList.contains(plcType)) {
            if (plcValue.size() == 0) {
                if (plcType == 114 || plcType == 120) {
                    map.put("refeValue", RandomProbability(1, 0));
                } else {
                    map.put("refeValue", RandomProbability(0, 1));
                }
            } else {
                for (MfrsPlctemplateInfo mpv : plcValue) {
                    if (mpv.getPlcType().equals(plcType)) {
                        String value = mpv.getRefeValue();
                        if (mpv.getPlctempId().equals(plctempId)) {
                            if (plcType == 119) {
                                if ("0".equals(value)) {
                                    map.put("refeValue", RandomProbability(0, 1));
                                    break;
                                } else if ("1".equals(value)) {
                                    map.put("refeValue", RandomProbability(1, 0));
                                    break;
                                }
                            } else {
                                if ("0".equals(value)) {
                                    map.put("refeValue", RandomProbability(0, 1));
                                    break;
                                } else if ("1".equals(value)) {
                                    map.put("refeValue", RandomProbability(1, 0));
                                    break;
                                } else {
                                    map.put("refeValue", 1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

        } else if (ogqsList.contains(plcType)) {
            map.put("refeValue", 0.0);
        } else if (plctempId == 365 || plctempId == 364 || plctempId == 366) {
            map.put("refeValue|1", new Double[]{0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09});
        } else {
            if (plctemplateInfo.getRegex() == null) {
                map.put("refeValue", 0);
            } else {
                map.put("refeValue|" + plctemplateInfo.getRegex(), 0);
            }
        }
        map.put("plcInfoTime", new Date());
        map.put("updateTime", new Date());
        map.put("createTime", new Date());
        map.put("status", plctemplateInfo.getStatus());
        map.put("mfrsId", plctemplateInfo.getMfrsId());
        return map;
    }

    /**
     * 插入
     *
     * @param mfrsPlctemplateInfo
     * @param plcTemplateService
     */
    private static void commit(MfrsPlctemplateInfo mfrsPlctemplateInfo, List<MfrsPlctemplateInfo> plcValue, List<MfrsPlctemplateInfo> slist, PlcTemplateService plcTemplateService) {
        Mock.reset(MfrsPlctemplateInfo.class, createValue(mfrsPlctemplateInfo, slist));
        MfrsPlctemplateInfo one = Mock.get(MfrsPlctemplateInfo.class).getOne();
        // 电，水气流量消耗
        Integer[] con = new Integer[]{472, 474, 475, 476, 477, 478, 479, 480, 481, 482, 483, 484, 485, 486, 487, 488, 489, 490, 491, 492, 493, 494, 495, 958, 969, 1000, 1001, 1002, 1057, 1058, 1059, 1060, 1061, 1062, 1063, 1065, 1066};
        // 故障
        Integer[] fault = new Integer[]{370, 373, 386, 389, 391, 393, 395, 398, 401, 404, 407, 410, 413, 416, 419, 422, 437, 440, 443, 497, 499, 502, 507, 510, 513, 516, 523, 525, 528, 531, 534, 537, 540, 543, 544, 545, 548, 549, 550, 553, 556, 559, 562, 565, 568, 570, 572, 575, 578, 581, 889, 892, 895, 898, 901, 904, 905, 906, 907, 908, 933, 941, 942, 968, 974, 975, 976, 977, 978, 979, 980, 981, 982, 984, 985, 987, 989, 991, 999, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010, 1011, 1012, 1013, 1014, 1015, 1016, 1017, 1018, 1019, 1020, 1021, 1022, 1023, 1024, 1025, 1026, 1027, 1028, 1029, 1030, 1031, 1032, 1033, 1034, 1035, 1036, 1037, 1073, 1075, 1081, 1087, 1090, 1093, 1096, 1098, 1100, 1102, 1104};
        List<Integer> conList = Arrays.asList(con);
        List<Integer> faultList = Arrays.asList(fault);
        if (!conList.contains(one.getPlctempId()) || plcValue.size() == 0) {
            if (faultList.contains(one.getPlctempId())) {
                plcTemplateService.insertFault(one);
            }
            plcTemplateService.insert(one);
            plcTemplateService.insertTemp(one);
        } else {
            for (int i = 0; i < plcValue.size(); i++) {
                if (one.getPlctempId().equals(plcValue.get(i).getPlctempId())) {
                    // 原来的value+心的value
                    one.setRefeValue(String.valueOf(Double.parseDouble((plcValue.get(i).getRefeValue() != null ? plcValue.get(i).getRefeValue() : "0")) + caluConsumption(one.getPlctempId())));
                    plcTemplateService.insert(one);
                    plcTemplateService.insertTemp(one);
                    break;
                }
            }
        }
    }

    /**
     * 判断单词循环遍历的数组下表
     *
     * @param plcTemplateService
     */
    private static void judge(MfrsPlctemplateInfo mf, List<MfrsPlctemplateInfo> plcValue, List<MfrsPlctemplateInfo> slist, PlcTemplateService plcTemplateService) {
        commit(mf, plcValue, slist, plcTemplateService);
    }

    /**
     * 99.9% (的概率正常 1‰的概率异常)
     *
     * @param h 高概率的
     * @param l 低概率的
     * @return
     */
    private static int RandomProbability(int h, int l) {
        double random = Math.random();
        if (random < 0.999) {
            return h;
        } else {
            return l;
        }
    }

    private static Double caluConsumption(int plcId) {
        Integer[] k1 = {1000, 1001, 1002, 1057, 1058, 1059, 1060, 1061, 1062, 1063, 1065, 1066};
        List<Integer> k1List = Arrays.asList(k1);
        Integer[] k2 = {472, 474, 475, 476, 477, 478, 479, 480, 481, 482, 483, 484, 485};
        List<Integer> k2List = Arrays.asList(k2);
        Integer[] k3 = {486, 487, 488, 489, 490, 491, 492, 493, 494, 495, 958, 969};
        List<Integer> k3List = Arrays.asList(k3);
        DecimalFormat df = new DecimalFormat("0.00");
        if (k1List.contains(plcId)) {
            return Double.parseDouble(df.format(Math.random() * 5));
        } else if (k2List.contains(plcId)) {
            return Double.parseDouble(df.format(Math.random() * 2));
        } else if (k3List.contains(plcId)) {
            return Double.parseDouble(df.format(Math.random() * 8));
        } else {
            System.out.println("不属于消耗类型的id");
            return 0.0;
        }
    }
}
