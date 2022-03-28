package com.epam.esm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "parser")
public class ParserProps {
    private String rootFolderPath;
    private String errorFolderPath;
    private Integer threadCount;
    private Float scanDelay;
    private String tmpFlag;
    private String internalAdminName;

    public String getTmpFlag() {
        return tmpFlag;
    }

    public void setTmpFlag(String tmpFlag) {
        this.tmpFlag = tmpFlag;
    }

    public String getInternalAdminName() {
        return internalAdminName;
    }

    public void setInternalAdminName(String internalAdminName) {
        this.internalAdminName = internalAdminName;
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

    public Integer getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }

    public Float getScanDelay() {
        return scanDelay;
    }

    public void setScanDelay(Float scanDelay) {
        this.scanDelay = scanDelay;
    }
}
