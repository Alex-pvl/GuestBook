package ru.alexpvl.guestbookfx.client.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.scene.control.Label;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.alexpvl.guestbookfx.client.Client;
import ru.alexpvl.guestbookfx.client.Main;

public class SignUpController {
    String firstName;
    String lastName;
    String login;
    String password;
    String email;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button signInBtn;

    @FXML
    private Button signUpBtn;

    @FXML
    private TextField firstNameText;

    @FXML
    private TextField lastNameText;

    @FXML
    private TextField emailText;

    @FXML
    private TextField loginText;

    @FXML
    private PasswordField passwordText;

    @FXML
    private Label firstNameError;

    @FXML
    private Label lastNameError;

    @FXML
    private Label emailError;

    @FXML
    private Label loginError;

    @FXML
    private Label passwordError;

    private static final String regex =
            "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    public void init() {
        firstNameError.setVisible(false);
        lastNameError.setVisible(false);
        emailError.setVisible(false);
        loginError.setVisible(false);
        passwordError.setVisible(false);
        firstNameText.setStyle("-fx-border-color: grey");
        lastNameText.setStyle("-fx-border-color: grey");
        emailText.setStyle("-fx-border-color: grey");
        loginText.setStyle("-fx-border-color: grey");
        passwordText.setStyle("-fx-border-color: grey");
    }

    @FXML
    public void initialize() {
        init();
        //обрабатываем событие - нажатие на кнопку "Зарегистрироваться"
        signUpBtn.setOnAction(actionEvent -> {
            //регистрация пользователя
            firstName = firstNameText.getText();
            lastName = lastNameText.getText();
            login = loginText.getText();
            password = passwordText.getText();
            email = emailText.getText();
            if (!Main.isConnected) {
                Main.client = new Client();
                // Main.client.enable();
                // Main.client.start();
                /*while (!Main.client.isConnected()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Main.isConnected = Main.client.isConnected();
                 */
            }
            init();
            boolean isFieldUsed = false;
            boolean isUsedLogin = false;
            boolean isUsedEmail = false;
            boolean isEmailValid = false;
            if (!(firstName.isEmpty() || lastName.isEmpty()
                    || login.isEmpty() || password.isEmpty() || email.isEmpty())) {
                isFieldUsed = true;
            } else {
                if (firstName.isEmpty()) {
                    firstNameText.setStyle("-fx-border-color: red");
                    firstNameError.setVisible(true);
                } else {
                    firstNameText.setStyle("-fx-border-color: grey");
                    firstNameError.setVisible(false);
                }
                if (lastName.isEmpty()) {
                    lastNameText.setStyle("-fx-border-color: red");
                    lastNameError.setVisible(true);
                } else {
                    lastNameText.setStyle("-fx-border-color: grey");
                    lastNameError.setVisible(false);
                }
                if (login.isEmpty()) {
                    loginText.setStyle("-fx-border-color: red");
                    loginError.setText("Поле не может быть пустым!");
                    loginError.setVisible(true);
                } else {
                    loginText.setStyle("-fx-border-color: grey");
                    loginError.setText("Поле не может быть пустым!");
                    loginError.setVisible(false);
                }
                if (password.isEmpty()) {
                    passwordText.setStyle("-fx-border-color: red");
                    passwordError.setVisible(true);
                } else {
                    passwordText.setStyle("-fx-border-color: grey");
                    passwordError.setVisible(false);
                }
                if (email.isEmpty()) {
                    emailText.setStyle("-fx-border-color: red");
                    emailError.setText("Поле не может быть пустым!");
                    emailError.setVisible(true);
                } else {
                    emailText.setStyle("-fx-border-color: grey");
                    emailError.setText("Поле не может быть пустым!");
                    emailError.setVisible(false);
                }
            }
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                isEmailValid = true;
            } else {
                emailText.setStyle("-fx-border-color:red");
                emailError.setText("Некорректно введён Email!");
                emailError.setVisible(true);
            }
            /*try {
                isUsedLogin = Main.client.isUsed("userName", userName);
                isUsedEmail = Main.client.isUsed("email", email);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            if (isUsedLogin) {
                loginText.setStyle("-fx-border-color:red");
                loginError.setText("Логин занят другим пользователем!");
                loginError.setVisible(true);
            } /*else {
                loginField.setStyle("-fx-border-color:grey");
                errorLabel2.setVisible(false);
            }*/
            if (isUsedEmail) {
                emailText.setStyle("-fx-border-color:red");
                emailError.setText("Email занят другим пользователем!");
                emailError.setVisible(true);
            } /*else {
                emailField.setStyle("-fx-border-color:grey");
                errorLabel3.setVisible(false);
            }*/
            if (Main.isConnected && !(isUsedEmail || isUsedLogin) && isFieldUsed && isEmailValid) {
                /*try {
                    Main.client.registerAccount(firstName, lastName, userName, password, email);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }*/
                //скрываем предыдущее окно
                signUpBtn.getScene().getWindow().hide();
                //загружаем новое окно
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/view/signIn.fxml"));
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
            }
        });

        //обрабатываем событие - нажатие на кнопку "Авторизоваться"
        signInBtn.setOnAction(actionEvent -> {
            //скрываем предыдущее окно
            signInBtn.getScene().getWindow().hide();
            //загружаем новое окно
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/signIn.fxml"));
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
    }

}
