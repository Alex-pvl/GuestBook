package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.Client;
import main.Main;

public class SignInController {


    String login;
    String password;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button signUpBtn;

    @FXML
    private TextField loginText;

    @FXML
    private PasswordField passwordText;

    @FXML
    private Button signInBtn;

    @FXML
    private Label loginError;

    @FXML
    private Label passwordError;

    void init() {
        loginError.setVisible(false);
        passwordError.setVisible(false);
        loginText.setStyle("-fx-border-color: grey");
        passwordText.setStyle("-fx-border-color: grey");
    }

    @FXML
    void initialize() {
        init();
        //обрабатываем событие - нажатие на кнопку "Авторизоваться"
        signInBtn.setOnAction(actionEvent -> {
            login = loginText.getText();
            password = passwordText.getText();
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
                    loginText.setStyle("-fx-border-color: red");
                    loginError.setText("Поле не может быть пустым!");
                    loginError.setVisible(true);
                } else {
                    loginText.setStyle("-fx-border-color: grey");
                    loginError.setVisible(false);
                }
                if (password.isEmpty()) {
                    passwordText.setStyle("-fx-border-color: red");
                    passwordError.setText("Поле не может быть пустым!");
                    passwordError.setVisible(true);
                } else {
                    passwordText.setStyle("-fx-border-color: grey");
                    passwordError.setVisible(false);
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
                            System.out.println("Welcome, user(" + login + ")!");
                        }
                        else
                            System.out.println("Welcome, admin(" + login + ")!");
                        //скрываем предыдущее окно
                        signInBtn.getScene().getWindow().hide();
                        //загружаем новое окно
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/view/GuestBook.fxml"));
                        try {
                            loader.load();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Parent root = loader.getRoot();
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.setResizable(false);
                        stage.setTitle("Гостевая Книга");
                        stage.showAndWait();
                    }
                    else if (msign.equalsIgnoreCase("authorize isn't success")) {
                        System.out.println(msign);
                        passwordText.setStyle("-fx-border-color: red");
                        loginText.setStyle("-fx-border-color: red");
                        loginError.setText("Неверный логин/пароль!");
                        loginError.setVisible(true);
                        passwordError.setText("Неверный логин/пароль!");
                        passwordError.setVisible(true);
                        loginText.clear();
                        passwordText.clear();
                    }
                    else if (msign.equalsIgnoreCase("-1"))
                        System.out.println("Error");
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        //обрабатываем событие - нажатие на кнопку "Зарегистрироваться"
        signUpBtn.setOnAction(actionEvent -> {
            //скрываем предыдущее окно
            signUpBtn.getScene().getWindow().hide();
            //загружаем новое окно
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/SignUp.fxml"));
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
