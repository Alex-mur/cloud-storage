package com.cloud.storage.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class Controller implements Initializable {

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
    VBox localListArea;

    @FXML
    ProgressBar pBar;

    @FXML
    VBox logArea;


    private boolean isConnected;
    private boolean isLocalDirChoosed;


    public void initialize(URL url, ResourceBundle rb) {
        localListArea.setManaged(false);
        localListArea.setVisible(false);
        remoteListArea.setManaged(false);
        remoteListArea.setVisible(false);
        transferBtnArea.setVisible(false);
        transferBtnArea.setManaged(false);
        isConnected = false;
        isLocalDirChoosed =false;
        pBar.prefWidthProperty().bind(logArea.widthProperty());
    }

    public void btnConnectClick() {
        try {
            ConnectionHandler.getInstance().connect();
            if (ConnectionHandler.getInstance().isConnected()) {
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
        localListArea.setManaged(true);
        localListArea.setVisible(true);
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
}
