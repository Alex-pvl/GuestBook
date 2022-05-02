import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final String DB_USERNAME = "postgres"; //вход в базу данных
    private static final String DB_PASSWORD = "sa3862930ha";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";

    static ExecutorService executeIt = Executors.newFixedThreadPool(3);
    static Vector<Map.Entry<Integer, ServerThread>> clients = new Vector<Map.Entry<Integer, ServerThread>>();
    public static Connection connection;

    private static ServerSocket server;
    private static Socket client;
    private static boolean isEnabled = false;

    public void disable() throws IOException {
        isEnabled = false;
        System.out.println("Server disabled");
        client.close();
        server.close();
        executeIt.shutdownNow();
    }

    public void enable() throws IOException {
        isEnabled = true;
        System.out.println("Server enabled");
    }

    public static void main(String[] args) {
        Server mainServer = new Server();
        try {
            mainServer.enable();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // стартуем сервер на порту 3345 и инициализируем переменную для обработки консольных команд с самого сервера
        try (ServerSocket server1 = new ServerSocket(8083);

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            server = server1;
            try {
                connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                System.out.println("Server connected to database");
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            System.out.println("Server socket created, command console reader for listen to server commands");
            int i = 0;
            // стартуем цикл при условии что серверный сокет не закрыт
            while (!server.isClosed() && isEnabled) {

                // проверяем поступившие комманды из консоли сервера если такие
                // были
                if (br.ready()) {
                    System.out.println("Main Server found any messages in channel, let's look at them.");

                    // если команда - quit то инициализируем закрытие сервера и
                    // выход из цикла раздачии нитей монопоточных серверов
                    String serverCommand = br.readLine();
                    if (serverCommand.equalsIgnoreCase("quit")) {
                        System.out.println("Main Server initiate exiting...");
                        server.close();
                        break;
                    }
                }

                // если комманд от сервера нет то становимся в ожидание
                // подключения к сокету общения под именем - "clientDialog" на
                // серверной стороне
                client = server.accept();

                // после получения запроса на подключение сервер создаёт сокет
                // для общения с клиентом и отправляет его в отдельную нить
                // в Runnable(при необходимости можно создать Callable)
                // монопоточную нить = сервер - ServerThread и тот
                // продолжает общение от лица сервера
                var pair = new AbstractMap.SimpleEntry<>(++i,new ServerThread(client,i));
                clients.add(pair);
                executeIt.execute(clients.lastElement().getValue());
                System.out.print("Connection accepted.\n");
            }

            // закрытие пула нитей после завершения работы всех нитей

            executeIt.shutdown();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}