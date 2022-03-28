package com.epam.esm.state;

public class ProcessState {
    private final Long certificatesInDB;
    private final Long certificatesInErrorFolder;

    public ProcessState(Long certificatesInDB, Long certificatesInErrorFolder) {
        this.certificatesInDB = certificatesInDB;
        this.certificatesInErrorFolder = certificatesInErrorFolder;
    }

    public Long getCertificatesInDB() {
        return certificatesInDB;
    }

    public Long getCertificatesInErrorFolder() {
        return certificatesInErrorFolder;
    }
}
