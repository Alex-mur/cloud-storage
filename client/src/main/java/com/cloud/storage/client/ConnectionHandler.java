package com.cloud.storage.client;

public class ConnectionHandler {
    private static ConnectionHandler ourInstance = new ConnectionHandler();

    public static ConnectionHandler getInstance() {
        return ourInstance;
    }

    private ConnectionHandler() {
    }
}
