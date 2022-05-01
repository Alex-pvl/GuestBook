package client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import client.main.Client;
import client.main.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignInController {
    String login;
    String password;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button signUpButton;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signInButton;

    @FXML
    private Label errorLabel0;

    @FXML
    private Label errorLabel1;

    void init() {
        errorLabel0.setVisible(false);
        errorLabel1.setVisible(false);
        loginField.setStyle("-fx-border-color: grey");
        passwordField.setStyle("-fx-border-color: grey");
    }

    @FXML
    void initialize() {
        init();
        //обрабатываем событие - нажатие на кнопку "Авторизоваться"
        signInButton.setOnAction(actionEvent -> {
            login = loginField.getText();
            password = passwordField.getText();
            if(!Main.isConnected) {
                Main.client = new Client();
                Main.client.enable();
                Main.client.start();
                while (!Main.client.isConnected()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Main.isConnected = Main.client.isConnected();
            }
            init();
            boolean isFieldUsed = false;
            if (!(login.isEmpty() || password.isEmpty())) {
                isFieldUsed = true;
            } else {
                if (login.isEmpty()) {
                    loginField.setStyle("-fx-border-color: red");
                    errorLabel0.setText("Введите соответсвующее поле!");
                    errorLabel0.setVisible(true);
                } else {
                    loginField.setStyle("-fx-border-color: grey");
                    errorLabel0.setVisible(false);
                }
                if (password.isEmpty()) {
                    passwordField.setStyle("-fx-border-color: red");
                    errorLabel1.setText("Введите соответсвующее поле!");
                    errorLabel1.setVisible(true);
                } else {
                    passwordField.setStyle("-fx-border-color: grey");
                    errorLabel1.setVisible(false);
                }
            }
            if (Main.isConnected && isFieldUsed) {
                try {
                    String msign = Main.client.loginAccount(login, password);
                    //если был найден аккаунт в базе, то регистрируемся
                    if (!(msign.equalsIgnoreCase("authorize isn't success")
                            || msign.equalsIgnoreCase("-1"))){
                        Main.client.setUserInfo(login, password);
                        var user_data = msign.split(" ");
                        if (Main.user.getRole() == 1) {
                            System.out.println("Hello, User!");
                        }
                        else
                            System.out.println("Hello, Admin!");
                        //скрываем предыдущее окно
                        signInButton.getScene().getWindow().hide();
                        //загружаем новое окно
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/client/views/GuestBook.fxml"));
                        try {
                            loader.load();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Parent root = loader.getRoot();
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.setResizable(false);
                        stage.setTitle("Гостевая книга");
                        stage.showAndWait();
                    }
                    else if (msign.equalsIgnoreCase("authorize isn't success")) {
                        System.out.println(msign);
                        passwordField.setStyle("-fx-border-color: red");
                        loginField.setStyle("-fx-border-color: red");
                        errorLabel0.setText("Неправильный логин/пароль!");
                        errorLabel0.setVisible(true);
                        errorLabel1.setText("Неправильный логин/пароль!");
                        errorLabel1.setVisible(true);
                        loginField.clear();
                        passwordField.clear();
                    }
                    else if (msign.equalsIgnoreCase("-1"))
                        System.out.println("Unknown Error");
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        //обрабатываем событие - нажатие на кнопку "Зарегистрироваться"
        signUpButton.setOnAction(actionEvent -> {
            //скрываем предыдущее окно
            signUpButton.getScene().getWindow().hide();
            //загружаем новое окно
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/client/views/SignUp.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Регистрация");
            stage.showAndWait();
        });
    }
}
