package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import main.Main;

public class UsersController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Pagination pagination;

    @FXML
    private Button deleteUserBtn;

    @FXML
    private Button changeRoleBtn;

    //размер таблицы
    private static int dataSize = 0;
    //таблица записей
    private TableView<Account> table = createTable();
    //данные
    private List<Account> data = createData();
    //количество строк на странице
    private final static int rowsPerPage = 10;

    private TableView<Account> createTable() {
        TableView<Account> table = new TableView<>();

        TableColumn<Account,Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(param -> param.getValue().id);
        idColumn.setPrefWidth(30);
        idColumn.setResizable(false);
        idColumn.setSortable(false);

        TableColumn<Account,String> userNameColumn = new TableColumn<>("Логин");
        userNameColumn.setCellValueFactory(param -> param.getValue().userName);
        userNameColumn.setPrefWidth(150);
        userNameColumn.setMinWidth(100);
        userNameColumn.setSortable(false);

        TableColumn<Account,String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(param -> param.getValue().email);
        emailColumn.setPrefWidth(150);
        emailColumn.setMinWidth(50);
        emailColumn.setSortable(false);

        TableColumn<Account,String> roleColumn = new TableColumn<>("Роль");
        roleColumn.setCellValueFactory(param -> param.getValue().role);
        roleColumn.setMinWidth(40);
        roleColumn.setMaxWidth(100);
        roleColumn.setSortable(false);

        table.getColumns().addAll(idColumn,userNameColumn,emailColumn,roleColumn);

        return table;
    }

    //этот метод используется для заполнения данных в таблицу
    private List<Account> createData() {
        updateDataSize();
        List<Account> data = null;
        try {
            data = new ArrayList<>(Main.client.getAllUsers(dataSize));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }

    void updateDataSize(){
        try {
            dataSize = Main.client.getCountOfTable("users");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void pageCount(){
        int pages = 1;
        if(data.size() % rowsPerPage == 0)
        {
            pages = data.size() / rowsPerPage;
        }
        else if (data.size() > rowsPerPage) {
            pages = data.size() / rowsPerPage + 1;
        }
        pagination.setPageCount(pages);
        pagination.setPageFactory(this::createPage);
    }

    void updateTable(){
        updateDataSize();
        try {
            data = Main.client.getAllUsers(dataSize);
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

        pagination.setCurrentPageIndex(0);
        pageCount();

        deleteUserBtn.setOnAction(actionEvent -> {
            if (table.getSelectionModel().getSelectedIndex() != -1) {
                int _id = table.getSelectionModel().getSelectedItem().id.getValue();
                try {
                    Main.client.deleteRowFromTable(_id, "users");
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
                alert.setTitle("Error!");
                alert.setHeaderText("Ошибка!");
                alert.setContentText("Выделите строку для удаления пользователя!");
                alert.showAndWait();
            }
        });

        changeRoleBtn.setOnAction(actionEvent -> {
            if (table.getSelectionModel().getSelectedIndex() != -1) {
                int _id = table.getSelectionModel().getSelectedItem().id.getValue();
                String _role = table.getSelectionModel().getSelectedItem().role.getValue();
                int role_id = 1;
                if (_role.equals("Пользователь")) {
                    role_id = 2;
                }
                try {
                    Main.client.changeRoleId(_id, role_id);
                    updateTable();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error!");
                alert.setHeaderText("Ошибка!");
                alert.setContentText("Выделите строку для смены роли!");
                alert.showAndWait();
            }
        });
    }

    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, data.size());
        table.setItems(FXCollections.observableArrayList(data.subList(fromIndex, toIndex)));
        return new BorderPane(table);
    }

    //статический класс для представления данных в таблице
    public static class Account {
        private final ObservableValue<Integer> id;
        private final SimpleStringProperty userName;
        private final SimpleStringProperty email;
        private final SimpleStringProperty role;


        public Account(int id, String userName, String email, String role) {
            this.id = new SimpleObjectProperty<>(id);
            this.userName = new SimpleStringProperty(userName);
            this.email = new SimpleStringProperty(email);
            this.role = new SimpleStringProperty(role);
        }
    }


}
