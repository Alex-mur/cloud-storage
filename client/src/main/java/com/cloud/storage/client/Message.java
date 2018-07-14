package com.cloud.storage.client;

import java.io.Serializable;

public class Message implements Serializable {

    private enum Type {
        COMMAND, DATA;
    }

    private Type type;
    private String command;
    private Byte data;

    public Message(String command) {
        this.type = Type.COMMAND;
        this.command = command;
    }

    public Message(Byte data) {
        this.type = Type.DATA;
        this.data = data;
    }
}
