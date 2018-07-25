package com.cloud.storage.common;

import java.io.File;
import java.io.Serializable;

public class CommandMessage implements Serializable {

    public static final String AUTH_REQUEST = "34456325432875";
    public static final String AUTH_CONFIRM = "53456546754769";
    public static final String AUTH_DECLINE = "13421543364567";
    public static final String REGISTER_NEW_USER = "653256898245";
    public static final String REGISTER_RESULT = "653256898245";
    public static final String DISCONNECT = "42391363456346";
    public static final String GET_FILE_LIST = "584772838686292475";

    private String command;
    private String text;
    private String login;
    private String password;
    private File[] fileList;

    public CommandMessage(String command, File[] fileList) {
        this.command = command;
        this.fileList = fileList;
    }

    public CommandMessage(String command, String text) {
        this.command = command;
        this.text = text;
    }

    public CommandMessage(String command) {
        this.command = command;
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

    public String getText() {
        return text;
    }

    public File[] getFileList() {
        return fileList;
    }
}
