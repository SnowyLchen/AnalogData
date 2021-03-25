package com.cj.demoredis.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @param <X>       为被处理的数据类型
 * @param <T>返回数据类型 知识点1：X,T为泛型，为什么要用泛型，泛型和Object的区别请看：https://www.cnblogs.com/xiaoxiong2015/p/12705815.html
 */
public abstract class MultiThread<X, T> {

    public static int i = 0;

    /**
     * 知识点2：线程池:https://www.cnblogs.com/xiaoxiong2015/p/12706153.html
     * 线程池
     */
    private final ExecutorService exec;

    /**
     * 知识点3：@author Doung Lea 队列：https://www.cnblogs.com/xiaoxiong2015/p/12825636.html
     */
    private final BlockingQueue<Future<T>> queue = new LinkedBlockingQueue<>();
    /**
     * 知识点4：计数器，还是并发包大神 @author Doug Lea 编写。是一个原子安全的计数器，可以利用它实现发令枪
     * 启动门，当所有线程就绪时调用countDown
     */
    private final CountDownLatch startLock = new CountDownLatch(1);
    /**
     * 结束门
     */
    private final CountDownLatch endLock;
    /**
     * 被处理的数据
     */
    private final List<X> listData;

    /**
     * @param list list.size()为多少个线程处理，list里面的H为被处理的数据
     */
    public MultiThread(List<X> list) {
        if (list != null && list.size() > 0) {
            this.listData = list;
            // 创建线程池，线程池共有nThread个线程
            exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            // 设置结束门计数器，当一个线程结束时调用countDown
            endLock = new CountDownLatch(list.size());
        } else {
            listData = null;
            exec = null;
            endLock = null;
        }
    }

    /**
     * @return 获取每个线程处理结速的数组
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public List<T> getResult() throws InterruptedException, ExecutionException {

        List<T> resultList = new ArrayList<>();
        if (listData != null && listData.size() > 0) {
            // 线程数量
            int nThread = listData.size();
            for (int i = 0; i < nThread; i++) {
                X data = listData.get(i);
                Future<T> future = exec.submit(new Task(i, data) {
                    @Override
                    public T execute(int currentThread, X data) throws InterruptedException {
                        return outExecute(currentThread, data);
                    }
                }); // 将任务提交到线程池
                // 将Future实例添加至队列
                queue.add(future);
            }
            // 所有任务添加完毕，启动门计数器减1，这时计数器为0，所有添加的任务开始执行
            startLock.countDown();
            // 主线程阻塞，直到所有线程执行完成
            endLock.await();
            for (Future<T> future : queue) {
                resultList.add(future.get());
            }
            exec.shutdown(); // 关闭线程池
        }
        return resultList;
    }

    /**
     * 每一个线程执行的功能，需要调用者来实现
     *
     * @param currentThread 线程号
     * @param data          每个线程被处理的数据
     * @return T返回对象
     * @throws InterruptedException
     */
    public abstract T outExecute(int currentThread, X data) throws InterruptedException;

//    /**
//     * 每一个线程执行的功能，需要调用者来实现
//     *
//     * @param currentThread 线程号
//     * @param data          每个线程被处理的数据
//     * @throws InterruptedException
//     * @return T返回对象
//     */
//    public abstract void outVoidExecute(int currentThread, X data) throws InterruptedException;

    /**
     * 线程类
     */
    private abstract class Task implements Callable<T> {

        private int currentThread;// 当前线程号

        private X data;

        public Task(int currentThread, X data) {
            this.currentThread = currentThread;
            this.data = data;
        }

        @Override
        public T call() throws Exception {

            // startLock.await(); // 线程启动后调用await，当前线程阻塞，只有启动门计数器为0时当前线程才会往下执行
            T t = null;
            try {
                t = execute(currentThread, data);
            } finally {
                endLock.countDown(); // 线程执行完毕，结束门计数器减1
            }
            return t;
        }

        /**
         * 每一个线程执行的功能
         *
         * @param currentThread 线程号
         * @param data          每个线程被处理的数据
         * @return T返回对象
         */
        public abstract T execute(int currentThread, X data) throws InterruptedException;
    }
}

