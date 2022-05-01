import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractMap;

import static java.lang.Integer.parseInt;

public class ServerThread implements Runnable {
    private Socket clientDialog;
    ObjectOutputStream oout;
    ObjectInputStream oin;
    private static Server server;
    int number;
    boolean isConnected = false;

    public ServerThread(Socket client, int number) {
        this.number = number;
        clientDialog = client;
        isConnected = true;
        try {
            oin = new ObjectInputStream(clientDialog.getInputStream());
            oout = new ObjectOutputStream(clientDialog.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            // инициируем каналы общения в сокете, для сервера

            // канал записи в сокет следует инициализировать сначала канал чтения для избежания блокировки выполнения
            // программы на ожидании заголовка в сокете

            System.out.println("ObjectInputStream created");

            System.out.println("ObjectOutputStream  created");
            String msgout = "";
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // основная рабочая часть //
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // начинаем диалог с подключенным клиентом в цикле, пока сокет не
            // закрыт клиентом
            while (!clientDialog.isClosed()) {
                System.out.println("Server reading from channel");

                // серверная нить ждёт в канале чтения (inputstream) получения
                // данных клиента после получения данных считывает их
                String entry = oin.readUTF();

                // и выводит в консоль
                System.out.println("READ from clientDialog message - " + entry);


                //отключение клиента
                if (entry.equalsIgnoreCase("quit")) {
                    // если кодовое слово получено то инициализируется закрытие
                    // серверной нити
                    System.out.println("Client initialize connections suicide ...");
                    oout.writeUTF("quit");
                    oout.flush();
                    //Thread.sleep(2000);
                    break;
                }
                //регистрация пользователя
                else if (entry.equalsIgnoreCase("register")) {
                    entry = oin.readUTF();
                    var user_data = entry.split(" ");
                    String sql = "insert into users (firstname, lastname, login, email, password, role)" +
                            "VALUES (?, ?, ?, ?, ?, 0);";
                    PreparedStatement preparedStatement = server.connection.prepareStatement(sql);
                    preparedStatement.setString(1,user_data[0]);
                    preparedStatement.setString(2,user_data[1]);
                    preparedStatement.setString(3,user_data[2]);
                    preparedStatement.setString(4,user_data[3]);
                    preparedStatement.setString(5,user_data[4]);
                    preparedStatement.executeUpdate();
                    oout.writeUTF("register success");
                    oout.flush();
                }
                //авторизация пользователя
                else if (entry.equalsIgnoreCase("authorize")) {
                    entry = oin.readUTF();
                    var user_data = entry.split(" ");
                    String sql = "select id,role from users where (login = ? or email = ?) and password =?;";
                    PreparedStatement preparedStatement = server.connection.prepareStatement(sql);
                    preparedStatement.setString(1,user_data[0]);
                    preparedStatement.setString(2,user_data[0]);
                    preparedStatement.setString(3,user_data[1]);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        oout.writeUTF("authorize success");
                        int user_id = resultSet.getInt("id");
                        int role_id = resultSet.getInt("role");
                        oout.writeUTF(user_id + " " + role_id);
                        oout.flush();
                        System.out.println("authorize success");
                    }
                    else {
                        oout.writeUTF("authorize isn't success");
                        oout.flush();
                        System.out.println("authorize isn't success");
                    }
                }
                //получение данных пользователя
                else if (entry.equalsIgnoreCase("get user info")) {
                    entry = oin.readUTF();
                    var user_data = entry.split(" ");
                    String sql = "select * from users where (login = ? or email = ?) and password =?;";
                    PreparedStatement preparedStatement = server.connection.prepareStatement(sql);
                    preparedStatement.setString(1,user_data[0]);
                    preparedStatement.setString(2,user_data[0]);
                    preparedStatement.setString(3,user_data[1]);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        oout.writeUTF("get user info success");
                        oout.flush();
                        String id = resultSet.getString("id");
                        String firstName = resultSet.getString("firstname");
                        String lastName = resultSet.getString("lastname");
                        String userName = resultSet.getString("login");
                        String email = resultSet.getString("email");
                        String password = resultSet.getString("password");
                        String role_id = resultSet.getString("role");
                        oout.writeUTF(id + " " + firstName + " " +
                                lastName + " " + userName + " " +
                                email + " " + password + " " + role_id);
                        oout.flush();
                        System.out.println("get user info success");
                    }
                    else {
                        oout.writeUTF("get user info isn't success");
                        oout.flush();
                        System.out.println("get user info isn't success");
                    }
                }
                //сохранение новых данных пользователя
                else if (entry.equalsIgnoreCase("change user info")) {
                    entry = oin.readUTF();
                    var user_data = entry.split(" ");

                    String sql = "update users set firstname = ?, " +
                            "lastname = ?, login = ?, " +
                            "email = ?, password = ? " +
                            "where (id = ?);";
                    PreparedStatement preparedStatement = server.connection.prepareStatement(sql);
                    preparedStatement.setString(1,user_data[0]);
                    preparedStatement.setString(2,user_data[1]);
                    preparedStatement.setString(3,user_data[2]);
                    preparedStatement.setString(4,user_data[3]);
                    preparedStatement.setString(5,user_data[4]);
                    preparedStatement.setInt(6, parseInt(user_data[5]));
                    preparedStatement.executeUpdate();
                    oout.writeUTF("change user info success");
                    oout.flush();
                }
                //сохранение нового сообщения в базу данных
                else if (entry.equalsIgnoreCase("send new message")){
                    String email = oin.readUTF();
                    String message = oin.readUTF();
                    String sql = "insert into messages (email, message) VALUES (?,?);";
                    PreparedStatement preparedStatement = server.connection.prepareStatement(sql);
                    preparedStatement.setString(1,email);
                    preparedStatement.setString(2,message);
                    preparedStatement.executeUpdate();
                    oout.writeUTF("save message success");
                    oout.flush();
                }
                //получение данных о количестве строк в таблице
                else if (entry.equalsIgnoreCase("get count of table")){
                    String table = oin.readUTF();
                    String sql = "select count(*) from " + table + ";";
                    System.out.println(sql);
                    Statement statement = server.connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    String count = "-1";
                    while (resultSet.next()){
                        count =  resultSet.getString("count");
                    }
                    oout.writeUTF("get count of table success");
                    oout.flush();
                    oout.writeUTF(count);
                    oout.flush();
                }
                //получение всех отзывов в порядке убывания даты
                else if (entry.equalsIgnoreCase("get all comments")){
                    oout.writeUTF("get all comments success");
                    String sql = "select * from messages order by date desc";
                    Statement statement = server.connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    while (resultSet.next()) {
                        String id = resultSet.getString("id");
                        String email = resultSet.getString("email");
                        String date = resultSet.getString("date");
                        String comment = resultSet.getString("message");
                        oout.writeUTF(id);
                        oout.flush();
                        oout.writeUTF(email);
                        oout.flush();
                        oout.writeUTF(comment);
                        oout.flush();
                        oout.writeUTF(date);
                        oout.flush();
                    }
                }
                //удаление выбранной записи из таблицы
                else if (entry.equalsIgnoreCase("delete row")){
                    String id = oin.readUTF();
                    String table = oin.readUTF();
                    String sql = "delete from "+ table +" where id = ?;";
                    PreparedStatement preparedStatement = server.connection.prepareStatement(sql);
                    preparedStatement.setInt(1, parseInt(id));
                    preparedStatement.executeUpdate();
                    oout.writeUTF("delete row success");
                    oout.flush();
                }
                //изменение данных в записях комментариев
                else if (entry.equalsIgnoreCase("change comment")){
                    String id = oin.readUTF();
                    String email = oin.readUTF();
                    String comment = oin.readUTF();
                    String sql = "update messages set email = ?, message = ? where id = ?;";
                    PreparedStatement preparedStatement = server.connection.prepareStatement(sql);
                    preparedStatement.setString(1,email);
                    preparedStatement.setString(2,comment);
                    preparedStatement.setInt(3, parseInt(id));
                    preparedStatement.executeUpdate();
                    oout.writeUTF("change comment success");
                    oout.flush();
                }
                //получение всех аккаунтов из базы данных
                else if (entry.equalsIgnoreCase("get all users")) {
                    oout.writeUTF("get all users success");
                    oout.flush();
                    String sql = "select * from users;";
                    Statement statement = server.connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    while (resultSet.next()) {
                        String id = String.valueOf(resultSet.getInt("id"));
                        String userName = resultSet.getString("login");
                        String email = resultSet.getString("email");
                        String role_id = String.valueOf(resultSet.getInt("role"));
                        oout.writeUTF(id);
                        oout.flush();
                        oout.writeUTF(userName);
                        oout.flush();
                        oout.writeUTF(email);
                        oout.flush();
                        oout.writeUTF(role_id);
                        oout.flush();
                    }
                }
                //изменение роли у пользователей
                else if (entry.equalsIgnoreCase("change role")) {
                    String id = oin.readUTF();
                    String role_id = oin.readUTF();
                    String sql = "update users set role = ? where id = ?;";
                    PreparedStatement preparedStatement = server.connection.prepareStatement(sql);
                    preparedStatement.setInt(1, parseInt(role_id));
                    preparedStatement.setInt(2, parseInt(id));
                    preparedStatement.executeUpdate();
                    oout.writeUTF("change role success");
                    oout.flush();
                }
                //проверка на то, что в базе есть аккаунт с данной почтой или никнеймом
                else if (entry.equalsIgnoreCase("is used")){
                    String tableField = oin.readUTF();
                    String value = oin.readUTF();
                    String sql = "select count(" + tableField + ") from users where " + tableField + " = '" + value + "';";
                    Statement statement = server.connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    while(resultSet.next()) {
                        int count = resultSet.getInt("count");
                        oout.writeUTF(String.valueOf(count));
                        oout.flush();
                    }
                }
            }
            // освобождаем буфер сетевых сообщений

            // возвращаемся в началло для считывания нового сообщения


            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // основная рабочая часть //
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // если условие выхода - верно выключаем соединения
            System.out.println("Client disconnected");
            System.out.println("Closing connections & channels.");

            // закрываем сначала каналы сокета !
            oin.close();
            oout.close();

            // потом закрываем сокет общения с клиентом в нити моносервера
            clientDialog.close();

            System.out.println("Closing connections & channels - DONE.");

            server.clients.removeElement(new AbstractMap.SimpleEntry<>(number, this));


        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

}
