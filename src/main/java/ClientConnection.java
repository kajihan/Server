import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class ClientConnection implements Runnable {
    private final String clientName;
    private final Socket socket;
    private PrintWriter out;

    public ClientConnection(String clientName, Socket socket) {
        this.clientName = clientName;
        this.socket = socket;

        try {
            new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Error in ClientConnection constructor");
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter ignored = new PrintWriter(socket.getOutputStream(), true)) {

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received from " + clientName + ": " + message);

                if (message.equals("-exit")) {
                    // Handle client disconnection.
                    System.out.println("[SERVER] " + clientName + " was disconnected on " + new Date());
                    break;
                } else if (message.startsWith("-file")) {
                    // Handle file transfer.
                    String[] parts = message.split(" ");
                    if (parts.length >= 2) {
                        String filePath = parts[1];
                        receiveFile(filePath);
                    }
                } else {
                    // Broadcast messages to all connected clients.
                    Server.broadcastMessage("[" + clientName + "] " + message);
                }
            }
        } catch (IOException e) {
            System.out.println("Error with ClientConnection of " + clientName + " -> " + e.getMessage());
        } finally {
            try {
                socket.close();
                Server.removeConnection(this);
            } catch (IOException e) {
                System.out.println("Error while closing connection to " + clientName + " -> " + e.getMessage());
            }
        }
    }

    void receiveFile(String filePath) {
        File serverFile = new File("C:\\FilesFromClients\\" + new File(filePath).getName());

        try (InputStream fileInputStream = socket.getInputStream();
             OutputStream fileOutputStream = new FileOutputStream(serverFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            out.println("File " + serverFile.getName() + " has been received and saved on the server");
            System.out.println("File received and saved: " + serverFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error while receiving file" + e.getMessage());
        }
    }
}
