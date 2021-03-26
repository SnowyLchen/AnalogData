package com.cj.demoredis.utils;

import com.cj.demoredis.domain.Job;
import com.cj.demoredis.domain.MfrsPlctemplateInfo;
import com.cj.demoredis.service.data.DataService;
import com.cj.demoredis.service.plctemplate.PlcTemplateService;
import com.cj.demoredis.service.redis.RedisService;
import com.forte.util.Mock;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
//@PropertySource("classpath:application.yml")
//@SpringBootApplication
public class ThreadUtils {
//
//    @Autowired
//    private DataService dataService;

    public void exec(List<MfrsPlctemplateInfo> list, List<MfrsPlctemplateInfo> plcValue, List<MfrsPlctemplateInfo> slist, PlcTemplateService plcTemplateService, RedisService redisService) throws InterruptedException {
        String ie = redisService.get("isEnd2");
        if (ie != null && "0".equals(ie)) {
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
                            judge(list.get(idx), plcValue, slist, plcTemplateService, list.get(idx));
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
    public static Map<String, Object> createValue(MfrsPlctemplateInfo plctemplateInfo, List<MfrsPlctemplateInfo> plcValue, DataService dataService) {
        Map<String, Object> map = new HashMap<>();
        Integer plcType = plctemplateInfo.getPlcType();
        // 故障 启停 手自动
        Integer[] gqs = dataService.getGqs();
        List<Integer> gqsList = Arrays.asList(gqs);
        // 集分水器压力 末端启停
        Integer[] oGqs = dataService.getOgqs();
        List<Integer> ogqsList = Arrays.asList(oGqs);
        // plcid
        Integer plctempId = plctemplateInfo.getPlctempId();
        map.put("plctempId", plctempId);
        map.put("siteId", plctemplateInfo.getSiteId());
        map.put("modbus", plctemplateInfo.getModbus());
        map.put("registerLowercase", plctemplateInfo.getRegisterLowercase());
        map.put("delFlag", plctemplateInfo.getDelFlag());
        map.put("refeKey", plctemplateInfo.getRefeKey());
        // 119->故障 250->烟感 251->红外 252->温感 253->明火 254->门禁
        Integer[] plcTypes = {119, 250, 251, 252, 253, 254, 320};
        List<Integer> plcTypeList = Arrays.asList(plcTypes);
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
                            if (plcTypeList.contains(plcType)) {
                                if ("0".equals(value) || value == null) {
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
    public static void commit(MfrsPlctemplateInfo mfrsPlctemplateInfo, List<MfrsPlctemplateInfo> plcValue, List<MfrsPlctemplateInfo> slist, PlcTemplateService plcTemplateService, DataService dataService) throws Exception {
        String uuid = UUID.randomUUID().toString();
        MockUtilsCopy.set(uuid, createValue(mfrsPlctemplateInfo, slist, dataService));
        MockUtilsCopy.clearMock();
        MfrsPlctemplateInfo one = (MfrsPlctemplateInfo) MockUtilsCopy.mapToObject(MockUtilsCopy.get(uuid).getOne(), MfrsPlctemplateInfo.class);
        // 电，水气流量消耗
        Integer[] con = dataService.getConsumption();
        Integer[] electric = dataService.getElectric();
        // 故障
        Integer[] fault = dataService.getFault();
        List<Integer> conList = Arrays.asList(con);
        List<Integer> electricList = Arrays.asList(electric);
        List<Integer> faultList = Arrays.asList(fault);
        Set<Integer> realValueId = plcValue.stream().collect(Collectors.groupingBy(MfrsPlctemplateInfo::getPlctempId, Collectors.counting())).keySet();
        if ((!conList.contains(one.getPlctempId()) && !electricList.contains(one.getPlctempId())) || plcValue.size() == 0 || !realValueId.contains(one.getPlctempId())) {
            if (faultList.contains(one.getPlctempId())) {
                plcTemplateService.insertFault(one);
            }
            plcTemplateService.insert(one);
            plcTemplateService.insertTemp(one);
//            System.out.println("第一：：：" + one);
        } else {
            for (int i = 0; i < plcValue.size(); i++) {
                if (one.getPlctempId().equals(plcValue.get(i).getPlctempId())) {
                    // 原来的value+心的value
                    one.setRefeValue(String.valueOf(Double.parseDouble((plcValue.get(i).getRefeValue() != null ? plcValue.get(i).getRefeValue() : "0")) + caluConsumption(one.getPlctempId(), dataService)));
//                    System.out.println("第二：：：" + one);
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
    public void judge(MfrsPlctemplateInfo mf, List<MfrsPlctemplateInfo> plcValue, List<MfrsPlctemplateInfo> slist, PlcTemplateService plcTemplateService, MfrsPlctemplateInfo mi) {
//        commit(mf, plcValue, slist, plcTemplateService, mi);
    }

    /**
     * 99.9% 的概率正常 0.1‰的概率异常)
     *
     * @param h 高概率的
     * @param l 低概率的
     * @return
     */
    public static int RandomProbability(int h, int l) {
        double random = Math.random();
        if (random < 0.999) {
            return h;
        } else {
            return l;
        }
    }

    /**
     * 消耗类型计算
     *
     * @param plcId
     * @return
     */
    public static Double caluConsumption(int plcId, DataService dataService) {
        Integer[] k1 = dataService.getV1();
        List<Integer> k1List = Arrays.asList(k1);
        Integer[] k2 = dataService.getV2();
        List<Integer> k2List = Arrays.asList(k2);
        Integer[] k3 = dataService.getV3();
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

//    public static void main(String[] args) throws InterruptedException {
//        ConfigurableApplicationContext applicationContext = SpringApplication.run(DemoRedisApplication.class, args);
//        PlcTemplateService plcTemplateService = applicationContext.getBean(PlcTemplateService.class);
//        DataService dataService = applicationContext.getBean(DataService.class);
//        try {
//            execute(plcTemplateService, dataService);
//        } catch (Exception e) {
//            System.out.println("出错");
//            execute(plcTemplateService, dataService);
//        }
//    }
//
//    static ScheduledExecutorService scheduledExecutorService =
//            Executors.newScheduledThreadPool(10);

    private static void thread(int listSize, MfrsPlctemplateInfo plctemplate, List<MfrsPlctemplateInfo> plcValue, List<MfrsPlctemplateInfo> tempValue, PlcTemplateService plcTemplateService, DataService dataService) throws InterruptedException {
        // 一个线程处理的数据大小
        int count = 100;
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
                            commit(plctemplate, plcValue, tempValue, plcTemplateService, dataService);
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

//        commit(plctemplate, plcValue, tempValue, plcTemplateService, dataService);

    }

    public static void execute(PlcTemplateService plcTemplateService, DataService dataService) throws InterruptedException {
        // 查询模板
        List<MfrsPlctemplateInfo> plctemplates = plcTemplateService.queryPlcTemplateList(null);
        // 查询消耗的
        Integer[] consumption = dataService.getConsumption();
        Integer[] electric = dataService.getElectric();
        Integer[] gqs = dataService.getGqs();
        Integer[] contact = contact(consumption, electric);
        List<String> strList = Arrays.stream(contact)
                .map(String::valueOf)
                .collect(Collectors.toList());
        List<String> gqsList = Arrays.stream(gqs)
                .map(String::valueOf)
                .collect(Collectors.toList());
        Integer[] types = dataService.getTypes();
        Integer[] hvac = dataService.getHVAC();
        Integer[] HVAC_GPD = contact(types, hvac);
        List<Integer> list = Arrays.asList(HVAC_GPD);
        int count = 50;
        int listSize = plctemplates.size();
        //线程数
        int runSize = (listSize / count) + 1;
        ThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(runSize);
        CountDownLatch countDownLatch = new CountDownLatch(runSize);
        for (int i = 0; i < runSize; i++) {
            List<MfrsPlctemplateInfo> newList = null;
            int startIndex = 0;
            int endIndex = 0;
            if ((i + 1) == runSize) {
                startIndex = (i * count);
                endIndex = plctemplates.size();
                newList = plctemplates.subList(startIndex, endIndex);
            } else {
                startIndex = i * count;
                endIndex = (i + 1) * count - 1;
                newList = plctemplates.subList(startIndex, endIndex);
            }

            Job task = new Job(newList, countDownLatch, strList, gqsList, list, plcTemplateService, dataService, startIndex, endIndex);
            executor.execute(task);
        }
        countDownLatch.await();  //主线程等待所有线程完成任务
        //所有线程完成任务后的一些业务
        System.out.println("插入数据完成!");
        //关闭线程池
        executor.shutdown();
    }

    public static Integer[] contact(Integer[] a, Integer[] b) {
        Integer[] result = new Integer[a.length + b.length];
        for (int i = 0; i < result.length; i++) {
            if (i < a.length) {
                result[i] = a[i];
            } else {
                result[i] = b[i - a.length];
            }
        }
        return result;
    }

    private static Map<String, Object> getMap(MfrsPlctemplateInfo plctemplateInfo) {
        Integer plctempId = plctemplateInfo.getPlctempId();
        Map<String, Object> map = new HashMap<>();
        map.put("plctempId", plctempId);
        map.put("siteId", plctemplateInfo.getSiteId());
        map.put("modbus", plctemplateInfo.getModbus());
        map.put("registerLowercase", plctemplateInfo.getRegisterLowercase());
        map.put("delFlag", plctemplateInfo.getDelFlag());
        map.put("refeKey", plctemplateInfo.getRefeKey());
        map.put("plcInfoTime", new Date());
        map.put("updateTime", new Date());
        map.put("createTime", new Date());
        map.put("status", plctemplateInfo.getStatus());
        map.put("mfrsId", plctemplateInfo.getMfrsId());
        if (plctemplateInfo.getRegex() == null) {
            map.put("refeValue", 0);
        } else {
            map.put("refeValue|" + plctemplateInfo.getRegex(), 0);
        }
        return map;
    }


    public static void main(String[] args) {
        MockConfiguration.setEnableJsScriptEngine(false);
        long s = System.currentTimeMillis();
//        Integer[] types = {220, 222, 258, 259, 238, 227, 245, 242, 257, 256, 261, 262, 237, 228};
        for (int i = 0; i < 2; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("refeValue|1-1.1", 0);
            Mock.reset(MfrsPlctemplateInfo.class, map);
            MfrsPlctemplateInfo one = Mock.get(MfrsPlctemplateInfo.class).getOne();
            System.out.println(one.getRefeValue() + "-------------");
            System.out.println("---->用时：" + (System.currentTimeMillis() - s) + "毫秒");
        }
    }
}
