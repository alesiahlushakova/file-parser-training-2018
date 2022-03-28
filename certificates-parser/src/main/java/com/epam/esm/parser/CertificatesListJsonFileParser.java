package com.epam.esm.parser;

import com.epam.esm.dto.CertificateDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class CertificatesListJsonFileParser {

    private static final TypeReference<List<CertificateDto>> TYPE_REFERENCE =
            new TypeReference<>() {
            };
    private final ObjectMapper objectMapper;


    @Autowired
    public CertificatesListJsonFileParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<CertificateDto> parse(File file) throws IOException {
        return objectMapper.readValue(file, TYPE_REFERENCE);
    }
}
