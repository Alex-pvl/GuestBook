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
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "sa3862930ha";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";

    static ExecutorService executeIt = Executors.newFixedThreadPool(5);
    static Vector<Map.Entry<Integer, ServerThread>> clients = new Vector<Map.Entry<Integer, ServerThread>>();
    public static Connection connection;

    private static ServerSocket server;
    private static Socket client;
    private static boolean isEnabled = false;

    public void enable() throws IOException {
        isEnabled = true;
        System.out.println("Server started");
    }

    public static void main(String[] args) {
        Server mainServer = new Server();
        try {
            mainServer.enable();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (ServerSocket server1 = new ServerSocket(8083);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            server = server1;
            try {
                connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            int i = 0;
            while (!server.isClosed() && isEnabled) {
                if (br.ready()) {
                    String serverCommand = br.readLine();
                    if (serverCommand.equalsIgnoreCase("quit")) {
                        server.close();
                        break;
                    }
                }
                client = server.accept();
                var pair = new AbstractMap.SimpleEntry<>(++i,new ServerThread(client,i));
                clients.add(pair);
                executeIt.execute(clients.lastElement().getValue());
                System.out.print("Connection accepted.\n");
            }
            executeIt.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}