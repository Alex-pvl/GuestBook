package controllers;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;

public class InfoController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Hyperlink poster;

    @FXML
    void initialize() {
        poster.setOnAction(e -> {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(new URI("https://novat.nsk.ru/afisha/performances/"));
                } catch (IOException | URISyntaxException exp1) {
                    exp1.printStackTrace();
                }
            }
        });
    }
}
