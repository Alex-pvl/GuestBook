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
import client.main.Main;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class GuestbookController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button updateUserInfoButton;

    @FXML
    private Button informationButton;

    @FXML
    private Label adminLabel;

    @FXML
    private Button deleteMessageButton;

    @FXML
    private Button redactMessageButton;

    @FXML
    private Button changeRoleButton;

    @FXML
    private Button quitButton;

    @FXML
    private TextArea messageField;

    @FXML
    private Button sendMessageButton;

    @FXML
    private Pagination pagination;

    @FXML
    private Button updateTableButton;

    //размер таблицы
    private static int dataSize = 0;
    //таблица записей
    private TableView<Note> table = createTable();
    //данные
    private List<Note> data = createData();
    //количество строк на странице
    private final static int rowsPerPage = 10;

    private TableView<Note> createTable() {
        TableView<Note> table = new TableView<>();

        TableColumn<Note, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(param -> param.getValue().id);
        idColumn.setPrefWidth(30);
        idColumn.setResizable(false);
        idColumn.setSortable(false);

        TableColumn<Note, String> emailColumn = new TableColumn<>("E-Mail");
        emailColumn.setCellValueFactory(param -> param.getValue().email);
        emailColumn.setPrefWidth(150);
        emailColumn.setMinWidth(55);
        emailColumn.setSortable(false);

        TableColumn<Note, String> dateColumn = new TableColumn<>("Дата");
        dateColumn.setCellValueFactory(param -> param.getValue().date);
        dateColumn.setMaxWidth(130);
        dateColumn.setMinWidth(45);
        dateColumn.setSortable(false);

        TableColumn<Note, String> messageColumn = new TableColumn<>("Комментарий");
        messageColumn.setCellValueFactory(param -> param.getValue().message);
        messageColumn.setMinWidth(110);
        messageColumn.setPrefWidth(390);
        messageColumn.setSortable(false);

        table.getColumns().addAll(idColumn, emailColumn, dateColumn, messageColumn);

        return table;
    }

    //этот метод используется для заполнения данных в таблицу
    private List<Note> createData() {
        updateDataSize();
        List<Note> data = null;
        try {
            data = new ArrayList<>(Main.client.getAllComments(dataSize));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }

    void updateDataSize() {
        try {
            dataSize = Main.client.getCountOfTable("comments");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        try {
            data = Main.client.getAllComments(dataSize);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        table = createTable();
        pageCount();
    }

    @FXML
    void initialize() {
        adminLabel.setVisible(false);
        deleteMessageButton.setVisible(false);
        redactMessageButton.setVisible(false);
        changeRoleButton.setVisible(false);
        if (Main.user.getRole() == 2) {
            adminLabel.setVisible(true);
            deleteMessageButton.setVisible(true);
            redactMessageButton.setVisible(true);
            changeRoleButton.setVisible(true);
        }
        pagination.setCurrentPageIndex(0);
        pageCount();

        sendMessageButton.setOnAction(actionEvent -> {
            messageField.setStyle("-fx-border-color: grey");
            messageField.setPromptText("Введите сообщение...");
            String message = messageField.getText();
            if (!message.isEmpty()) {
                messageField.clear();
                try {
                    Main.client.sendMessage(Main.user.getEmail(), message);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateTable();
            } else {
                messageField.setStyle("-fx-border-color: red");
                messageField.setPromptText("Обязательно введите сообщение!");
            }
        });
        quitButton.setOnAction(actionEvent -> {
            //скрываем предыдущее окно
            quitButton.getScene().getWindow().hide();
            //загружаем новое окно
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/client/views/SignIn.fxml"));
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

        updateUserInfoButton.setOnAction(actionEvent -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/client/views/EditUser.fxml"));
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

        updateTableButton.setOnAction(actionEvent -> {
            updateTable();
        });

        deleteMessageButton.setOnAction(actionEvent -> {
            if (table.getSelectionModel().getSelectedIndex() != -1){
                int _id = table.getSelectionModel().getSelectedItem().id.getValue();
                try {
                    Main.client.deleteRowFromTable(_id, "comments");
                    table.getItems().removeAll(table.getSelectionModel().getSelectedItem());
                    updateTable();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Предупреждение");
                alert.setHeaderText("Ошибка выделения строки!");
                alert.setContentText("Выделите строку в Гостевой Книге, чтобы удалить запись!");
                alert.showAndWait();
            }

        });

        redactMessageButton.setOnAction(actionEvent -> {
            if (table.getSelectionModel().getSelectedIndex() != -1) {
                int _id = table.getSelectionModel().getSelectedItem().id.getValue();
                String _date = table.getSelectionModel().getSelectedItem().date.getValue();
                String _email = table.getSelectionModel().getSelectedItem().email.getValue();
                String _message = table.getSelectionModel().getSelectedItem().message.getValue();
                Main.message = new Message(_id, _date, _email, _message);
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/client/views/EditMessage.fxml"));
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
                alert.setContentText("Выделите строку в Гостевой Книге, чтобы редактировать запись!");
                alert.showAndWait();
            }
        });

        changeRoleButton.setOnAction(actionEvent -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/client/views/Users.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Аккаунты");
            stage.showAndWait();
        });

        informationButton.setOnAction(actionEvent -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/client/views/Info.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Информационный стенд");
            stage.showAndWait();
        });
    }

    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, data.size());
        table.setItems(FXCollections.observableArrayList(data.subList(fromIndex, toIndex)));
        return new BorderPane(table);
    }

    //статический класс для представления данных в таблице
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
