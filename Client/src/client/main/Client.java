package client.main;

import client.controllers.UsersController;
import client.bean.User;
import client.controllers.GuestbookController.Note;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Client extends Thread {
    private boolean isEnabled = false;
    private boolean isConnected = false;
    ObjectOutputStream out;
    ObjectInputStream in;
    static Socket socket;

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

    public void disconnect() {
        try {
            if (isConnected) {
                out.writeUTF("quit");
                out.flush();
                System.out.println("Client disconnected");
            }
            disable();
        } catch (IOException e) {
            isConnected = false;
            isEnabled = false;
            System.out.println("Exception: Client disconnected");
        }
    }

    public int registerUser(String firstName, String lastName, String login,
                            String password, String email) {
        if (isConnected) {
            String str = null;
            try {
                out.writeUTF("register");
                out.flush();
                out.writeUTF(firstName + " " + lastName + " " +
                        login + " " + email + " " + password);
                Thread.sleep(500);
                str = in.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (str.equalsIgnoreCase("register success")) {
                System.out.println(str);
                return 1;
            }
        }
        return -1;
    }

    public String loginUser(String login, String password) {
        if (isConnected) {
            try {
                out.writeUTF("authorize");
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
            } catch (IOException e) {
                System.out.println("Client exception");
            } catch (InterruptedException e) {
                System.out.println("Client exception");
            }
        }
        return "-1";
    }

    public int sendMessage(String email, String message) {
        if (isConnected) {
            String str = null;
            try {
                String clientCommand = "send new message";
                out.writeUTF(clientCommand);
                out.flush();
                out.writeUTF(email);
                out.flush();
                out.writeUTF(message);
                out.flush();
                Thread.sleep(500);
                str = in.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

    public void setUserInfo(String login, String password) {
        if (isConnected) {
            try {
                out.writeUTF("get user info");
                out.flush();
                out.writeUTF(login + " " + password);
                out.flush();
                Thread.sleep(500);
                String msign = in.readUTF();
                if (msign.equalsIgnoreCase("get user info success")){
                    String data = in.readUTF();
                    var user_data = data.split(" ");
                    Main.user = new User(Integer.parseInt(user_data[0]), user_data[1],
                            user_data[2], user_data[3], user_data[4],
                            user_data[5], Integer.parseInt(user_data[6]));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public List<Note> getAllComments (int dataSize){
        List<Note> data = new ArrayList<>();
        if (isConnected) {
            try {
                out.writeUTF("get all comments");
                out.flush();
                Thread.sleep(500);
                String str = in.readUTF();
                int i = 0;
                if (str.equalsIgnoreCase("get all comments success")){
                    System.out.println(str);
                    while (i < dataSize) {
                        String entry = in.readUTF();
                        int id =  Integer.parseInt(entry);
                        String email = in.readUTF();
                        String message = in.readUTF();
                        String date = in.readUTF();
                        data.add(new Note(id,email,date,message));
                        i++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public void saveUserChanges(String firstName, String lastName,
                                String userName, String email, String password) {
        String str = null;
        try {
            out.writeUTF("change user info");
            out.flush();
            out.writeUTF(firstName + " " + lastName + " " + userName
                    + " " + email + " " + password + " " + Main.user.getId());
            out.flush();
            Thread.sleep(500);
            str = in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (str.equalsIgnoreCase("change user info success")){
            System.out.println(str);
        }
        else {
            System.out.println("change user info isn't success");
        }
    }

    public int getCountOfTable(String comments) {
        try {
            out.writeUTF("get count of table");
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void deleteRowFromTable(int id, String table) {
        if (isConnected) {
            String str = null;
            try {
                out.writeUTF("delete row");
                out.flush();
                out.writeUTF(String.valueOf(id));
                out.flush();
                out.writeUTF(table);
                out.flush();
                Thread.sleep(500);
                str = in.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (str.equalsIgnoreCase("delete row success")){
                System.out.println(str);
            }
            else {
                System.out.println("delete row isn't success");
            }
        }
    }

    public void saveCommentChange(int id, String email, String comment) {
        if (isConnected){
            String str = null;
            try {
                out.writeUTF("change comment");
                out.flush();
                out.writeUTF(String.valueOf(id));
                out.flush();
                out.writeUTF(email);
                out.flush();
                out.writeUTF(comment);
                out.flush();
                Thread.sleep(500);
                str = in.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (str.equalsIgnoreCase("change comment success")){
                System.out.println(str);
            }
            else {
                System.out.println("change comment isn't success");
            }
        }
    }

    public List<UsersController.Account> getAllUsers(int dataSize) {
        List<UsersController.Account> data = new ArrayList<>();
        if (isConnected) {
            try {
                out.writeUTF("get all users");
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
                        if (role_id == 0) {
                            role = "Пользователь";
                        }
                        else if (role_id == 1){
                            role = "Администратор";
                        }
                        else {
                            role = "Неизвестная ошибка";
                        }
                        data.add(new UsersController.Account(id,userName,email,role));
                        i++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public void changeRoleId(int id, int role_id) {
        if (isConnected) {
            String str = null;
            try {
                out.writeUTF("change role");
                out.flush();
                out.writeUTF(String.valueOf(id));
                out.flush();
                out.writeUTF(String.valueOf(role_id));
                out.flush();
                str = in.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (str.equalsIgnoreCase("change role success")){
                System.out.println(str);
            } else {
                System.out.println("change role isn't success");
            }
        }
    }

    public boolean isUsed(String tableField, String value) {
        int _value = 0;
        try {
            out.writeUTF("is used");
            out.flush();
            out.writeUTF(tableField);
            out.flush();
            out.writeUTF(value);
            out.flush();
            Thread.sleep(500);
            _value = Integer.parseInt(in.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
                socket = new Socket("localhost", 8083);
                try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream in = new ObjectInputStream(socket.getInputStream());) {
                    this.out = out;
                    this.in = in;
                    isConnected = true;
                    String str = "";
                    System.out.println("Connected");
                    while (!socket.isOutputShutdown() && isEnabled) {
                        //что здесь??????????
                        //моно тред отправляет quit можно проеверять отправлен ли quit
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
