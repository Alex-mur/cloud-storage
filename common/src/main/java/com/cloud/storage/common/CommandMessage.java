package com.cloud.storage.common;

import java.io.Serializable;

public class CommandMessage implements Serializable {

    private String command;

    public CommandMessage(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
