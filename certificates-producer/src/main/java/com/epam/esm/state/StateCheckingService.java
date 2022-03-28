package com.epam.esm.state;

import com.epam.esm.CertificateService;
import com.epam.esm.concurrency.CountingScheduledExecutorService;
import com.epam.esm.config.ProducerProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Service
public class StateCheckingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StateCheckingService.class);
    private final CertificateService certificateService;
    private final ProcessState initialState;
    private final ProducerProps producerProps;
    private final CountingScheduledExecutorService countingScheduledExecutorService;

    @Autowired
    public StateCheckingService(CertificateService certificateService,
                                ProcessState initialState, ProducerProps producerProps,
                                CountingScheduledExecutorService countingScheduledExecutorService) {
        this.certificateService = certificateService;
        this.initialState = initialState;
        this.producerProps = producerProps;
        this.countingScheduledExecutorService = countingScheduledExecutorService;
    }


    public boolean isProcessedWell() {
        try (Stream<Path> list = Files.list(Path.of(producerProps.getErrorFolderPath()))) {
            long certificatesInDB = certificateService.count();
            long certificatesInErrorFolder = list.count();
            long addedToDB = certificatesInDB - initialState.getCertificatesInDB();
            long addedToErrorFolder = certificatesInErrorFolder - initialState.getCertificatesInErrorFolder();
            long generated =
                    countingScheduledExecutorService.getNumberOfExecutions() * getNumberOfFilesPerExecution();
            LOGGER.info("Number of generated files: {}", generated);
            LOGGER.info("Added to DB: {}", addedToDB);
            LOGGER.info("Added to error folder: {}", addedToErrorFolder);
            return generated == addedToDB + addedToErrorFolder;
        } catch (IOException e) {
            return false;
        }
    }

    private long getNumberOfFilesPerExecution() {
        return producerProps.getNumberOfFolders() * producerProps.getFilesCount();
    }

}
