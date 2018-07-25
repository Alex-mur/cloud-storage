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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    private boolean isAuthorized;
    private boolean isConnected;
    private boolean isLocalDirChoosed;
    private File currentRootDirectory;
    private ArrayList<FileSender> sendQueue;
    ObservableList<LocalFileListItem> localFileList;
    ObservableList<RemoteFileListItem> remoteFileList;
    Thread connectionListenerThread;

    public void initialize(URL url, ResourceBundle rb) {
        localTableArea.setManaged(false);
        localTableArea.setVisible(false);
        localTableArea.setPrefWidth(mainArea.getWidth() * 0.45);
        remoteTableArea.setManaged(false);
        remoteTableArea.setVisible(false);
        remoteTableArea.setPrefWidth(mainArea.getWidth() * 0.45);
        transferBtnArea.setVisible(false);
        transferBtnArea.setManaged(false);
        isAuthorized = false;
        isConnected = false;
        isLocalDirChoosed = false;
        sendQueue = new ArrayList<>();
        initLocalFilesTable();
        initRemoteFilesTable();
        pBar.prefWidthProperty().bind(logArea.widthProperty());
    }

    public void btnConnectClick() {
        try {
            if (fieldLogin.getText().length() != 0 && fieldPassword.getText().length() != 0) {
                if (!isConnected) initConnection();
                ConnectionHandler.getInstance().sendData(new CommandMessage(CommandMessage.AUTH_REQUEST, fieldLogin.getText(), fieldPassword.getText()));

            } else  writeToLogArea("Login or password cannot be empty!!!");

        } catch (IOException e) {
            e.printStackTrace();
            writeToLogArea(e.getMessage());
        }

    }

    public void btnRegisterClick() {
        try {
            if (fieldLogin.getText().length() != 0 && fieldPassword.getText().length() != 0) {
                if (!isConnected) initConnection();
                ConnectionHandler.getInstance().sendData(new CommandMessage(CommandMessage.REGISTER_NEW_USER, fieldLogin.getText(), fieldPassword.getText()));

            } else  writeToLogArea("Login or password cannot be empty!!!");

        } catch (IOException e) {
            e.printStackTrace();
            writeToLogArea(e.getMessage());
        }
    }

    public void initConnection() {
        try {
            ConnectionHandler.getInstance().connect();
            if (ConnectionHandler.getInstance().isConnected()) {
                startConnectionListener();
                isConnected = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            writeToLogArea(e.getMessage());
        }
    }

    public void closeConnection() {
        connectionListenerThread.interrupt();
        ConnectionHandler.getInstance().close();
        isConnected = false;
        isAuthorized = false;
    }

    public void startConnectionListener() {
        connectionListenerThread = new Thread(() -> {
            try {
                while(!Thread.currentThread().isInterrupted()) {
                    Object msg = ConnectionHandler.getInstance().readData();

                    if (msg instanceof CommandMessage) {
                        String command = ((CommandMessage) msg).getCommand();

                        if (command.equals(CommandMessage.AUTH_CONFIRM)) {
                            isAuthorized = true;
                            Platform.runLater(() -> {
                                writeToLogArea("Connected to server OK");
                                initRemoteArea();
                                requestRemoteFileList();
                            });
                            break;
                        }

                        if (command.equals(CommandMessage.AUTH_DECLINE)) {
                            Platform.runLater(() ->
                                    writeToLogArea("Authorization declined by server. Wrong login or password?"));
                        }

                        if (command.equals(CommandMessage.REGISTER_CONFIRM)) {
                            Platform.runLater(() ->
                                writeToLogArea(((CommandMessage) msg).getText())
                            );
                        }

                        if (command.equals(CommandMessage.REGISTER_DECLINE)) {
                            Platform.runLater(() ->
                                writeToLogArea("Registration declined. Try another user name..")
                            );
                        }
                    }
                }

                while (!Thread.currentThread().isInterrupted()) {
                    Object msg = ConnectionHandler.getInstance().readData();

                    if (msg instanceof CommandMessage) {
                        String command = ((CommandMessage) msg).getCommand();

                        if (command.equals(CommandMessage.GET_FILE_LIST)) {
                            Platform.runLater(() -> {
                                writeToLogArea("Remote filesList received");
                                updateRemoteTable(((CommandMessage) msg).getFileList());
                            });
                        }

                        if (command.equals(CommandMessage.DISCONNECT)){
                            Platform.runLater(() -> writeToLogArea("Received disconnect command from server"));
                            break;
                        }

                        if (command.equals(CommandMessage.SEND_FILE_DECLINE_EXIST)) {
                            Platform.runLater(() ->
                                writeToLogArea("File already exist on server: " + ((CommandMessage) msg).getText())
                            );
                        }

                        if (command.equals(CommandMessage.SEND_FILE_DECLINE_SPACE)) {
                            Platform.runLater(() ->
                                writeToLogArea("Not enough space to save file")
                            );
                        }

                        if (command.equals(CommandMessage.SEND_FILE_CONFIRM)) {
                            FileSender fs = new FileSender(new File(currentRootDirectory + "\\" + ((CommandMessage) msg).getText()));
                            sendQueue.add(fs);
                            fs.sendFile();

                        }

                        if (command.equals(CommandMessage.MESSAGE)) {
                            Platform.runLater(() -> {
                                writeToLogArea("Message from server: " + ((CommandMessage) msg).getText());
                            });
                        }

                    } else if(msg instanceof FileMessage) {

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> writeToLogArea(e.getMessage()));

            } finally {
                ConnectionHandler.getInstance().close();
                isAuthorized = false;
                isConnected = false;
                Platform.runLater(() -> {
                    writeToLogArea("connection closed");
                    hideRemoteArea();
                });
            }
        });
        connectionListenerThread.start();
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

    public void btnChangeDirClick() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select local directory");
        File selectedDirectory = chooser.showDialog(Main.primaryStage);
        if (selectedDirectory != null) {
            currentRootDirectory = selectedDirectory;
            updateLocalTable(selectedDirectory);
        }
    }

    public void btnChooseDirClick() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select local directory");
        File selectedDirectory = chooser.showDialog(Main.primaryStage);

        if (selectedDirectory != null) {
            currentRootDirectory = selectedDirectory;
            updateLocalTable(selectedDirectory);
            isLocalDirChoosed = true;
            chooseLocalDirArea.setManaged(false);
            chooseLocalDirArea.setVisible(false);
            localTableArea.setManaged(true);
            localTableArea.setVisible(true);
            if (isAuthorized) {
                transferBtnArea.setVisible(true);
                transferBtnArea.setManaged(true);
            }
        }
    }

    public void btnLogoutClick() {
        ConnectionHandler.getInstance().close();
        isAuthorized = false;
        writeToLogArea("initiating logout");
        writeToLogArea("connection closed by user");
        hideRemoteArea();
    }

    public void writeToLogArea(String text) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy--HH:mm");
        logAreaList.getItems().add(LocalDateTime.now().format(formatter) + " -> " + text);
        logAreaList.scrollTo(logAreaList.getItems().size() - 1);
    }

    public void initLocalFilesTable() {
        TableColumn<LocalFileListItem, String> tcName = new TableColumn<>("Name");
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcName.maxWidthProperty().bind(localTable.widthProperty().multiply(0.65));
        tcName.prefWidthProperty().bind(localTable.widthProperty().multiply(0.65));
        tcName.setResizable(false);

        TableColumn<LocalFileListItem, Long> tcSize = new TableColumn<>("Size (KB)");
        tcSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        tcSize.maxWidthProperty().bind(localTable.widthProperty().multiply(0.35));
        tcSize.prefWidthProperty().bind(localTable.widthProperty().multiply(0.35));
        tcSize.setResizable(false);

        localTable.getColumns().addAll(tcName, tcSize);
    }

    public void initRemoteFilesTable() {
        TableColumn<RemoteFileListItem, String> tcName = new TableColumn<>("Name");
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcName.maxWidthProperty().bind(remoteTable.widthProperty().multiply(0.65));
        tcName.prefWidthProperty().bind(remoteTable.widthProperty().multiply(0.65));
        tcName.setResizable(false);

        TableColumn<RemoteFileListItem, Integer> tcSize = new TableColumn<>("Size (KB)");
        tcSize.setCellValueFactory(new PropertyValueFactory<>("sizeKB"));
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
            localFileList.add(new LocalFileListItem(files[i].getName(), files[i].length() / 1024, files[i].getAbsolutePath()));
        }

        localTable.setItems(localFileList);
    }

    public void updateRemoteTable(ArrayList<String[]> files) {
        remoteFileList = FXCollections.observableArrayList();
        for(int i = 0; i < files.size(); i++) {
            remoteFileList.add(new RemoteFileListItem(files.get(i)[0], Long.parseLong(files.get(i)[1])));
        }

        remoteTable.setItems(remoteFileList);
    }

    public void requestRemoteFileList() {
        try {
            ConnectionHandler.getInstance().sendData(new CommandMessage(CommandMessage.GET_FILE_LIST));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestFileSending(File file) {
        writeToLogArea("Reqest sending file:  " + file.getAbsolutePath());
        try {
            ConnectionHandler.getInstance().sendData(new CommandMessage(CommandMessage.SEND_FILE_REQUEST, file.getName(), file.length()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnSendClick() {
        LocalFileListItem fileItem = (LocalFileListItem) localTable.getSelectionModel().getSelectedItem();
        requestFileSending(new File(fileItem.getPath()));
    }

    public void btnReceiveClick() {
    }

    public void btnRefreshRemote(ActionEvent actionEvent) {
        requestRemoteFileList();
    }
}
