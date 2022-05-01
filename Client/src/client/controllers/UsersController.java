package client.controllers;

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
import client.Main;


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

    private static int dataSize = 0;

    private TableView<Account> table = createTable();

    private List<Account> data = createData();

    public static final int rowsPerPage = 10;

    private TableView<Account> createTable() {
        TableView<Account> table = new TableView<>();
        TableColumn<Account, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(e -> e.getValue().id);
        idColumn.setPrefWidth(30);
        idColumn.setResizable(false);
        idColumn.setSortable(false);

        TableColumn<Account, String> loginColumn = new TableColumn<>("Логин");
        loginColumn.setCellValueFactory(e -> e.getValue().login);
        loginColumn.setPrefWidth(150);
        loginColumn.setMinWidth(100);
        loginColumn.setSortable(false);

        TableColumn<Account, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(e -> e.getValue().email);
        emailColumn.setPrefWidth(150);
        emailColumn.setMinWidth(50);
        emailColumn.setSortable(false);

        TableColumn<Account, String> roleColumn = new TableColumn<>("Роль");
        emailColumn.setCellValueFactory(e -> e.getValue().role);
        emailColumn.setMaxWidth(100);
        emailColumn.setMinWidth(40);
        emailColumn.setSortable(false);

        table.getColumns().addAll(idColumn, emailColumn, loginColumn, roleColumn);
        return table;
    }

    private List<Account> createData() {
        updateDataSize();
        List<Account> data = null;
        data = new ArrayList<>(Main.client.getAllUsers(dataSize));
        return data;
    }

    void updateDataSize(){
        dataSize = Main.client.getCountOfTable("accounts");
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
        data = Main.client.getAllUsers(dataSize);
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
                Main.client.deleteRowFromTable(_id, "users");
                table.getItems().removeAll(table.getSelectionModel().getSelectedItem());
                updateTable();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Предупреждение");
                alert.setHeaderText("Ошибка выделения строки!");
                alert.setContentText("Выделите строку в таблице аккаунтов, чтобы удалить аккаунт!");
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
            Main.client.changeRoleId(_id, role_id);
            updateTable();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Предупреждение");
            alert.setHeaderText("Ошибка!");
            alert.setContentText("Выделите строку для изменения роли!");
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


public static class Account {
        private final ObservableValue<Integer> id;
        private final SimpleStringProperty login;
        private final SimpleStringProperty email;
        private final SimpleStringProperty role;

        public Account(int id, String login, String email, String role) {
            this.id = new SimpleObjectProperty<>(id);
            this.login = new SimpleStringProperty(login);
            this.email = new SimpleStringProperty(email);
            this.role = new SimpleStringProperty(role);
        }
    }
}
