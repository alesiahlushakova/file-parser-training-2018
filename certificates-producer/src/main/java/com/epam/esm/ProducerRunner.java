package com.epam.esm;

import com.epam.esm.concurrency.CountingScheduledExecutorService;
import com.epam.esm.config.ProducerProps;
import com.epam.esm.fio.FoldersCreator;
import com.epam.esm.fio.producing.ProducingTaskFactory;
import com.epam.esm.state.StateCheckingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ProducerRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerRunner.class);
    private final ProducerProps props;
    private final FoldersCreator foldersCreator;
    private final ProducingTaskFactory producingTaskFactory;
    private final CountingScheduledExecutorService countingScheduledExecutorService;
    private final StateCheckingService stateCheckingService;

    @Autowired
    public ProducerRunner(ProducerProps props, FoldersCreator foldersCreator,
                          ProducingTaskFactory producingTaskFactory,
                          CountingScheduledExecutorService countingScheduledExecutorService,
                          StateCheckingService stateCheckingService) {
        this.props = props;
        this.foldersCreator = foldersCreator;
        this.producingTaskFactory = producingTaskFactory;
        this.countingScheduledExecutorService = countingScheduledExecutorService;
        this.stateCheckingService = stateCheckingService;
    }

    @Override
    public void run(String... args) throws IOException, InterruptedException {
        foldersCreator.createFolders();
        Integer testTime = props.getTestTime();
        Float periodTime = props.getPeriodTime();
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        LOGGER.info("START PRODUCING");
        countingScheduledExecutorService.scheduleAtFixedRateCounting(
                () -> forkJoinPool.invoke(
                        producingTaskFactory.createProducerTask(Path.of(props.getRootFolderPath()))
                ),
                0, (long) (periodTime * 1000), TimeUnit.MILLISECONDS);
        ScheduledExecutorService managerExecutor = Executors.newSingleThreadScheduledExecutor();
        managerExecutor.schedule(() -> {
                    LOGGER.info("SHUTDOWN");
                    countingScheduledExecutorService.shutdown();
                    forkJoinPool.shutdown();
                },
                testTime.longValue(), TimeUnit.SECONDS);
        forkJoinPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        checkState(managerExecutor);
    }

    private void checkState(ScheduledExecutorService scheduledExecutorService) {
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (areAllFoldersEmpty()) {
                LOGGER.info("CHECK IF ALL FILES WHERE PROCESSED WELL");
                boolean processedWell = stateCheckingService.isProcessedWell();
                assert (processedWell);
                LOGGER.info("SUCCESS");
                scheduledExecutorService.shutdown();
            }
        }, props.getInitialStateCheckingDelay(), props.getStateCheckingDelay(), TimeUnit.SECONDS);

    }

    private boolean areAllFoldersEmpty() {
        try (var files = Files.walk(Path.of(props.getRootFolderPath()))) {
            return files.map(Path::toFile)
                    .filter(File::isFile)
                    .findAny()
                    .isEmpty();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return false;
        }
    }
}
