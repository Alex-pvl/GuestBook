package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import main.Main;

public class EditMessageController {
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
    private Label emailLabel;

    @FXML
    private TextArea messageText;

    @FXML
    private Button saveBtn;

    void init() {
        messageText.setStyle("-fx-border-color: grey");
    }

    @FXML
    void initialize() {
        init();
        idLabel.setText(String.valueOf(Main.comment.getId()));
        dateLabel.setText(Main.comment.getDate());
        emailLabel.setText(Main.comment.getEmail());
        messageText.setText(Main.comment.getMessage());

        saveBtn.setOnAction(actionEvent -> {
            init();
            message = messageText.getText();
            boolean isFieldUsed = false;
            if (!message.isEmpty()) {
                isFieldUsed = true;
                messageText.setStyle("-fx-border-color: grey");
                messageText.setPromptText("Введите сообщение...");
            } else {
                messageText.setStyle("-fx-border-color: red");
                messageText.setPromptText("Поле не может быть пустым!");
            }
            if (Main.isConnected && isFieldUsed){
                try {
                    Main.client.saveCommentChange(Main.comment.getId(), message);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                saveBtn.getScene().getWindow().hide();
            }
        });

    }
}
