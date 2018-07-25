package com.cloud.storage.client;

public class FileListItem {
    private String name;
    private Long size;
    private String path;

    public FileListItem(String name, long size, String path) {
        this.name = name;
        this.size = size;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public String getPath() {
        return path;
    }
}
