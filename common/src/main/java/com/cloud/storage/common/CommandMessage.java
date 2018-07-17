package com.cloud.storage.common;

import java.io.Serializable;

public class CommandMessage implements Serializable {

    public static final String AUTH_REQUEST = "auth";
    public static final String AUTH_CONFIRM = "auth_ok";
    public static final String AUTH_DECLINE = "auth_wrongPass";
    public static final String DISCONNECT = "disconnect";
    public static final String SEND_FILE_REQUEST = "send_file";

    private String command;
    private String login;
    private String password;
    private long fileSizeInBytes;

    public CommandMessage(String command) {
        this.command = command;
    }

    public CommandMessage(String command, long fileSizeInBytes) {
        this.command = command;
        this.fileSizeInBytes = fileSizeInBytes;
    }

    public CommandMessage(String command, String login, String password) {
        this.command = command;
        this.login = login;
        this.password = password;
    }

    public String getCommand() {
        return command;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public long getFileSizeInBytes() {
        return fileSizeInBytes;
    }
}
