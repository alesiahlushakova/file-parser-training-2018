package com.epam.esm.fio.producing;

import com.epam.esm.config.ProducerProps;
import com.epam.esm.generator.CertificateListGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

class ProducingTask extends RecursiveAction {
    private static final long serialVersionUID = -8112069695351187526L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducingTask.class);
    private final transient Path dir;
    private final transient ProducerProps producerProps;
    private final transient CertificateListGenerator certificateListGenerator;
    private final ObjectMapper objectMapper;

    public ProducingTask(Path dir, ProducerProps producerProps,
                         ObjectMapper objectMapper, CertificateListGenerator certificateListGenerator) {
        this.dir = dir;
        this.producerProps = producerProps;
        this.objectMapper = objectMapper;
        this.certificateListGenerator = certificateListGenerator;
    }


    @Override
    protected void compute() {
        final List<ProducingTask> tasks = new ArrayList<>();
        try {
            Files.walkFileTree(dir,
                    new ProducingTaskFileVisitor(tasks, dir, producerProps, objectMapper, certificateListGenerator));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        for (ProducingTask t : tasks) {
            t.join();
        }
    }
}
