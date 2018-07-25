package com.cloud.storage.server;

import java.io.File;

public class FilesHandler {

    public static File[] listDirectory(String path) {
        return new File(path).listFiles();
    }
}
