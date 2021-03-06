package com.alibaba.mtc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author ding.lid
 */
public class Utils {
    private static class SleepTask implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void expandThreadPool(ExecutorService executor) {
        try {
            List<Future<?>> ret = new ArrayList<Future<?>>();
            for (int i = 0; i < 5; ++i) {
                Future<?> submit = executor.submit(new SleepTask());
                ret.add(submit);
            }

            for (Future<?> future : ret) {
                future.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static <T> void print(ConcurrentMap<String, MtContextThreadLocal<T>> mtContexts) {
        for (Map.Entry<String, MtContextThreadLocal<T>> entry : mtContexts.entrySet()) {
            String key = entry.getKey();
            T value = entry.getValue().get();
            System.out.printf("Key %s, value: %s\n", key, value);
        }
    }

    public static <T> Map<String, Object> copied(ConcurrentMap<String, MtContextThreadLocal<T>> mtContexts) {
        Map<String, Object> copiedContent = new HashMap<String, Object>();
        for (Map.Entry<String, MtContextThreadLocal<T>> entry : mtContexts.entrySet()) {
            String key = entry.getKey();
            T value = entry.getValue().get();
            // store value in task
            if (null != value) {
                copiedContent.put(key, value);
            }
        }
        return copiedContent;
    }
}
