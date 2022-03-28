package com.epam.esm.validator;

import com.epam.esm.dto.CertificateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Validator;

@Component
public class CertificateDtoValidator {
    private final Validator validator;

    @Autowired
    public CertificateDtoValidator(Validator validator) {
        this.validator = validator;
    }

    public boolean validate(CertificateDto certificateDto) {
        return validator.validate(certificateDto).isEmpty();
    }
}
