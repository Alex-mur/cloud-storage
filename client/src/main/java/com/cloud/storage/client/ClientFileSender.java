package com.cloud.storage.client;

import com.cloud.storage.common.FileMessage;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

public class ClientFileSender {
    private static final int FILE_PART_SIZE = 500000;
    private File file;
    private long fileLength;
    private long currentBytePosition;

    public ClientFileSender(File file) {
        this.file = file;
        this.fileLength = file.length();
        this.currentBytePosition = 0;
    }

    public void sendFile() {
        new Thread(() -> {
            try {
                FileInputStream fis = new FileInputStream(file);
                byte[] filePart = new byte[FILE_PART_SIZE];
                int len;
                while ((len = fis.read(filePart)) != -1) {
                    if (len < filePart.length) {
                        byte[] out = Arrays.copyOf(filePart, len);
                        ConnectionHandler.getInstance().sendData(new FileMessage(file.getName(), currentBytePosition, out));
                    } else {
                        ConnectionHandler.getInstance().sendData(new FileMessage(file.getName(), currentBytePosition, filePart));
                        currentBytePosition += FILE_PART_SIZE;
                        filePart = new byte[FILE_PART_SIZE];
                    }
                }
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
