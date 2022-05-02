package main;

import bean.Message;
import bean.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static boolean isConnected = false;
    public static Client client;
    public static User user;
    public static Message comment;
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/SignIn.fxml"));
        primaryStage.setTitle("Авторизация");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void setup(String[] args){
        Application.launch(args);
        if (isConnected) {
            client.disconnect();
        }
    }
}