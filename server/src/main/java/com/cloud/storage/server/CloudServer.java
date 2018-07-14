package com.cloud.storage.server;

public class CloudServer {

    private int port;

    public CloudServer(int port) {
        this.port = port;
    }




    public static void main(String[] args) {
        new CloudServer(SettingsMgmt.port);

    }
}
