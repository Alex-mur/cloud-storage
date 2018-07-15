package com.cloud.storage.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import javax.swing.text.html.ListView;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

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


    public void initialize(URL url, ResourceBundle rb) {
        localListArea.setManaged(false);
        localListArea.setVisible(false);
        remoteListArea.setManaged(false);
        remoteListArea.setVisible(false);
        transferBtnArea.setVisible(false);
        transferBtnArea.setManaged(false);

    }

    public void btnConnectClick() {
        loginArea.setManaged(false);
        loginArea.setVisible(false);
        remoteListArea.setManaged(true);
        remoteListArea.setVisible(true);
        transferBtnArea.setVisible(true);
        transferBtnArea.setManaged(true);
    }

    public void btnChangeDirClick(ActionEvent actionEvent) {
    }

    public void btnChooseDirClick(ActionEvent actionEvent) {
    }
}
