package com.epam.esm.concurrency;

import java.util.concurrent.TimeUnit;

public interface CountingScheduledExecutorService {

    Long getNumberOfExecutions();

    void scheduleAtFixedRateCounting(Runnable command,
                                     long initialDelay,
                                     long period,
                                     TimeUnit unit);

    void shutdown();
}
