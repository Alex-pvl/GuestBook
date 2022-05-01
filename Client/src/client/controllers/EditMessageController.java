package client.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import client.main.Main;

public class EditMessageController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label idLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private TextArea emailField;

    @FXML
    private TextArea commentField;

    @FXML
    private Button saveButton;

    private String email;

    private String comment;

    private static final String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    void init() {
        emailField.setStyle("-fx-border-color: grey");
        commentField.setStyle("-fx-border-color: grey");
    }

    @FXML
    void initialize() {
        init();
        idLabel.setText(String.valueOf(Main.message.getId()));
        dateLabel.setText(Main.message.getDate());
        emailField.setText(Main.message.getEmail());
        commentField.setText(Main.message.getMessage());

        saveButton.setOnAction(actionEvent -> {
            init();
            email = emailField.getText();
            comment = commentField.getText();
            boolean isFieldUsed = false;
            if (!(email.isEmpty() || comment.isEmpty())) {
                isFieldUsed = true;
            } else {
                if (email.isEmpty()) {
                    emailField.setStyle("-fx-border-color: red");
                    emailField.setPromptText("Введите соответсвующее поле!");
                } else {
                    emailField.setStyle("-fx-border-color: grey");
                    emailField.setPromptText("Введите E-Mail...");
                }
                if (comment.isEmpty()) {
                    commentField.setStyle("-fx-border-color: red");
                    commentField.setPromptText("Введите соответсвующее поле!");
                } else {
                    commentField.setStyle("-fx-border-color: grey");
                    commentField.setPromptText("Введите комментарий...");
                }
            }
            boolean isEmailValid = false;
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                isEmailValid = true;
            } else {
                emailField.setStyle("-fx-border-color:red");
                emailField.clear();
                emailField.setPromptText("Неправильно введён E-Mail!");
            }
            if (Main.isConnected && isFieldUsed && isEmailValid){
                try {
                    Main.client.saveCommentChange(Main.message.getId(), email, comment);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                saveButton.getScene().getWindow().hide();
            }
        });

    }
}
