import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    static List<ClientConnection> activeConnections = new ArrayList<>();
    private final AtomicInteger clientCounter = new AtomicInteger(0);
    private ServerSocket serverSocket;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Error with server socket" + e.getMessage());
        }
    }

    public static void broadcastMessage(String message) {
        for (ClientConnection connection : activeConnections) {
            connection.sendMessage(message);
        }
    }

    public static void removeConnection(ClientConnection connection) {
        activeConnections.remove(connection);
    }

    public void start() {
        System.out.println("Server is running...");
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                String clientName = generateUniqueClientName();
                ClientConnection clientConnection = new ClientConnection(clientName, clientSocket);
                activeConnections.add(clientConnection);
                new Thread(clientConnection).start();

                String message = "[SERVER] " + clientName + " has connected at " + new Date();
                Server.broadcastMessage(message);
            } catch (IOException e) {
                System.out.println("Error occurred while server starting" + e.getMessage());
                try {
                    serverSocket.close();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                break;
            }
        }
    }

    public String generateUniqueClientName() {
        int clientId = clientCounter.incrementAndGet();
        return "client-" + clientId;
    }
}
