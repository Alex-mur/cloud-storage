package com.cloud.storage.server;


import com.cloud.storage.common.FileMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FileReceiver {
    private String finalFileName;
    private long finalFileLength;
    private File tempFile;
    private ArrayList<FileMessage> msgBuffer;
    private FileOutputStream fos;

    public String getFinalFileName() {
        return finalFileName;
    }

    public long getFinalFileLength() {
        return finalFileLength;
    }

    public File getTempFile() {
        return tempFile;
    }

    public FileReceiver(String path, String name, long finalFileLength) throws IOException {
        tempFile = new File(path + "\\" + name + ".partial");
        tempFile.createNewFile();
        this.finalFileName = name;
        this.finalFileLength = finalFileLength;
        this.msgBuffer = new ArrayList<>();
        this.fos = new FileOutputStream(tempFile);
        System.out.println("file receiver created");
    }

    public boolean receiveFile(FileMessage msg) throws IOException {
        System.out.println("receive file method started");
        if (tempFile.length() == msg.getStartByte()) {
            fos.write(msg.getData());
        } else if (msg.getStartByte() > tempFile.length()) {
            msgBuffer.add(msg);
        }

        if (msgBuffer.size() > 0) {
            for(FileMessage bufMsg : msgBuffer) {
                if (tempFile.length() == bufMsg.getStartByte()) {
                    fos.write(bufMsg.getData());
                }
            }
        }

        if (tempFile.length() >= finalFileLength) {
            fos.close();
            tempFile.renameTo(new File(tempFile.getAbsolutePath().replace(".partial", "")));
            return true;
        }
        return false;
    }
}
