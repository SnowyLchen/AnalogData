package com.cj.demoredis.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author chen
 */
public class ThreadPool {

    // 线程数
//    public static final int THREAD_CORE_POOL_SIZE = 10;
//    public static final int THREAD_MAX_POOL_SIZE = 200;

    public static ThreadPoolExecutor createPool(String poolName,int corePoolSize) throws InterruptedException {
        // 使用 ThreadFactoryBuilder 创建自定义线程名称的 ThreadFactory
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat(poolName + "%d").build();
        // 创建线程池，其中任务队列需要结合实际情况设置合理的容量
        return new ThreadPoolExecutor(corePoolSize,
                corePoolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }
}
