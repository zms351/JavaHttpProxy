package com.meituan.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Executors {

    private static Executors instance;

    public static Executors getInstance() {
        if(instance==null) {
            synchronized(Executors.class) {
                if(instance==null) {
                    instance=new Executors();
                }
            }
        }
        return instance;
    }

    private ExecutorService executor;

    private Executors() {
    }

    protected synchronized ExecutorService getExecutor() {
        if(executor==null) {
            final ThreadFactory parent= java.util.concurrent.Executors.defaultThreadFactory();
            ThreadFactory factory=new ThreadFactory() {
                @Override
                public Thread newThread(Runnable runnable) {
                    Thread thread=parent.newThread(runnable);
                    thread.setDaemon(true);
                    return thread;
                }
            };
            executor= java.util.concurrent.Executors.newCachedThreadPool(factory);
        }
        return executor;
    }

    public void shutdown() {
        if(executor!=null) {
            executor.shutdown();
        }
    }

    public <T> Future<T> submitCommon(Callable<T> call) {
        return getExecutor().submit(call);
    }

}
