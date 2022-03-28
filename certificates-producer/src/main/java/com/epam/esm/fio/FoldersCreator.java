package com.epam.esm.fio;

import com.epam.esm.config.ProducerProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

import static com.epam.esm.fio.FileUtil.createDirectoryIfNotExists;


@Component
public class FoldersCreator {

    private final ProducerProps producerProps;

    @Autowired
    public FoldersCreator(ProducerProps producerProps) {
        this.producerProps = producerProps;
    }

    public void createFolders() throws IOException {
        generateSubTree(0, producerProps.getRootFolderPath());
    }

    private void generateSubTree(int currentLevel, String currentPath) throws IOException {
        int subFoldersCount = producerProps.getSubfoldersCount();
        int depth = subFoldersCount / 3;
        createDirectoryIfNotExists(currentPath);
        if (currentLevel != depth) {
            for (int i = 1; i <= depth; i++) {
                String path = String.format("%s%s%d", currentPath, File.separator, i);
                generateSubTree(currentLevel + 1, path);
            }
        }
    }
}
