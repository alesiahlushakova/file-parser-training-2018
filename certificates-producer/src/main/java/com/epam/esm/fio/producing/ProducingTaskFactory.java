package com.epam.esm.fio.producing;

import com.epam.esm.config.ProducerProps;
import com.epam.esm.generator.CertificateListGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class ProducingTaskFactory {
    private final ProducerProps producerProps;
    private final ObjectMapper objectMapper;
    private final CertificateListGenerator certificateListGenerator;

    @Autowired
    public ProducingTaskFactory(ProducerProps producerProps, ObjectMapper objectMapper,
                                CertificateListGenerator certificateListGenerator) {
        this.producerProps = producerProps;
        this.objectMapper = objectMapper;
        this.certificateListGenerator = certificateListGenerator;
    }


    public ProducingTask createProducerTask(Path dir) {
        return new ProducingTask(dir, producerProps, objectMapper, certificateListGenerator);
    }
}
