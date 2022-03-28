package com.epam.esm.concurrency.impl;

import com.epam.esm.concurrency.CountingScheduledExecutorService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class CountingScheduledExecutorServiceImpl implements CountingScheduledExecutorService {
    private final AtomicLong count;
    private final ScheduledExecutorService scheduledExecutorService;

    public CountingScheduledExecutorServiceImpl(int corePoolSize) {
        this.count = new AtomicLong();
        this.scheduledExecutorService = Executors.newScheduledThreadPool(corePoolSize);
    }

    @Override
    public Long getNumberOfExecutions() {
        return count.get();
    }

    @Override
    public void scheduleAtFixedRateCounting(Runnable command, long initialDelay,
                                            long period, TimeUnit unit) {
        scheduledExecutorService.scheduleAtFixedRate(wrapWithCountIncrease(command),
                initialDelay, period, unit);
    }

    @Override
    public void shutdown() {
        scheduledExecutorService.shutdown();
    }

    private Runnable wrapWithCountIncrease(Runnable command) {
        return () -> {
            command.run();
            count.incrementAndGet();
        };
    }
}
