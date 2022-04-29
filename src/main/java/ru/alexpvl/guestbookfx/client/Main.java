package ru.alexpvl.guestbookfx.client;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.alexpvl.guestbookfx.client.bean.Message;
import ru.alexpvl.guestbookfx.client.bean.User;

public class Main extends Application {
    public static boolean isConnected = false;
    public static User user;
    public static Client client;
    public static Message message;

    @Override
    public void start(Stage stage) throws Exception {

    }

    public static void setup(String[] args) {
        Application.launch(args);
        if (isConnected) {
            //client.disconnect();
        }
    }
}
