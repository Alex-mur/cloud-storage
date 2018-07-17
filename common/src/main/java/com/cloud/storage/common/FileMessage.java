package com.cloud.storage.common;

import java.io.Serializable;

public class FileMessage implements Serializable {
    String name;
    long fileSize;
    long startByte;
    long endByte;
    byte[] data;

    public FileMessage(String name, long fileSize, long startByte, long endByte, byte[] data) {
        this.name = name;
        this.fileSize = fileSize;
        this.startByte = startByte;
        this.endByte = endByte;
        this.data = data;
    }
}
