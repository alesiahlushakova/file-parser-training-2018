package com.epam.esm.fio.parsing;

import com.epam.esm.CertificateService;
import com.epam.esm.config.ParserProps;
import com.epam.esm.dto.CertificateDto;
import com.epam.esm.parser.CertificatesListJsonFileParser;
import com.epam.esm.validator.CertificateDtoValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class ParsingTask implements Callable<Long> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParsingTask.class);
    private final Path dir;
    private final ParserProps parserProps;
    private final CertificateService certificateService;
    private final CertificatesListJsonFileParser parser;
    private final CertificateDtoValidator certificateDtoValidator;

    ParsingTask(Path dir, ParserProps parserProps,
                CertificateService certificateService,
                CertificatesListJsonFileParser parser, CertificateDtoValidator certificateDtoValidator) {
        this.dir = dir;
        this.parserProps = parserProps;
        this.certificateService = certificateService;
        this.parser = parser;
        this.certificateDtoValidator = certificateDtoValidator;
    }

    /**
     * Parses all files in {@link #dir}, saves valid certificates,
     * moves invalid certificates to ERROR_FOLDER, deletes successfully processed files.
     *
     * @return number of successfully processed files
     */
    @Override
    public Long call() {
        try (Stream<Path> files = Files.list(dir).filter(Files::isRegularFile)) {
            return files.filter(this::processFile)
                    .count();
        } catch (IOException e) {
            LOGGER.error("Error occurred while listing files", e);
            return 0L;
        }
    }

    private boolean processFile(Path filePath) {
        try {
            if (shouldBeProcessed(filePath)) {
                List<CertificateDto> certificates = parser.parse(filePath.toFile());
                boolean areAllValid = certificates.stream().allMatch(certificateDtoValidator::validate);
                if (areAllValid) {
                    certificates.forEach(certificateService::add);
                    return Files.deleteIfExists(filePath);
                } else {
                    moveToErrorFolder(filePath);
                }
            }
            return false;
        } catch (Exception e) {
            moveToErrorFolder(filePath);
            return false;
        }
    }

    private boolean shouldBeProcessed(Path filePath) {
        return Files.exists(filePath) && notTmpFile(filePath);
    }

    private void moveToErrorFolder(Path filePath) {
        try {
            Files.move(
                    filePath,
                    resolveFilePathInErrorFolder(filePath),
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            LOGGER.error("Error occurred while moving file into ERROR_FOLDER", e);
        }
    }

    private Path resolveFilePathInErrorFolder(Path filePath) {
        return Path.of(parserProps.getErrorFolderPath()).resolve(filePath.getFileName());
    }

    private boolean notTmpFile(Path path) {
        return !path.toString().contains(parserProps.getTmpFlag());
    }
}
