package com.cloud.storage.client;

import com.cloud.storage.common.CommandMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    @FXML
    Button receiveBtn;

    @FXML
    Button sendBtn;

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
    VBox remoteTableArea;

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
        localTableArea.setPrefWidth(mainArea.getWidth() * 0.45);
        remoteTableArea.setManaged(false);
        remoteTableArea.setVisible(false);
        remoteTableArea.setPrefWidth(mainArea.getWidth() * 0.45);
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

                ConnectionHandler.getInstance().sendData(new CommandMessage("/auth blabla trulala"));

                loginArea.setManaged(false);
                loginArea.setVisible(false);
                remoteTableArea.setManaged(true);
                remoteTableArea.setVisible(true);
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
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select local directory");
        File selectedDirectory = chooser.showDialog(Main.primaryStage);
        if(selectedDirectory != null) {
            updateLocalTable(selectedDirectory);
        }
    }

    public void btnChooseDirClick(ActionEvent actionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select local directory");
        File selectedDirectory = chooser.showDialog(Main.primaryStage);

        if(selectedDirectory != null) {
            updateLocalTable(selectedDirectory);
            isLocalDirChoosed = true;
            chooseLocalDirArea.setManaged(false);
            chooseLocalDirArea.setVisible(false);
            localTableArea.setManaged(true);
            localTableArea.setVisible(true);
            if (isConnected) {
                transferBtnArea.setVisible(true);
                transferBtnArea.setManaged(true);
            }
        }
    }

    public void btnLogoutClick(ActionEvent actionEvent) {
        ConnectionHandler.getInstance().close();
        writeToLogArea("initiating logout");
        writeToLogArea("connection closed by user");
        isConnected = false;
        remoteTableArea.setManaged(false);
        remoteTableArea.setVisible(false);
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
        tcName.maxWidthProperty().bind(localTable.widthProperty().multiply(0.65));
        tcName.prefWidthProperty().bind(localTable.widthProperty().multiply(0.65));
        tcName.setResizable(false);

        TableColumn<FileItem, Long> tcSize = new TableColumn<>("Size (KB)");
        tcSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        tcSize.maxWidthProperty().bind(localTable.widthProperty().multiply(0.35));
        tcSize.prefWidthProperty().bind(localTable.widthProperty().multiply(0.35));
        tcSize.setResizable(false);


        localTable.getColumns().addAll(tcName, tcSize);
    }

    public void initializeRemoteFilesTable() {
        TableColumn<FileItem, String> tcName = new TableColumn<>("Name");
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcName.prefWidthProperty().bind(remoteTable.widthProperty().multiply(0.65));
        tcName.maxWidthProperty().bind(remoteTable.widthProperty().multiply(0.65));
        tcName.setResizable(false);

        TableColumn<FileItem, Long> tcSize = new TableColumn<>("Size (KB)");
        tcSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        tcSize.prefWidthProperty().bind(remoteTable.widthProperty().multiply(0.35));
        tcSize.maxWidthProperty().bind(remoteTable.widthProperty().multiply(0.35));
        tcSize.setResizable(false);

        remoteTable.getColumns().addAll(tcName, tcSize);
    }

    public void updateLocalTable(File folder) {
        localTable.getItems().removeAll();
        File[] files = folder.listFiles(pathname -> pathname.isFile());
        localFileList = FXCollections.observableArrayList();

        for(int i = 0; i < files.length; i++) {
            localFileList.add(new FileItem(files[i].getName(), files[i].length() / 1024, files[i].getAbsolutePath()));
        }

        localTable.setItems(localFileList);
    }

    public void sendFile(File file) {
        writeToLogArea("demo sending file:  " + file.getAbsolutePath());
        try {
            ConnectionHandler.getInstance().sendData(new CommandMessage("demo sending file:  " + file.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnSendClick(ActionEvent actionEvent) {
        FileItem fileItem = (FileItem) localTable.getSelectionModel().getSelectedItem();
        sendFile(new File(fileItem.getPath()));
    }

    public void btnReceiveClick(ActionEvent actionEvent) {
    }
}
