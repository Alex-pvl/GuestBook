package client.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import client.main.Main;
import client.bean.User;

public class EditUserController {
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
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField loginField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button saveButton;

    @FXML
    private Label errorLabel0;

    @FXML
    private Label errorLabel1;

    @FXML
    private Label errorLabel2;

    @FXML
    private Label errorLabel3;

    @FXML
    private Label errorLabel4;

    private static final String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    void init() {
        errorLabel0.setVisible(false);
        errorLabel1.setVisible(false);
        errorLabel2.setVisible(false);
        errorLabel3.setVisible(false);
        errorLabel4.setVisible(false);
        nameField.setStyle("-fx-border-color: grey");
        surnameField.setStyle("-fx-border-color: grey");
        loginField.setStyle("-fx-border-color: grey");
        passwordField.setStyle("-fx-border-color: grey");
        emailField.setStyle("-fx-border-color: grey");
    }

    @FXML
    void initialize() {
        init();
        nameField.setText(Main.user.getFirstName());
        surnameField.setText(Main.user.getLastName());
        loginField.setText(Main.user.getLogin());
        emailField.setText(Main.user.getEmail());
        passwordField.setText(Main.user.getPassword());

        saveButton.setOnAction(actionEvent -> {
            firstName = nameField.getText();
            lastName = surnameField.getText();
            userName = loginField.getText();
            password = passwordField.getText();
            email = emailField.getText();
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
                    nameField.setStyle("-fx-border-color: red");
                    errorLabel0.setVisible(true);
                } else {
                    nameField.setStyle("-fx-border-color: grey");
                    errorLabel0.setVisible(false);
                }
                if (lastName.isEmpty()) {
                    surnameField.setStyle("-fx-border-color: red");
                    errorLabel1.setVisible(true);
                } else {
                    surnameField.setStyle("-fx-border-color: grey");
                    errorLabel1.setVisible(false);
                }
                if (userName.isEmpty()) {
                    loginField.setStyle("-fx-border-color: red");
                    errorLabel2.setText("Введите соответсвующее поле!");
                    errorLabel2.setVisible(true);
                } else {
                    loginField.setStyle("-fx-border-color: grey");
                    errorLabel2.setText("Введите соответсвующее поле!");
                    errorLabel2.setVisible(false);
                }
                if (password.isEmpty()) {
                    passwordField.setStyle("-fx-border-color: red");
                    errorLabel4.setVisible(true);
                } else {
                    passwordField.setStyle("-fx-border-color: grey");
                    errorLabel4.setVisible(false);
                }
                if (email.isEmpty()) {
                    emailField.setStyle("-fx-border-color: red");
                    errorLabel3.setText("Введите соответсвующее поле!");
                    errorLabel3.setVisible(true);
                } else {
                    emailField.setStyle("-fx-border-color: grey");
                    errorLabel3.setText("Введите соответсвующее поле!");
                    errorLabel3.setVisible(false);
                }
            }
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                isEmailValid = true;
            } else {
                emailField.setStyle("-fx-border-color:red");
                errorLabel3.setText("Неправильно введён E-Mail!");
                errorLabel3.setVisible(true);
            }
            try {
                isUsedLogin = Main.client.isUsed("login", userName);
                isUsedEmail = Main.client.isUsed("email", email);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (isUsedLogin && !(userName.equals(Main.user.getLogin()))) {
                loginField.setStyle("-fx-border-color:red");
                errorLabel2.setText("Логин занят другим пользователем!");
                errorLabel2.setVisible(true);
            } else {
                isUsedLogin = false;
            }
            if (isUsedEmail && !(email.equals(Main.user.getEmail()))) {
                emailField.setStyle("-fx-border-color:red");
                errorLabel3.setText("E-Mail занят другим пользователем!");
                errorLabel3.setVisible(true);
            } else {
                isUsedEmail = false;
            }
            if (Main.isConnected && !(isUsedEmail || isUsedLogin) && isFieldUsed && isEmailValid) {
                Main.user = new User(Main.user.getId(), firstName, lastName, email, userName,
                        password, Main.user.getRole());
                try {
                    Main.client.saveUserChanges(firstName, lastName, email, userName, password);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //скрываем предыдущее окно
                saveButton.getScene().getWindow().hide();
            }

        });


    }

}

