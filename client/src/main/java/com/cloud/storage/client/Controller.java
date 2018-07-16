package com.cloud.storage.client;

import com.cloud.storage.common.CommandMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    TableView remoteTable;

    @FXML
    TableView localTable;

    @FXML
    ListView logAreaList;

    @FXML
    Button btnLogout;

    @FXML
    HBox chooseLocalDirArea;

    @FXML
    VBox mainArea;

    @FXML
    VBox loginArea;

    @FXML
    VBox remoteListArea;

    @FXML
    VBox transferBtnArea;

    @FXML
    VBox localTableArea;

    @FXML
    ProgressBar pBar;

    @FXML
    VBox logArea;


    private boolean isConnected;
    private boolean isLocalDirChoosed;
    ObservableList<FileItem> localFileList;

    public void initialize(URL url, ResourceBundle rb) {
        localTableArea.setManaged(false);
        localTableArea.setVisible(false);
        remoteListArea.setManaged(false);
        remoteListArea.setVisible(false);
        transferBtnArea.setVisible(false);
        transferBtnArea.setManaged(false);
        isConnected = false;
        isLocalDirChoosed =false;
        initializeLocalFilesTable();
        pBar.prefWidthProperty().bind(logArea.widthProperty());
    }

    public void btnConnectClick() {
        try {
            ConnectionHandler.getInstance().connect();
            if (ConnectionHandler.getInstance().isConnected()) {

                ConnectionHandler.getInstance().sendData(new CommandMessage("Client trying to connect"));

                loginArea.setManaged(false);
                loginArea.setVisible(false);
                remoteListArea.setManaged(true);
                remoteListArea.setVisible(true);
                isConnected = true;
                if (isLocalDirChoosed) {
                    transferBtnArea.setVisible(true);
                    transferBtnArea.setManaged(true);
                }
                writeToLogArea("connected to server");
            }
        } catch (IOException e) {
            e.printStackTrace();
            writeToLogArea(e.getMessage());
        }
    }

    public void btnChangeDirClick(ActionEvent actionEvent) {
    }

    public void btnChooseDirClick(ActionEvent actionEvent) {
        isLocalDirChoosed = true;
        chooseLocalDirArea.setManaged(false);
        chooseLocalDirArea.setVisible(false);
        localTableArea.setManaged(true);
        localTableArea.setVisible(true);
        updateLocalTable(new File("F:\\"));
        if (isConnected) {
            transferBtnArea.setVisible(true);
            transferBtnArea.setManaged(true);
        }
    }

    public void btnLogoutClick(ActionEvent actionEvent) {
        ConnectionHandler.getInstance().close();
        writeToLogArea("initiating logout");
        writeToLogArea("connection closed by user");
        isConnected = false;
        remoteListArea.setManaged(false);
        remoteListArea.setVisible(false);
        transferBtnArea.setVisible(false);
        transferBtnArea.setManaged(false);
        loginArea.setManaged(true);
        loginArea.setVisible(true);
    }

    public void writeToLogArea(String text) {
        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy-HH:mm:ss");
        logAreaList.getItems().add(format.format(Calendar.getInstance().getTime()) + " -> " + text);
        logAreaList.scrollTo(logAreaList.getItems().size() - 1);
    }

    public void initializeLocalFilesTable() {
        TableColumn<FileItem, String> tcName = new TableColumn<>("Name");
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcName.prefWidthProperty().bind(localTable.widthProperty().multiply(0.65));

        TableColumn<FileItem, Long> tcSize = new TableColumn<>("Size (KB)");
        tcSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        tcSize.prefWidthProperty().bind(localTable.widthProperty().multiply(0.35));

        localTable.getColumns().addAll(tcName, tcSize);
    }

    public void initializeRemoteFilesTable() {
        TableColumn<FileItem, String> tcName = new TableColumn<>("Name");
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcName.prefWidthProperty().bind(remoteTable.widthProperty().multiply(0.65));

        TableColumn<FileItem, Long> tcSize = new TableColumn<>("Size (KB)");
        tcSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        tcSize.prefWidthProperty().bind(remoteTable.widthProperty().multiply(0.35));

        remoteTable.getColumns().addAll(tcName, tcSize);
    }

    public void updateLocalTable(File folder) {
        File[] files = folder.listFiles(pathname -> pathname.isFile());
        localFileList = FXCollections.observableArrayList();

        for(int i = 0; i < files.length; i++) {
            localFileList.add(new FileItem(files[i].getName(), files[i].length() / 1024, files[i].getAbsolutePath()));
        }

        localTable.setItems(localFileList);
    }

    public void sendFile(File file) {

    }

}
