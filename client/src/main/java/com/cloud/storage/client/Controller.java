package com.cloud.storage.client;

import com.cloud.storage.common.CommandMessage;
import com.cloud.storage.common.FileMessage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    public TextField fieldLogin;
    public PasswordField fieldPassword;
    public Button receiveBtn;
    public Button sendBtn;
    public TableView remoteTable;
    public TableView localTable;
    public ListView logAreaList;
    public Button btnLogout;
    public HBox chooseLocalDirArea;
    public VBox mainArea;
    public VBox loginArea;
    public VBox remoteTableArea;
    public VBox transferBtnArea;
    public VBox localTableArea;
    public ProgressBar pBar;
    public VBox logArea;

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
        isLocalDirChoosed = false;
        initLocalFilesTable();
        initRemoteFilesTable();
        pBar.prefWidthProperty().bind(logArea.widthProperty());
    }

    public void btnConnectClick() {
        try {
            ConnectionHandler.getInstance().connect();
            if(ConnectionHandler.getInstance().isConnected()) {
                startConnectionListener();
                ConnectionHandler.getInstance().sendData(new CommandMessage(CommandMessage.AUTH_REQUEST, fieldLogin.getText(), fieldPassword.getText()));
            }

        } catch (IOException e) {
            e.printStackTrace();
            writeToLogArea(e.getMessage());
        }

    }

    public void startConnectionListener() {
        new Thread(() -> {
            try {
                while(true) {
                    Object msg = ConnectionHandler.getInstance().readData();
                    if(msg instanceof CommandMessage) {
                        String command = ((CommandMessage) msg).getCommand();
                        Platform.runLater(() -> writeToLogArea(command));

                        if (command.equals(CommandMessage.AUTH_DECLINE)) {
                            System.out.println("bad password");
                            Platform.runLater(() ->
                                    writeToLogArea("authorization declined. wrong login or password?"));
                        }

                        if(command.equals(CommandMessage.AUTH_CONFIRM)) {
                            isConnected = true;
                            Platform.runLater(() -> {
                                writeToLogArea("connected to server OK");
                                initRemoteArea();
                            });
                            break;
                        }


                    }
                }

                while (true) {
                    Object msg = ConnectionHandler.getInstance().readData();

                    if(msg instanceof CommandMessage) {
                        String command = ((CommandMessage) msg).getCommand();

                        if(command.equals(CommandMessage.DISCONNECT)){
                            Platform.runLater(() -> writeToLogArea("received disconnect command from server"));
                            break;
                        }

                    } else if(msg instanceof FileMessage) {

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> writeToLogArea(e.getMessage()));

            } finally {
                ConnectionHandler.getInstance().close();
                Platform.runLater(() -> {
                    writeToLogArea("connection closed");
                    hideRemoteArea();
                });
            }
        }).start();
    }

    public void initRemoteArea() {
        loginArea.setManaged(false);
        loginArea.setVisible(false);
        remoteTableArea.setManaged(true);
        remoteTableArea.setVisible(true);
        fieldLogin.clear();
        fieldPassword.clear();
        if (isLocalDirChoosed) {
            transferBtnArea.setVisible(true);
            transferBtnArea.setManaged(true);
        }
    }

    public void hideRemoteArea() {
        remoteTableArea.setManaged(false);
        remoteTableArea.setVisible(false);
        transferBtnArea.setVisible(false);
        transferBtnArea.setManaged(false);
        loginArea.setManaged(true);
        loginArea.setVisible(true);
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
        isConnected = false;
        writeToLogArea("initiating logout");
        writeToLogArea("connection closed by user");
        hideRemoteArea();
    }

    public void writeToLogArea(String text) {
        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy-HH:mm:ss");
        logAreaList.getItems().add(format.format(Calendar.getInstance().getTime()) + " -> " + text);
        logAreaList.scrollTo(logAreaList.getItems().size() - 1);
    }

    public void initLocalFilesTable() {
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

    public void initRemoteFilesTable() {
        TableColumn<FileItem, String> tcName = new TableColumn<>("Name");
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcName.maxWidthProperty().bind(remoteTable.widthProperty().multiply(0.65));
        tcName.prefWidthProperty().bind(remoteTable.widthProperty().multiply(0.65));
        tcName.setResizable(false);

        TableColumn<FileItem, Long> tcSize = new TableColumn<>("Size (KB)");
        tcSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        tcSize.maxWidthProperty().bind(remoteTable.widthProperty().multiply(0.35));
        tcSize.prefWidthProperty().bind(remoteTable.widthProperty().multiply(0.35));
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
