/*
 * Copyright 2015-2020 msun.com All right reserved.
 */
package com.mob.easySearch.support;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lamfire.utils.StringUtils;

/**
 * @author zxc Aug 18, 2016 4:30:03 PM
 */
public class ThreadUtil {

    /** # */
    private final static String    POUND      = "#";
    /** - */
    private final static String    MINUS_SIGN = "-";

    private static ExecutorService pool       = Executors.newFixedThreadPool(50);

    public static void submitTask(Runnable runnable) {
        pool.submit(runnable);
    }

    /**
     * 重复开启 threadNum 个线程来执行 runnable
     * 
     * @param runnable 可执行任务
     * @param threadNum 重复开启的线程个数
     * @param sleepTime 启动完所有线程后，休息 ms
     */
    public static void startThread(Runnable runnable, String threadName, int threadNum, long sleepTime) {
        for (int i = 0; i < threadNum; i++) {
            Thread thread = new Thread(runnable, POUND + StringUtils.defaultIfEmpty(threadName, "Thread") + MINUS_SIGN + i);
            thread.start();
        }
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
        }
    }

    /**
     * 重复开启 threadNum 个线程来执行 runnable
     * 
     * @param runnable 可执行任务
     * @param threadNum 重复开启的线程个数
     * @param sleepTime 启动完所有线程后，休息 ms
     */
    public static void startThread(Runnable runnable, int threadNum, long sleepTime) {
        ThreadUtil.startThread(runnable, "Thread", threadNum, sleepTime);
    }

    /**
     * 开启 1 个线程来执行 runnable
     * 
     * @param runnable 可执行任务
     */
    public static void startThread(Runnable runnable) {
        startThread(runnable, 1, 0);
    }

    /**
     * 开启 1 个线程来执行 runnable
     * 
     * @param runnable 可执行任务
     */
    public static void startThread(Runnable runnable, String threadName) {
        startThread(runnable, StringUtils.trimToEmpty(threadName), 1, 0);
    }

    /**
     * 重复开启 threadNum 个线程来执行 runnable
     * 
     * @param runnable 可执行任务
     * @param sleepTime 重复开启的线程个数
     */
    public static void startThread(Runnable runnable, long sleepTime) {
        startThread(runnable, 1, sleepTime);
    }

    /**
     * Sleep thread without exception.
     * 
     * @param millis
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
