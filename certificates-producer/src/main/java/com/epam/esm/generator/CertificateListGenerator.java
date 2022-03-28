package com.epam.esm.generator;

import com.epam.esm.config.ProducerProps;
import com.epam.esm.dto.CertificateDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CertificateListGenerator {

    private final ProducerProps producerProps;

    @Autowired
    public CertificateListGenerator(ProducerProps producerProps) {
        this.producerProps = producerProps;
    }

    public List<CertificateDto> generateValidCertificateList() {
        return Stream.generate(this::generateValidCertificate)
                .limit(producerProps.getCertificatesPerFile())
                .collect(Collectors.toList());
    }

    /**
     * Skipping first element is required because, according to the task,
     * non valid bean should be in the middle of a list.
     */
    public List<CertificateDto> generateCertificateListWithNonValidBean() {
        List<CertificateDto> certificates = generateValidCertificateList();
        certificates.stream()
                .skip(1)
                .findAny()
                .get()
                .setDuration(producerProps.getInvalidCertificateDuration());
        return certificates;
    }

    private CertificateDto generateValidCertificate() {
        CertificateDto validCertificate = new CertificateDto();
        validCertificate.setName(RandomStringUtils.randomAlphanumeric(16));
        validCertificate.setDescription(producerProps.getCertificateDescription());
        validCertificate.setDuration(producerProps.getCertificateDuration());
        validCertificate.setPrice(producerProps.getCertificatePrice());
        return validCertificate;
    }


}
