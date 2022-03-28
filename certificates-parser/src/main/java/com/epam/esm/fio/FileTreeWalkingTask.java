package com.epam.esm.fio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileTreeWalkingTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileTreeWalkingTask.class);
    private final Path rootFolderPath;
    private final ExecutorService executorService;
    private final Function<Path, Callable<Long>> dirProcessingTasksProvider;


    /**
     * Creates Creates a new FileTreeWalkingTask with the given parameters.
     *
     * @param rootFolderPath             root folder's path
     * @param numberOfThreads            number of threads in thread pool dirProcessingTasks should be executed in.
     * @param dirProcessingTasksProvider provides tasks which should be executed for each
     *                                   subdirectory including the root one. Number of successfully processed files
     *                                   expected to be returned by these tasks
     */
    public FileTreeWalkingTask(Path rootFolderPath, int numberOfThreads,
                               Function<Path, Callable<Long>> dirProcessingTasksProvider) {
        this.rootFolderPath = rootFolderPath;
        this.executorService = Executors.newFixedThreadPool(numberOfThreads);
        this.dirProcessingTasksProvider = dirProcessingTasksProvider;
    }

    /**
     * Walks the file tree rooted at the path provided to the constructor
     * and executes {@link Callable} task produced by function provided to the constructor
     * for each subdirectory including the root one in the thread pool that reuses a fixed number
     * of threads provided to the constructor.
     *
     * @see FileTreeWalkingTask#FileTreeWalkingTask(java.nio.file.Path, int, java.util.function.Function)
     */
    @Override
    public void run() {
        try {
            if (Files.exists(rootFolderPath)) {
                try (var dirs = Files.walk(rootFolderPath)) {
                    List<Callable<Long>> dirProcessingTasks = dirs
                            .filter(Files::isDirectory)
                            .map(dirProcessingTasksProvider)
                            .collect(Collectors.toList());
                    LOGGER.info("Dirs to be processed: {}", dirProcessingTasks.size());
                    List<Future<Long>> futures = executorService.invokeAll(dirProcessingTasks);
                    long successfullyProcessed = futures.stream()
                            .mapToLong(this::getFromFutureWrappingException)
                            .sum();
                    LOGGER.info("Number of successfully processed files: {}", successfullyProcessed);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred while walking the file tree", e);
            throw new RuntimeException(e);
        }
    }

    private Long getFromFutureWrappingException(Future<Long> longFuture) {
        try {
            return longFuture.get();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
