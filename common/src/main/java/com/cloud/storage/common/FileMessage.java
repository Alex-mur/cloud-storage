package com.cloud.storage.common;

import java.io.Serializable;

public class FileMessage implements Serializable {
    String name;
    long size;
    long startByte;
    long endByte;
    byte[] data;

    public FileMessage(String name, long size, long startByte, long endByte, byte[] data) {
        this.name = name;
        this.size = size;
        this.startByte = startByte;
        this.endByte = endByte;
        this.data = data;
    }
}
