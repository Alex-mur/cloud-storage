package com.cloud.storage.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

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
        loginArea.setManaged(false);
        loginArea.setVisible(false);
        remoteListArea.setManaged(true);
        remoteListArea.setVisible(true);
        isConnected = true;
        if (isLocalDirChoosed) {
            transferBtnArea.setVisible(true);
            transferBtnArea.setManaged(true);
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
        isConnected = false;
        remoteListArea.setManaged(false);
        remoteListArea.setVisible(false);
        transferBtnArea.setVisible(false);
        transferBtnArea.setManaged(false);
        loginArea.setManaged(true);
        loginArea.setVisible(true);
    }
}
