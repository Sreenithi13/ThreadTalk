import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ThreadTalkServer {

    private static int port = 1234;

    private static ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> undeliveredMessages = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Listening on port " + port);

            while (true) {
                Socket socket = server.accept();

                System.out.println("Client has connected...");
                ClientHandler clientHandler = new ClientHandler(socket);

                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Server error. Exiting...");
            System.exit(1);
        }
    }

    public static void addClient(String username, ClientHandler client) {
        clients.put(username, client);
        client.broadcast(username, " has joined in the chat");
    }

    public static void removeClient(String username, ClientHandler client) {
        clients.remove(username, client);
        client.broadcast(username, " has left the chat");
    }

    public static ConcurrentHashMap<String, ClientHandler> getClients() {
        return clients;
    }

    public static ClientHandler getTargetClient(String username) {
        return clients.get(username);
    }

    public static void addundeliveredMessage(String recipientUsername, String message) {
        undeliveredMessages.putIfAbsent(recipientUsername, new ConcurrentLinkedQueue<>());
        undeliveredMessages.get(recipientUsername).add(message);
    }

    public static ConcurrentLinkedQueue<String> getandRemoveUndeliveredMessage(String username) {
        return undeliveredMessages.remove(username);
    }
}
