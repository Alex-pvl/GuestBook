import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.AbstractMap;

import static java.lang.Integer.parseInt;

public class ServerThread implements Runnable {

    private Socket clientDialog;
    ObjectOutputStream out;
    ObjectInputStream in;
    private static Server server;
    int number;
    boolean isConnected = false;

    public ServerThread(Socket client, int number) {
        this.number = number;
        clientDialog = client;
        isConnected = true;
        try {
            in = new ObjectInputStream(clientDialog.getInputStream());
            out = new ObjectOutputStream(clientDialog.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String msg_out = "";
            while (!clientDialog.isClosed()) {
                System.out.println("Server reading from channel");
                String entry = in.readUTF();
                System.out.println("read msg: " + entry);
                //отключение клиента
                if (entry.equalsIgnoreCase("quit")) {
                    System.out.println("quit");
                    out.writeUTF("quit");
                    out.flush();
                    break;
                }
                //регистрация пользователя
                else if (entry.equalsIgnoreCase("register")) {
                    entry = in.readUTF();
                    String[] user_data = entry.split(" ");
                    String sql = "INSERT INTO users (firstname, lastname, username, email, password, role) VALUES (?, ?, ?, ?, ?, 1);";
                        PreparedStatement preparedStatement = server.connection.prepareStatement(sql);
                        preparedStatement.setString(1,user_data[0]);
                        preparedStatement.setString(2,user_data[1]);
                        preparedStatement.setString(3,user_data[2]);
                        preparedStatement.setString(4,user_data[3]);
                        preparedStatement.setString(5,user_data[4]);
                        preparedStatement.executeUpdate();
                    out.writeUTF("register success");
                    out.flush();
                }
                //авторизация пользователя
                else if (entry.equalsIgnoreCase("authorize")) {
                    entry = in.readUTF();
                    String[] user_data = entry.split(" ");
                    String sql = "SELECT id,role FROM users WHERE (username = ? OR email = ?) AND password =?;";
                    PreparedStatement preparedStatement = server.connection.prepareStatement(sql);
                    preparedStatement.setString(1,user_data[0]);
                    preparedStatement.setString(2,user_data[0]);
                    preparedStatement.setString(3,user_data[1]);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        out.writeUTF("authorize success");
                        int user_id = resultSet.getInt("id");
                        int role_id = resultSet.getInt("role");
                        out.writeUTF(user_id + " " + role_id);
                        out.flush();
                        System.out.println("authorize success");
                    }
                    else {
                        out.writeUTF("authorize isn't success");
                        out.flush();
                        System.out.println("authorize isn't success");
                    }
                }
                //получение данных пользователя
                else if (entry.equalsIgnoreCase("get user info")) {
                    entry = in.readUTF();
                    var user_data = entry.split(" ");
                    String sql = "SELECT * FROM users WHERE (username = ? OR email = ?) AND password =?;";
                    PreparedStatement preparedStatement = server.connection.prepareStatement(sql);
                    preparedStatement.setString(1,user_data[0]);
                    preparedStatement.setString(2,user_data[0]);
                    preparedStatement.setString(3,user_data[1]);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        out.writeUTF("get user info success");
                        out.flush();
                        String id = resultSet.getString("id");
                        String firstName = resultSet.getString("firstname");
                        String lastName = resultSet.getString("lastname");
                        String userName = resultSet.getString("username");
                        String email = resultSet.getString("email");
                        String password = resultSet.getString("password");
                        String role_id = resultSet.getString("role");
                        out.writeUTF(id + " " + firstName + " " + lastName + " " + userName + " " + email + " " + password + " " + role_id);
                        out.flush();
                        System.out.println("get user info success");
                    }
                    else {
                        out.writeUTF("get user info isn't success");
                        out.flush();
                        System.out.println("get user info isn't success");
                    }
                }
                //сохранение новых данных пользователя
                else if (entry.equalsIgnoreCase("change user info")) {
                    entry = in.readUTF();
                    String[] user_data = entry.split(" ");
                    String sql = "UPDATE users SET firstname = ?, lastname = ?, username = ?, email = ?, password = ? WHERE (id = ?);";
                    PreparedStatement preparedStatement = server.connection.prepareStatement(sql);
                    preparedStatement.setString(1,user_data[0]);
                    preparedStatement.setString(2,user_data[1]);
                    preparedStatement.setString(3,user_data[2]);
                    preparedStatement.setString(4,user_data[3]);
                    preparedStatement.setString(5,user_data[4]);
                    preparedStatement.setInt(6, parseInt(user_data[5]));
                    preparedStatement.executeUpdate();
                    out.writeUTF("change user info success");
                    out.flush();
                }
                //сохранение нового сообщения в базу данных
                else if (entry.equalsIgnoreCase("send new message")){
                    String email = in.readUTF();
                    String message = in.readUTF();
                    String sql = "INSERT INTO messages (email, message) VALUES (?,?);";
                    PreparedStatement preparedStatement = server.connection.prepareStatement(sql);
                    preparedStatement.setString(1,email);
                    preparedStatement.setString(2,message);
                    preparedStatement.executeUpdate();
                    out.writeUTF("save message success");
                    out.flush();
                }
                //получение данных о количестве строк в таблице
                else if (entry.equalsIgnoreCase("get count of table")){
                    String table = in.readUTF();
                    String sql = "SELECT COUNT(*) FROM " + table + ";";
                    System.out.println(sql);
                    Statement statement = server.connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    String count = "-1";
                    while (resultSet.next()){
                       count =  resultSet.getString("count");
                    }
                    out.writeUTF("get count of table success");
                    out.flush();
                    out.writeUTF(count);
                    out.flush();
                }
                //получение всех отзывов в порядке убывания даты
                else if (entry.equalsIgnoreCase("get all messages")){
                    out.writeUTF("get all messages success");
                    String sql = "SELECT * FROM messages ORDER BY date DESC";
                    Statement statement = server.connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    while (resultSet.next()) {
                        String id = resultSet.getString("id");
                        String email = resultSet.getString("email");
                        String date = resultSet.getString("date");
                        String comment = resultSet.getString("message");
                        out.writeUTF(id);
                        out.flush();
                        out.writeUTF(email);
                        out.flush();
                        out.writeUTF(date);
                        out.flush();
                        out.writeUTF(comment);
                        out.flush();
                    }
                }
                //удаление выбранной записи из таблицы
                else if (entry.equalsIgnoreCase("delete row")){
                    String id = in.readUTF();
                    String table = in.readUTF();
                    String sql = "DELETE FROM "+ table +" WHERE id = ?;";
                    PreparedStatement preparedStatement = server.connection.prepareStatement(sql);
                    preparedStatement.setInt(1, parseInt(id));
                    preparedStatement.executeUpdate();
                    out.writeUTF("delete row success");
                    out.flush();
                }
                //изменение данных в записях комментариев
                else if (entry.equalsIgnoreCase("change message")){
                    String id = in.readUTF();
                    String comment = in.readUTF();
                    String sql = "UPDATE messages SET message = ? WHERE id = ?;";
                    PreparedStatement preparedStatement = server.connection.prepareStatement(sql);
                    preparedStatement.setString(1,comment);
                    preparedStatement.setInt(2,Integer.parseInt(id));
                    preparedStatement.executeUpdate();
                    out.writeUTF("change message success");
                    out.flush();
                }
                //получение всех аккаунтов из базы данных
                else if (entry.equalsIgnoreCase("get all users")) {
                    out.writeUTF("get all users success");
                    out.flush();
                    String sql = "SELECT * FROM users;";
                    Statement statement = server.connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    while (resultSet.next()) {
                        String id = String.valueOf(resultSet.getInt("id"));
                        String userName = resultSet.getString("username");
                        String email = resultSet.getString("email");
                        String role_id = String.valueOf(resultSet.getInt("role"));
                        out.writeUTF(id);
                        out.flush();
                        out.writeUTF(userName);
                        out.flush();
                        out.writeUTF(email);
                        out.flush();
                        out.writeUTF(role_id);
                        out.flush();
                    }
                }
                //изменение роли у пользователей
                else if (entry.equalsIgnoreCase("change role")) {
                    String id = in.readUTF();
                    String role_id = in.readUTF();
                    String sql = "UPDATE users SET role = ? WHERE id = ?;";
                    PreparedStatement preparedStatement = server.connection.prepareStatement(sql);
                    preparedStatement.setInt(1,Integer.parseInt(role_id));
                    preparedStatement.setInt(2,Integer.parseInt(id));
                    preparedStatement.executeUpdate();
                    out.writeUTF("change role success");
                    out.flush();
                }
                //проверка на то, что в базе есть аккаунт с данной почтой или никнеймом
                else if (entry.equalsIgnoreCase("is used")){
                    String tableField = in.readUTF();
                    String value = in.readUTF();
                    String sql = "SELECT COUNT(" + tableField + ") FROM users WHERE " + tableField + " = '" + value + "';";
                    Statement statement = server.connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    while(resultSet.next()) {
                        int count = resultSet.getInt("count");
                        out.writeUTF(String.valueOf(count));
                        out.flush();
                    }
                }
            }
            in.close();
            out.close();
            clientDialog.close();
            server.clients.removeElement(new AbstractMap.SimpleEntry<>(number, this));
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
