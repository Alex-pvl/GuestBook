package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.control.Label;
import main.Client;
import main.Main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignUpController {
    String firstName;
    String lastName;
    String userName;
    String password;
    String email;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button signInBtn;

    @FXML
    private TextField firstNameText;

    @FXML
    private TextField lastNameText;

    @FXML
    private TextField loginText;

    @FXML
    private TextField emailText;

    @FXML
    private PasswordField passwordText;

    @FXML
    private Button signUpBtn;

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

    private static final String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    void init() {
        firstNameError.setVisible(false);
        lastNameError.setVisible(false);
        loginError.setVisible(false);
        emailError.setVisible(false);
        passwordError.setVisible(false);
        firstNameText.setStyle("-fx-border-color: grey");
        lastNameText.setStyle("-fx-border-color: grey");
        loginText.setStyle("-fx-border-color: grey");
        passwordText.setStyle("-fx-border-color: grey");
        emailText.setStyle("-fx-border-color: grey");
    }

    @FXML
    void initialize() {
        init();
        //обрабатываем событие - нажатие на кнопку "Зарегистрироваться"
        signUpBtn.setOnAction(actionEvent -> {
            //регистрация пользователя
            firstName = firstNameText.getText();
            lastName = lastNameText.getText();
            userName = loginText.getText();
            password = passwordText.getText();
            email = emailText.getText();
            if (!Main.isConnected) {
                Main.client = new Client();
                Main.client.enable();
                Main.client.start();
                while (!Main.client.isConnected()) {
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
            boolean isUsedLogin = false;
            boolean isUsedEmail = false;
            boolean isEmailValid = false;
            if (!(firstName.isEmpty() || lastName.isEmpty()
                    || userName.isEmpty() || password.isEmpty() || email.isEmpty())) {
                isFieldUsed = true;
            } else {
                if (firstName.isEmpty()) {
                    firstNameText.setStyle("-fx-border-color: red");
                    firstNameError.setText("Поле не может быть пустым!");
                    firstNameError.setVisible(true);
                } else {
                    firstNameText.setStyle("-fx-border-color: grey");
                    firstNameError.setVisible(false);
                }
                if (lastName.isEmpty()) {
                    lastNameText.setStyle("-fx-border-color: red");
                    lastNameError.setText("Поле не может быть пустым!");
                    lastNameError.setVisible(true);
                } else {
                    lastNameText.setStyle("-fx-border-color: grey");
                    lastNameError.setVisible(false);
                }
                if (userName.isEmpty()) {
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
                if (email.isEmpty()) {
                    emailText.setStyle("-fx-border-color: red");
                    emailError.setText("Поле не может быть пустым!");
                    emailError.setVisible(true);
                } else {
                    emailText.setStyle("-fx-border-color: grey");
                    emailError.setVisible(false);
                }
            }
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                isEmailValid = true;
            } else {
                emailText.setStyle("-fx-border-color:red");
                emailError.setText("Email введен некорректно!");
                emailError.setVisible(true);
            }
            try {
                isUsedLogin = Main.client.isUsed("userName", userName);
                isUsedEmail = Main.client.isUsed("email", email);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (isUsedLogin) {
                loginText.setStyle("-fx-border-color:red");
                loginError.setText("Логин занят другим пользователем!");
                loginError.setVisible(true);
            }
            if (isUsedEmail) {
                emailText.setStyle("-fx-border-color:red");
                emailError.setText("Email занят другим пользователем!");
                emailError.setVisible(true);
            }
            if (Main.isConnected && !(isUsedEmail || isUsedLogin) && isFieldUsed && isEmailValid) {
                try {
                    Main.client.registerAccount(firstName, lastName, userName, password, email);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                //скрываем предыдущее окно
                signUpBtn.getScene().getWindow().hide();
                //загружаем новое окно
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/view/SignIn.fxml"));
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
            loader.setLocation(getClass().getResource("/view/SignIn.fxml"));
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