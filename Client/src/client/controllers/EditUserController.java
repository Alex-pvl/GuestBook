package client.controllers;

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
    private String firstName;
    private String lastName;
    private String login;
    private String password;
    private String email;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

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
    private Button saveBtn;

    @FXML
    private Label firstNameError;

    @FXML
    private Label lastNameError;

    @FXML
    private Label loginError;

    @FXML
    private Label emailError;

    @FXML
    private Label passwordError;

    private static final String regex =
            "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    void init() {
        firstNameError.setVisible(false);
        lastNameError.setVisible(false);
        loginError.setVisible(false);
        emailError.setVisible(false);
        passwordError.setVisible(false);
        firstNameText.setStyle("-fx-border-color: grey");
        lastNameText.setStyle("-fx-border-color: grey");
        loginText.setStyle("-fx-border-color: grey");
        emailText.setStyle("-fx-border-color: grey");
        passwordText.setStyle("-fx-border-color: grey");
    }

    @FXML
    void initialize() {
        init();
        firstNameText.setText(Main.user.getFirstName());
        lastNameText.setText(Main.user.getLastName());
        loginText.setText(Main.user.getLogin());
        emailText.setText(Main.user.getEmail());
        passwordText.setText(Main.user.getPassword());

        saveBtn.setOnAction(actionEvent -> {
            firstName = firstNameText.getText();
            lastName = lastNameText.getText();
            login = loginText.getText();
            password = passwordText.getText();
            email = emailText.getText();
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
                emailError.setText("Неправильно введён Email!");
                emailError.setVisible(true);
            }
            isUsedLogin = Main.client.isUsed("login", login);
            isUsedEmail = Main.client.isUsed("email", email);
            if (isUsedLogin && !(loginError.equals(Main.user.getLogin()))) {
                loginText.setStyle("-fx-border-color:red");
                loginError.setText("Логин занят другим пользователем!");
                loginError.setVisible(true);
            } else {
                isUsedLogin = false;
            }
            if (isUsedEmail && !(email.equals(Main.user.getEmail()))) {
                emailText.setStyle("-fx-border-color:red");
                emailError.setText("Email занят другим пользователем!");
                emailError.setVisible(true);
            } else {
                isUsedEmail = false;
            }
            if (Main.isConnected && !(isUsedEmail || isUsedLogin) && isFieldUsed && isEmailValid) {
                Main.user = new User(Main.user.getId(), firstName, lastName, login, email,
                        password, Main.user.getRole());
                Main.client.saveUserChanges(firstName, lastName, login, email, password);
                //скрываем предыдущее окно
                saveBtn.getScene().getWindow().hide();
            }
        });
    }

}

