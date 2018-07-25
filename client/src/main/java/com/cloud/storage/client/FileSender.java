package com.cloud.storage.client;

import com.cloud.storage.common.FileMessage;

import java.io.File;
import java.io.FileInputStream;

public class FileSender {
    private static final int FILE_PART_SIZE = 500000;
    private File file;
    private long fileLength;
    private long currentBytePosition;

    public FileSender(File file) {
        this.file = file;
        this.fileLength = file.length();
        this.currentBytePosition = 0;
    }

    public void sendFile() throws Exception {
        FileInputStream fis = new FileInputStream(file);
        byte[] filePart = new byte[FILE_PART_SIZE];
        while (fis.read(filePart) != -1) {
            ConnectionHandler.getInstance().sendData(new FileMessage(file.getName(), currentBytePosition, filePart));
            currentBytePosition += FILE_PART_SIZE;
            filePart = new byte[FILE_PART_SIZE];
        }
        fis.close();
    }
}
