package ru.alexpvl.guestbookfx.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.alexpvl.guestbookfx.client.Client;
import ru.alexpvl.guestbookfx.client.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignInController {
    String login;
    String password;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL connection;

    @FXML
    private Button signInBtn;

    @FXML
    private Button signUpBtn;

    @FXML
    private TextField loginText;

    @FXML
    private PasswordField passwordText;

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
        // Войти
        signInBtn.setOnAction(e -> {
            login = loginText.getText();
            password = passwordText.getText();
            if (!Main.isConnected) {
                Main.client = new Client();
                // Main.client.enable();
                // Main.client.start();
                while (!Main.isConnected) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                // Main.isConnected = Main.client.isConnected();
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
                /*try {
                    String msign = Main.client.loginAccount(login, password);
                    if (!(msign.equalsIgnoreCase("SignIn Error") || msign.equalsIgnoreCase("-1"))) {
                        Main.client.setUserInfo(login, password);
                        var user_data = msign.split(" ");
                        if (Main.user.getRole() == 0) {
                            System.out.println("User entered");
                        } else {
                            System.out.println("Admin entered");
                        }
                        signInBtn.getScene().getWindow().hide();
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("ru/alexpvl/guestbookfx/client/views/GuestBook.fxml"));
                        try {
                            loader.load();
                        } catch (IOException exp) {
                            exp.printStackTrace();
                        }
                        Parent root = loader.getRoot();
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.setResizable(false);
                        stage.setTitle("Гостевая книга");
                        stage.showAndWait();
                    }
                    else if (msign.equalsIgnoreCase("SignIn Error")) {
                        System.out.println(msign);
                        passwordText.setStyle("-fx-border-color: red");
                        loginText.setStyle("-fx-border-color: red");
                        loginError.setText("Неправильный логин/пароль!");
                        loginError.setVisible(true);
                        passwordError.setText("Неправильный логин/пароль!");
                        passwordError.setVisible(true);
                        loginText.clear();
                        passwordText.clear();
                    }
                    else if (msign.equalsIgnoreCase("-1")) {
                        System.out.println("Unknown error");
                    }

                } catch (IOException | InterruptedException exp) {
                    exp.printStackTrace();
                }

                     */
            }
        });


        // Регистрация
        signUpBtn.setOnAction(e -> {
            signUpBtn.getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ru/alexpvl/guestbookfx/client/views/SignUp.fxml"));
            try {
                loader.load();
            } catch (IOException ex) {
                ex.printStackTrace();
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
