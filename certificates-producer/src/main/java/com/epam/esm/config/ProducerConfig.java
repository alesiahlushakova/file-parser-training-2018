package com.epam.esm.config;

import com.epam.esm.CertificateService;
import com.epam.esm.concurrency.CountingScheduledExecutorService;
import com.epam.esm.concurrency.impl.CountingScheduledExecutorServiceImpl;
import com.epam.esm.state.ProcessState;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Configuration
@EnableConfigurationProperties(ProducerProps.class)
public class ProducerConfig {

    @Bean
    public CountingScheduledExecutorService countingScheduledExecutorService(ProducerProps producerProps) {
        return new CountingScheduledExecutorServiceImpl(producerProps.getSchedulerPoolSize());
    }

    @Bean
    public ProcessState initialState(CertificateService certificateService,
                                     ProducerProps producerProps) throws IOException {
        long certificatesInDB = certificateService.count();
        Path errFolderPath = Path.of(producerProps.getErrorFolderPath());
        long certificatesInErrorFolder = 0L;
        if (Files.exists(errFolderPath)) {
            try (Stream<Path> list = Files.list(errFolderPath)) {
                certificatesInErrorFolder = list.count();
            }
        }
        return new ProcessState(certificatesInDB, certificatesInErrorFolder);
    }
}
