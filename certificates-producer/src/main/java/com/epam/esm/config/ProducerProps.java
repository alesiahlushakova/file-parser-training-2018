package com.epam.esm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@ConfigurationProperties(prefix = "producer")
public class ProducerProps {
    private Integer testTime;
    private Long filesCount;
    private Float periodTime;
    private Integer subfoldersCount;
    private String rootFolderPath;
    private String errorFolderPath;
    private Long validFilesCount;
    private Long wrongJsonFormattedFilesCount;
    private Long wrongFieldNameFilesCount;
    private Long nonValidBeansFilesCount;
    private Integer numberOfFolders;
    private Integer certificatesPerFile;
    private String certificateDescription;
    private Integer certificateDuration;
    private BigDecimal certificatePrice;
    private Integer invalidCertificateDuration;
    private String invalidJson;
    private String priceFieldName;
    private String wrongFieldName;
    private String tmpFlag;
    private Integer schedulerPoolSize;
    private Long initialStateCheckingDelay;
    private Long stateCheckingDelay;


    @PostConstruct
    private void init() {
        if (filesCount % 20 != 0) {
            throw new IllegalArgumentException("According to the task FILES_COUNT should be multiple of 20");
        }
        validFilesCount = filesCount * 16 / 20;
        wrongJsonFormattedFilesCount = wrongFieldNameFilesCount = filesCount / 20;
        nonValidBeansFilesCount = filesCount / 10;
    }

    public Integer getTestTime() {
        return testTime;
    }

    public void setTestTime(Integer testTime) {
        this.testTime = testTime;
    }

    public Long getFilesCount() {
        return filesCount;
    }

    public void setFilesCount(Long filesCount) {
        this.filesCount = filesCount;
    }

    public Float getPeriodTime() {
        return periodTime;
    }

    public void setPeriodTime(Float periodTime) {
        this.periodTime = periodTime;
    }

    public Long getValidFilesCount() {
        return validFilesCount;
    }

    public void setValidFilesCount(Long validFilesCount) {
        this.validFilesCount = validFilesCount;
    }

    public Long getWrongJsonFormattedFilesCount() {
        return wrongJsonFormattedFilesCount;
    }

    public void setWrongJsonFormattedFilesCount(Long wrongJsonFormattedFilesCount) {
        this.wrongJsonFormattedFilesCount = wrongJsonFormattedFilesCount;
    }

    public Long getWrongFieldNameFilesCount() {
        return wrongFieldNameFilesCount;
    }

    public void setWrongFieldNameFilesCount(Long wrongFieldNameFilesCount) {
        this.wrongFieldNameFilesCount = wrongFieldNameFilesCount;
    }

    public Long getNonValidBeansFilesCount() {
        return nonValidBeansFilesCount;
    }

    public void setNonValidBeansFilesCount(Long nonValidBeansFilesCount) {
        this.nonValidBeansFilesCount = nonValidBeansFilesCount;
    }

    public Integer getSubfoldersCount() {
        return subfoldersCount;
    }

    public void setSubfoldersCount(Integer subfoldersCount) {
        this.subfoldersCount = subfoldersCount;
    }

    public String getRootFolderPath() {
        return rootFolderPath;
    }

    public void setRootFolderPath(String rootFolderPath) {
        this.rootFolderPath = rootFolderPath;
    }

    public String getErrorFolderPath() {
        return errorFolderPath;
    }

    public void setErrorFolderPath(String errorFolderPath) {
        this.errorFolderPath = errorFolderPath;
    }

    public Integer getNumberOfFolders() {
        return numberOfFolders;
    }

    public void setNumberOfFolders(Integer numberOfFolders) {
        this.numberOfFolders = numberOfFolders;
    }

    public Integer getCertificatesPerFile() {
        return certificatesPerFile;
    }

    public void setCertificatesPerFile(Integer certificatesPerFile) {
        this.certificatesPerFile = certificatesPerFile;
    }

    public String getCertificateDescription() {
        return certificateDescription;
    }

    public void setCertificateDescription(String certificateDescription) {
        this.certificateDescription = certificateDescription;
    }

    public Integer getCertificateDuration() {
        return certificateDuration;
    }

    public void setCertificateDuration(Integer certificateDuration) {
        this.certificateDuration = certificateDuration;
    }

    public BigDecimal getCertificatePrice() {
        return certificatePrice;
    }

    public void setCertificatePrice(BigDecimal certificatePrice) {
        this.certificatePrice = certificatePrice;
    }

    public Integer getInvalidCertificateDuration() {
        return invalidCertificateDuration;
    }

    public void setInvalidCertificateDuration(Integer invalidCertificateDuration) {
        this.invalidCertificateDuration = invalidCertificateDuration;
    }

    public String getInvalidJson() {
        return invalidJson;
    }

    public void setInvalidJson(String invalidJson) {
        this.invalidJson = invalidJson;
    }

    public String getPriceFieldName() {
        return priceFieldName;
    }

    public void setPriceFieldName(String priceFieldName) {
        this.priceFieldName = priceFieldName;
    }

    public String getWrongFieldName() {
        return wrongFieldName;
    }

    public void setWrongFieldName(String wrongFieldName) {
        this.wrongFieldName = wrongFieldName;
    }

    public String getTmpFlag() {
        return tmpFlag;
    }

    public void setTmpFlag(String tmpFlag) {
        this.tmpFlag = tmpFlag;
    }

    public Integer getSchedulerPoolSize() {
        return schedulerPoolSize;
    }

    public void setSchedulerPoolSize(Integer schedulerPoolSize) {
        this.schedulerPoolSize = schedulerPoolSize;
    }

    public Long getInitialStateCheckingDelay() {
        return initialStateCheckingDelay;
    }

    public void setInitialStateCheckingDelay(Long initialStateCheckingDelay) {
        this.initialStateCheckingDelay = initialStateCheckingDelay;
    }

    public Long getStateCheckingDelay() {
        return stateCheckingDelay;
    }

    public void setStateCheckingDelay(Long stateCheckingDelay) {
        this.stateCheckingDelay = stateCheckingDelay;
    }
}
