package com.epam.esm.fio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {

    private FileUtil() {
    }

    public static void createDirectoryIfNotExists(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() && file.isDirectory()) {
            createDirectory(filePath);
        }
    }

    public static void createDirectory(String dirPath) throws IOException {
        Files.createDirectory(Paths.get(dirPath));
    }
}
