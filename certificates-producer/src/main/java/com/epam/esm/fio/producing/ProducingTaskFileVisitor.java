package com.epam.esm.fio.producing;

import com.epam.esm.config.ProducerProps;
import com.epam.esm.dto.CertificateDto;
import com.epam.esm.generator.CertificateListGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

class ProducingTaskFileVisitor extends SimpleFileVisitor<Path> {
    private final List<ProducingTask> tasks;
    private final Path dir;
    private final ProducerProps producerProps;
    private final ObjectMapper objectMapper;
    private final CertificateListGenerator certificateListGenerator;

    ProducingTaskFileVisitor(List<ProducingTask> tasks, Path dir, ProducerProps producerProps,
                             ObjectMapper objectMapper, CertificateListGenerator certificateListGenerator) {
        this.tasks = tasks;
        this.dir = dir;
        this.producerProps = producerProps;
        this.objectMapper = objectMapper;
        this.certificateListGenerator = certificateListGenerator;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dirPath, BasicFileAttributes attrs) throws IOException {
        produceFiles(dirPath);
        if (!dirPath.equals(dir)) {
            ProducingTask task = new ProducingTask(dirPath, producerProps, objectMapper, certificateListGenerator);
            task.fork();
            tasks.add(task);
            return FileVisitResult.SKIP_SUBTREE;
        } else {
            return FileVisitResult.CONTINUE;
        }
    }

    private void produceFiles(Path dirPath) throws IOException {
        produceValidJsonFiles(dirPath, producerProps.getValidFilesCount(),
                certificateListGenerator::generateValidCertificateList);
        produceValidJsonFiles(dirPath, producerProps.getNonValidBeansFilesCount(),
                certificateListGenerator::generateCertificateListWithNonValidBean);
        produceValidJsonFiles(dirPath, producerProps.getWrongFieldNameFilesCount(), () -> {
            List<CertificateDto> validCertificates =
                    certificateListGenerator.generateValidCertificateList();
            return createJsonContainingWrongFieldName(validCertificates);
        });
        produceFilesWithWrongJsonFormat(dirPath, producerProps.getWrongJsonFormattedFilesCount());
    }

    private void produceFilesWithWrongJsonFormat(Path dirPath,
                                                 Long wrongJsonFormattedFilesCount) throws IOException {
        for (int i = 0; i <= wrongJsonFormattedFilesCount; i++) {
            Path tmpFilePath = dirPath.resolve(generateNameWithTmpFlag());
            boolean isCreated = tmpFilePath
                    .toFile()
                    .createNewFile();
            Files.write(tmpFilePath, producerProps.getInvalidJson().getBytes());
            if (isCreated && tmpFilePath.toString().contains(producerProps.getTmpFlag())) {
                removeTmpFlag(tmpFilePath);
            }
        }
    }

    private void produceValidJsonFiles(Path dirPath, Long count, Supplier<Object> generator)
            throws IOException {
        for (int i = 0; i <= count; i++) {
            Path tmpFilePath = dirPath.resolve(generateNameWithTmpFlag());
            boolean isCreated = tmpFilePath
                    .toFile()
                    .createNewFile();
            File tmpFile = tmpFilePath.toFile();
            objectMapper.writeValue(tmpFile, generator.get());
            if (isCreated && tmpFile.toString().contains(producerProps.getTmpFlag())) {
                removeTmpFlag(tmpFilePath);
            }
        }
    }

    private JsonNode createJsonContainingWrongFieldName(List<CertificateDto> validCertificates) {
        JsonNode jsonTree = objectMapper.valueToTree(validCertificates);
        StreamSupport.stream(jsonTree.spliterator(), false).skip(1).findFirst().ifPresent(jsonNode -> {
            if (jsonNode.isNumber()) {
                ObjectNode objectNode = (ObjectNode) jsonNode;
                objectNode.remove(producerProps.getPriceFieldName());
                objectNode.put(producerProps.getWrongFieldName(), BigDecimal.ONE);
            }
        });
        return jsonTree;
    }

    private String generateNameWithTmpFlag() {
        return producerProps.getTmpFlag() + RandomStringUtils.randomAlphanumeric(8) + ".json";
    }

    private void removeTmpFlag(Path filePath) throws IOException {
        Files.move(filePath,
                filePath.getParent()
                        .resolve(filePath.getFileName().toString()
                                .replace(producerProps.getTmpFlag(), "")),
                StandardCopyOption.ATOMIC_MOVE);
    }
}