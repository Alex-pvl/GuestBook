package client;

import client.bean.Message;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import client.bean.User;

public class Main extends Application {
    public static boolean isConnected = false;
    public static User user;
    public static Client client;
    public static Message message;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("C:\\Users\\Александр\\Java\\GuestBookFX\\GuestBookFX\\src\\main\\java\\ru\\alexpvl\\guestbookfx\\client\\views\\SignIn.fxml"));
        stage.setTitle("Авторизация");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();

    }

    public static void setup(String[] args) {
        Application.launch(args);
        if (isConnected) {
            client.disconnect();
        }
    }
}
