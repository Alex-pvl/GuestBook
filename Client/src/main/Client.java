package main;

import controllers.UsersController.Account;
import bean.User;
import controllers.GuestbookController.Note;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client extends Thread {
    private boolean isEnabled = false;
    ObjectOutputStream out;
    ObjectInputStream in;
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
                out.writeUTF("quit");
                out.flush();
                System.out.println("Client quit");
            }
            disable();
        }
        catch(IOException e) {
            isConnected = false;
            isEnabled = false;
            System.out.println("Клиент отключился");
        }


    }

    public int registerAccount(String firstName, String lastName, String userName, String password, String email) throws IOException, InterruptedException {
        if (isConnected) {
            String clientCommand = "register";
            out.writeUTF(clientCommand);
            out.flush();
            out.writeUTF(firstName + " " + lastName + " " + userName + " " + email + " " + password);
            out.flush();
            Thread.sleep(500);
            String str = in.readUTF();
            if (str.equalsIgnoreCase("register success")) {
                System.out.println(str);
                return 1;
            }
        }
        return -1;
    }

    public String loginAccount(String login, String password) throws IOException, InterruptedException {
        if (isConnected) {
            String clientCommand = "authorize";
            out.writeUTF(clientCommand);
            out.flush();
            out.writeUTF(login + " " + password);
            out.flush();
            Thread.sleep(500);
            String str = in.readUTF();
            if (str.equalsIgnoreCase("authorize success")) {
                System.out.println(str);
                str = in.readUTF();
                return str;
            } else if (str.equalsIgnoreCase("authorize isn't success")) {
                return str;
            }
        }
        return "-1";
    }

    public int sendMessage(String email, String message) throws IOException, InterruptedException {
        if (isConnected) {
            String clientCommand = "send new message";
            out.writeUTF(clientCommand);
            out.flush();
            out.writeUTF(email);
            out.flush();
            out.writeUTF(message);
            out.flush();
            Thread.sleep(500);
            String str = in.readUTF();
            if (str.equalsIgnoreCase("save message success")) {
                System.out.println(str);
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
            out.writeUTF(clientCommand);
            out.flush();
            out.writeUTF(login + " " + password);
            out.flush();
            Thread.sleep(500);
            String str = in.readUTF();
            if (str.equalsIgnoreCase("get user info success")){
                String data = in.readUTF();
                var user_data = data.split(" ");
                Main.user = new User(Integer.parseInt(user_data[0]), user_data[1],
                        user_data[2], user_data[3], user_data[4],
                        user_data[5], Integer.parseInt(user_data[6]));
            }
        }
    }

    public List<Note> getAllComments (int dataSize) throws IOException, InterruptedException {
       List<Note> data = new ArrayList<>();
       if (isConnected) {
          String clientCommand = "get all messages";
          out.writeUTF(clientCommand);
          out.flush();
          Thread.sleep(500);
          String str = in.readUTF();
          int i = 0;
          if (str.equalsIgnoreCase("get all messages success")){
              System.out.println(str);
              while (i < dataSize) {
                  String entry = in.readUTF();
                  int id =  Integer.parseInt(entry);
                  String email = in.readUTF();
                  String date = in.readUTF();
                  String message = in.readUTF();
                  data.add(new Note(id,email,date,message));
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
        out.writeUTF(clientCommand);
        out.flush();
        out.writeUTF(firstName + " " + lastName + " " + userName
                + " " + email + " " + password + " " + Main.user.getId());
        out.flush();
        Thread.sleep(500);
        String str = in.readUTF();
        if (str.equalsIgnoreCase("change user info success")){
            System.out.println(str);
        }
        else {
            System.out.println("change userInfo isn't success");
        }
    }

    public int getCountOfTable(String comments) throws IOException, InterruptedException {
        String clientCommand = "get count of table";
        out.writeUTF(clientCommand);
        out.flush();
        out.writeUTF(comments);
        out.flush();
        Thread.sleep(500);
        String str = in.readUTF();
        if (str.equalsIgnoreCase("get count of table success")){
            String data = in.readUTF();
            System.out.println(str);
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
            out.writeUTF(clientCommand);
            out.flush();
            out.writeUTF(String.valueOf(id));
            out.flush();
            out.writeUTF(table);
            out.flush();
            Thread.sleep(500);
            String str = in.readUTF();
            if (str.equalsIgnoreCase("delete row success")){
                System.out.println(str);
            }
            else {
                System.out.println("delete row isn't success");
            }
        }
    }

    public void saveCommentChange(int id, String comment) throws IOException, InterruptedException {
        if (isConnected){
            String clientCommand = "change message";
            out.writeUTF(clientCommand);
            out.flush();
            out.writeUTF(String.valueOf(id));
            out.flush();
            out.writeUTF(comment);
            out.flush();
            Thread.sleep(500);
            String str = in.readUTF();
            if (str.equalsIgnoreCase("change message success")){
                System.out.println(str);
            }
            else {
                System.out.println("change message isn't success");
            }
        }
    }

    public List<Account> getAllUsers(int dataSize) throws IOException, InterruptedException {
        List<Account> data = new ArrayList<>();
        if (isConnected) {
            String clientCommand = "get all users";
            out.writeUTF(clientCommand);
            out.flush();
            Thread.sleep(500);
            String str = in.readUTF();
            int i = 0;
            if (str.equalsIgnoreCase("get all users success")){
                System.out.println(str);
                while (i < dataSize) {
                    String entry = in.readUTF();
                    int id =  Integer.parseInt(entry);
                    String userName = in.readUTF();
                    String email = in.readUTF();
                    int role_id = Integer.parseInt(in.readUTF());
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
            out.writeUTF(clientCommand);
            out.flush();
            out.writeUTF(String.valueOf(id));
            out.flush();
            out.writeUTF(String.valueOf(role_id));
            out.flush();
            String str = in.readUTF();
            if (str.equalsIgnoreCase("change role success")){
                System.out.println(str);
            } else {
                System.out.println("change role isn't success");
            }
        }
    }

    public boolean isUsed(String tableField, String value) throws IOException, InterruptedException {
        String clientCommand = "is used";
        out.writeUTF(clientCommand);
        out.flush();
        out.writeUTF(tableField);
        out.flush();
        out.writeUTF(value);
        out.flush();
        Thread.sleep(500);
        int _value = Integer.parseInt(in.readUTF());
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
                socket = new Socket("127.0.0.1", 8083);
                try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream in = new ObjectInputStream(socket.getInputStream());) {
                    this.out = out;
                    this.in = in;
                    isConnected = true;
                    String str = "";
                    System.out.println("Connected");
                    while (!socket.isOutputShutdown() && isEnabled) {

                    }
                    isEnabled = false;
                } catch (IOException ee) {
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
            }
        }
    }
}


