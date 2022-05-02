package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import main.Main;

public class EditMessageController {
    private String email;
    private String message;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label idLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private TextArea emailText;

    @FXML
    private TextArea messageText;

    @FXML
    private Button saveBtn;

    private static final String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    void init() {
        emailText.setStyle("-fx-border-color: grey");
        messageText.setStyle("-fx-border-color: grey");
    }

    @FXML
    void initialize() {
        init();
        idLabel.setText(String.valueOf(Main.comment.getId()));
        dateLabel.setText(Main.comment.getDate());
        emailText.setText(Main.comment.getEmail());
        messageText.setText(Main.comment.getMessage());


        saveBtn.setOnAction(actionEvent -> {
            init();
            email = emailText.getText();
            message = messageText.getText();
            boolean isFieldUsed = false;
            if (!(email.isEmpty() || message.isEmpty())) {
                isFieldUsed = true;
            } else {
                if (email.isEmpty()) {
                    emailText.setStyle("-fx-border-color: red");
                    emailText.setPromptText("Поле не может быть пустым!");
                } else {
                    emailText.setStyle("-fx-border-color: grey");
                    emailText.setPromptText("Введите Email...");
                }
                if (message.isEmpty()) {
                    messageText.setStyle("-fx-border-color: red");
                    messageText.setPromptText("Поле не может быть пустым!");
                } else {
                    messageText.setStyle("-fx-border-color: grey");
                    messageText.setPromptText("Введите сообщение...");
                }
            }
            boolean isEmailValid = false;
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                isEmailValid = true;
            } else {
                emailText.setStyle("-fx-border-color:red");
                emailText.clear();
                emailText.setPromptText("Email введен некорректно!");
            }
            if (Main.isConnected && isFieldUsed && isEmailValid){
                try {
                    Main.client.saveCommentChange(Main.comment.getId(), email, message);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                saveBtn.getScene().getWindow().hide();
            }
        });

    }
}
