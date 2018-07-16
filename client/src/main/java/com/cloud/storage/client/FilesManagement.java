package com.cloud.storage.client;

import java.io.File;

public class FilesManagement {

    public static void listLocalFolder(File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                fileEntry.getAbsolutePath();
            }
        }
    }
}
