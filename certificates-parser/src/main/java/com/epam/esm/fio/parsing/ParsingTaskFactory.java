package com.epam.esm.fio.parsing;

import com.epam.esm.CertificateService;
import com.epam.esm.config.ParserProps;
import com.epam.esm.parser.CertificatesListJsonFileParser;
import com.epam.esm.validator.CertificateDtoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class ParsingTaskFactory {
    private final ParserProps parserProps;
    private final CertificateService certificateService;
    private final CertificatesListJsonFileParser parser;
    private final CertificateDtoValidator certificateDtoValidator;

    @Autowired
    public ParsingTaskFactory(ParserProps parserProps, CertificateService certificateService,
                              CertificatesListJsonFileParser parser,
                              CertificateDtoValidator certificateDtoValidator) {
        this.parserProps = parserProps;
        this.certificateService = certificateService;
        this.parser = parser;

        this.certificateDtoValidator = certificateDtoValidator;
    }

    public ParsingTask createParsingTask(Path dir) {
        return new ParsingTask(dir, parserProps, certificateService, parser, certificateDtoValidator);
    }
}
