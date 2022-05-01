package client.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import client.bean.Message;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import client.Main;

public class GuestbookController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button updateUserInfoBtn;

    @FXML
    private Button infoBtn;

    @FXML
    private Label adminLabel;

    @FXML
    private Button deleteMessageBtn;

    @FXML
    private Button editMessageBtn;

    @FXML
    private Button changeRoleBtn;

    @FXML
    private Button exitBtn;

    @FXML
    private TextArea messageText;

    @FXML
    private Button sendBtn;

    @FXML
    private Pagination pagination;

    @FXML
    private Button updateTableBtn;

    private static int dataSize = 0;

    private final static int rowsPerPage = 10;

    private TableView<Note> table = createTable();

    private List<Note> data = createData();

    private TableView<Note> createTable() {
        TableView<Note> table = new TableView<>();

        TableColumn<Note, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(e -> e.getValue().id);
        idColumn.setPrefWidth(30);
        idColumn.setResizable(false);
        idColumn.setSortable(false);

        TableColumn<Note, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(e -> e.getValue().email);
        emailColumn.setPrefWidth(150);
        emailColumn.setMinWidth(55);
        emailColumn.setSortable(false);

        TableColumn<Note, String> messageColumn = new TableColumn<>("Комментарий");
        messageColumn.setCellValueFactory(param -> param.getValue().message);
        messageColumn.setMinWidth(110);
        messageColumn.setPrefWidth(390);
        messageColumn.setSortable(false);

        TableColumn<Note, String> dateColumn = new TableColumn<>("Дата");
        dateColumn.setCellValueFactory(param -> param.getValue().date);
        dateColumn.setMaxWidth(130);
        dateColumn.setMinWidth(45);
        dateColumn.setSortable(false);

        table.getColumns().addAll(idColumn, emailColumn, messageColumn, dateColumn);

        return table;
    }

    private List<Note> createData() {
        updateDataSize();
        List<Note> data = null;
        data = new ArrayList<>(Main.client.getAllComments(dataSize));
        return data;

    }

    void updateDataSize() {
        dataSize = Main.client.getCountOfTable("comments");
    }

    void pageCount() {
        int pages = 1;
        if (data.size() % rowsPerPage == 0) {
            pages = data.size() / rowsPerPage;
        } else if (data.size() > rowsPerPage) {
            pages = data.size() / rowsPerPage + 1;
        }
        pagination.setPageCount(pages);
        pagination.setPageFactory(this::createPage);
    }

    void updateTable() {
        updateDataSize();
        data = Main.client.getAllComments(dataSize);
        table = createTable();
        pageCount();
    }

    @FXML
    void initialize() {
        adminLabel.setVisible(false);
        deleteMessageBtn.setVisible(false);
        editMessageBtn.setVisible(false);
        changeRoleBtn.setVisible(false);
        if (Main.user.getRole() == 2) {
            adminLabel.setVisible(true);
            deleteMessageBtn.setVisible(true);
            editMessageBtn.setVisible(true);
            changeRoleBtn.setVisible(true);
        }
        pagination.setCurrentPageIndex(0);
        pageCount();

        sendBtn.setOnAction(actionEvent -> {
            messageText.setStyle("-fx-border-color: grey");
            messageText.setPromptText("Введите сообщение...");
            String message = messageText.getText();
            if (!message.isEmpty()) {
                messageText.clear();
                Main.client.sendMessage(Main.user.getEmail(), message);
                updateTable();
            } else {
                messageText.setStyle("-fx-border-color: red");
                messageText.setPromptText("Поле не может быть пустым!");
            }
        });
        exitBtn.setOnAction(actionEvent -> {
            //скрываем предыдущее окно
            exitBtn.getScene().getWindow().hide();
            //загружаем новое окно
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("src/main/java/ru/alexpvl/guestbookfx/client/views/SignIn.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Авторизация");
            stage.show();
        });

        updateUserInfoBtn.setOnAction(actionEvent -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("src/main/java/ru/alexpvl/guestbookfx/client/views/EditUser.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Редактирование аккаунта");
            stage.show();
        });

        updateTableBtn.setOnAction(actionEvent -> {
            updateTable();
        });

        deleteMessageBtn.setOnAction(actionEvent -> {
            if (table.getSelectionModel().getSelectedIndex() != -1){
                int _id = table.getSelectionModel().getSelectedItem().id.getValue();
                Main.client.deleteRowFromTable(_id, "comments");
                table.getItems().removeAll(table.getSelectionModel().getSelectedItem());
                updateTable();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Предупреждение");
                alert.setHeaderText("Ошибка");
                alert.setContentText("Выделите строку для удаления!");
                alert.showAndWait();
            }

        });

        editMessageBtn.setOnAction(actionEvent -> {
            if (table.getSelectionModel().getSelectedIndex() != -1) {
                int _id = table.getSelectionModel().getSelectedItem().id.getValue();
                String _date = table.getSelectionModel().getSelectedItem().date.getValue();
                String _email = table.getSelectionModel().getSelectedItem().email.getValue();
                String _message = table.getSelectionModel().getSelectedItem().message.getValue();
                Main.message = new Message(_id, _date, _email, _message);
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("src/main/java/ru/alexpvl/guestbookfx/client/views/EditMessage.fxml"));
                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Parent root = loader.getRoot();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.setTitle("Редактирование записи");
                stage.show();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Предупреждение");
                alert.setHeaderText("Ошибка выделения строки!");
                alert.setContentText("Выделите строку для изменения!");
                alert.showAndWait();
            }
        });

        changeRoleBtn.setOnAction(actionEvent -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("src/main/java/ru/alexpvl/guestbookfx/client/views/Users.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Пользователи");
            stage.showAndWait();
        });

        infoBtn.setOnAction(actionEvent -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("src/main/java/ru/alexpvl/guestbookfx/client/views/Info.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Информация");
            stage.showAndWait();
        });
    }

    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, data.size());
        table.setItems(FXCollections.observableArrayList(data.subList(fromIndex, toIndex)));
        return new BorderPane(table);
    }

    public static class Note {
        private final ObservableValue<Integer> id;
        private final SimpleStringProperty email;
        private final SimpleStringProperty date;
        private final SimpleStringProperty message;

        public Note(int id, String email, String date, String message) {
            this.id = new SimpleObjectProperty<>(id);
            this.email = new SimpleStringProperty(email);
            this.date = new SimpleStringProperty(date);
            this.message = new SimpleStringProperty(message);
        }
    }
}
