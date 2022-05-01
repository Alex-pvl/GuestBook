package client.main;

import client.controllers.UsersController.Account;
import client.bean.User;
import client.controllers.GuestbookController.Note;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Client extends Thread {
    private boolean isEnabled = false;
    ObjectOutputStream oout;
    ObjectInputStream oin;
    static Socket socket;
    private boolean isConnected = false;

    public boolean isConnected() {
        return isConnected;
    }

    public void disable() {
        isEnabled = false;
        System.out.println("disabled");
    }

    public void enable() {
        isEnabled = true;
        System.out.println("enabled");
    }

    public void disconnect()  {
        try {
            if (isConnected) {
                oout.writeUTF("quit");
                oout.flush();
                System.out.println("Client kill connections");
            }
            disable();
        }
        catch(IOException e) {
            isConnected = false;
            isEnabled = false;
            System.out.println("Клиент разорвал соединение");
        }


    }

    public int registerAccount(String firstName, String lastName, String userName, String password, String email) throws IOException, InterruptedException {
        if (isConnected) {
            String clientCommand = "register";
            oout.writeUTF(clientCommand);
            oout.flush();
            oout.writeUTF(firstName + " " + lastName + " " + userName + " " + email + " " + password);
            oout.flush();
            Thread.sleep(500);
            String msign = oin.readUTF();
            if (msign.equalsIgnoreCase("register success")) {
                System.out.println(msign);
                return 1;
            }
        }
        return -1;
    }

    public String loginAccount(String login, String password) throws IOException, InterruptedException {
        if (isConnected) {
            String clientCommand = "authorize";
            oout.writeUTF(clientCommand);
            oout.flush();
            oout.writeUTF(login + " " + password);
            oout.flush();
            Thread.sleep(500);
            String msign = oin.readUTF();
            if (msign.equalsIgnoreCase("authorize success")) {
                System.out.println(msign);
                msign = oin.readUTF();
                return msign;
            } else if (msign.equalsIgnoreCase("authorize isn't success")) {
                return msign;
            }
        }
        return "-1";
    }

    public int sendMessage(String email, String message) throws IOException, InterruptedException {
        if (isConnected) {
            String clientCommand = "send new message";
            oout.writeUTF(clientCommand);
            oout.flush();
            oout.writeUTF(email);
            oout.flush();
            oout.writeUTF(message);
            oout.flush();
            Thread.sleep(500);
            String msign = oin.readUTF();
            if (msign.equalsIgnoreCase("save message success")) {
                System.out.println(msign);
                return 1;
            }
            else {
                return 0;
            }
        }
        return -1;
    }

    public void setUserInfo(String login, String password) throws IOException, InterruptedException {
        if (isConnected) {
            String clientCommand = "get user info";
            oout.writeUTF(clientCommand);
            oout.flush();
            oout.writeUTF(login + " " + password);
            oout.flush();
            Thread.sleep(500);
            String msign = oin.readUTF();
            if (msign.equalsIgnoreCase("get user info success")){
                String data = oin.readUTF();
                var user_data = data.split(" ");
                Main.user = new User(Integer.parseInt(user_data[0]), user_data[1],
                        user_data[2], user_data[3], user_data[4],
                        user_data[5], Integer.parseInt(user_data[6]));
            }
            System.out.println(Main.user.getId());
            System.out.println(Main.user.getFirstName());
            System.out.println(Main.user.getLastName());
            System.out.println(Main.user.getEmail());
            System.out.println(Main.user.getLogin());
            System.out.println(Main.user.getPassword());
            System.out.println(Main.user.getRole());
        }
    }

    public List<Note> getAllComments (int dataSize) throws IOException, InterruptedException {
        List<Note> data = new ArrayList<>();
        if (isConnected) {
            String clientCommand = "get all messages";
            oout.writeUTF(clientCommand);
            oout.flush();
            Thread.sleep(500);
            String msign = oin.readUTF();
            int i = 0;
            if (msign.equalsIgnoreCase("get all messages success")){
                System.out.println(msign);
                while (i < dataSize) {
                    String entry = oin.readUTF();
                    int id =  Integer.parseInt(entry);
                    String email = oin.readUTF();
                    String message = oin.readUTF();
                    String date = oin.readUTF();
                    data.add(new Note(id,email,message, date));
                    i++;
                }
            }
        }
        return data;
    }

    public void saveUserChanges(String firstName, String lastName,
                                String userName, String email, String password)
            throws IOException, InterruptedException {
        String clientCommand = "change user info";
        oout.writeUTF(clientCommand);
        oout.flush();
        oout.writeUTF(firstName + " " + lastName + " " + email
                + " " + userName + " " + password + " " + Main.user.getId());
        oout.flush();
        Thread.sleep(500);
        String msign = oin.readUTF();
        if (msign.equalsIgnoreCase("change user info success")){
            System.out.println(msign);
        }
        else {
            System.out.println("change user info isn't success");
        }
    }

    public int getCountOfTable(String comments) throws IOException, InterruptedException {
        String clientCommand = "get count of table";
        oout.writeUTF(clientCommand);
        oout.flush();
        oout.writeUTF(comments);
        oout.flush();
        Thread.sleep(500);
        String msign = oin.readUTF();
        if (msign.equalsIgnoreCase("get count of table success")){
            String data = oin.readUTF();
            System.out.println(msign);
            return Integer.parseInt(data);
        }
        else {
            System.out.println("get count of table isn't success");
            return -1;
        }
    }

    public void deleteRowFromTable(int id, String table) throws IOException, InterruptedException {
        if (isConnected) {
            String clientCommand = "delete row";
            oout.writeUTF(clientCommand);
            oout.flush();
            oout.writeUTF(String.valueOf(id));
            oout.flush();
            oout.writeUTF(table);
            oout.flush();
            Thread.sleep(500);
            String msign = oin.readUTF();
            if (msign.equalsIgnoreCase("delete row success")){
                System.out.println(msign);
            }
            else {
                System.out.println("delete row isn't success");
            }
        }
    }

    public void saveCommentChange(int id, String email, String comment) throws IOException, InterruptedException {
        if (isConnected){
            String clientCommand = "change comment";
            oout.writeUTF(clientCommand);
            oout.flush();
            oout.writeUTF(String.valueOf(id));
            oout.flush();
            oout.writeUTF(email);
            oout.flush();
            oout.writeUTF(comment);
            oout.flush();
            Thread.sleep(500);
            String msign = oin.readUTF();
            if (msign.equalsIgnoreCase("change comment success")){
                System.out.println(msign);
            }
            else {
                System.out.println("change comment isn't success");
            }
        }
    }

    public List<Account> getAllUsers(int dataSize) throws IOException, InterruptedException {
        List<Account> data = new ArrayList<>();
        if (isConnected) {
            String clientCommand = "get all users";
            oout.writeUTF(clientCommand);
            oout.flush();
            Thread.sleep(500);
            String msign = oin.readUTF();
            int i = 0;
            if (msign.equalsIgnoreCase("get all users success")){
                System.out.println(msign);
                while (i < dataSize) {
                    String entry = oin.readUTF();
                    int id =  Integer.parseInt(entry);
                    String userName = oin.readUTF();
                    String email = oin.readUTF();
                    int role_id = Integer.parseInt(oin.readUTF());
                    String role;
                    if (role_id == 1) {
                        role = "Пользователь";
                    }
                    else if (role_id == 2){
                        role = "Администратор";
                    }
                    else {
                        role = "Неизвестная ошибка";
                    }
                    data.add(new Account(id,userName,email,role));
                    i++;
                }
            }
        }
        return data;
    }

    public void changeRoleId(int id, int role_id) throws IOException {
        if (isConnected) {
            String clientCommand = "change role";
            oout.writeUTF(clientCommand);
            oout.flush();
            oout.writeUTF(String.valueOf(id));
            oout.flush();
            oout.writeUTF(String.valueOf(role_id));
            oout.flush();
            String msign = oin.readUTF();
            if (msign.equalsIgnoreCase("change role success")){
                System.out.println(msign);
            } else {
                System.out.println("change role isn't success");
            }
        }
    }

    public boolean isUsed(String tableField, String value) throws IOException, InterruptedException {
        String clientCommand = "is used";
        oout.writeUTF(clientCommand);
        oout.flush();
        oout.writeUTF(tableField);
        oout.flush();
        oout.writeUTF(value);
        oout.flush();
        Thread.sleep(500);
        int _value = Integer.parseInt(oin.readUTF());
        if (_value != 0){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void run() {
        super.run();
// запускаем подключение сокета по известным координатам и нициализируем приём сообщений с консоли клиента
        while (isEnabled) {
            try {
                socket = new Socket("localhost", 3345);
                try (ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());) {


                    this.oout = oout;
                    this.oin = oin;
                    isConnected = true;
                    String msgin = "";
                    System.out.println("Connected");
                    while (!socket.isOutputShutdown() && isEnabled) {
                        //что здесь??????????
                        //моно тред отправляет quit можно проеверять отправлен ли quit
                    }

                    isEnabled = false;

                } catch (IOException ee) {
                    // TODO Auto-generated catch block
                    ee.printStackTrace();
                }
            } catch (IOException e) {
                isConnected = false;
                System.err.println("Can't connect to server!");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                //interrupt();
            }
        }
    }
}
