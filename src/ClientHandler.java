import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientHandler implements Runnable {

    private Socket socket; // connection
    private BufferedReader in; // reads text from the client
    private PrintWriter out; // sends messages to the client
    protected String username;
    private boolean loggedin;
    private final ConcurrentLinkedQueue<QueueMessage> retryqueue = new ConcurrentLinkedQueue<>();

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.loggedin = false;
    }

    AuthenticationController auth = new AuthenticationController();

    @Override
    public void run() {
        try {
            // Login/signup menu loop
            while (!loggedin) {
                showMenu();
                String choiceStr = in.readLine();

                if (choiceStr == null || choiceStr.trim().isEmpty()) {
                    out.println("Please enter a number from the menu:");
                    out.flush();
                    continue;
                }

                int choice = -1;
                try {
                    choice = Integer.parseInt(choiceStr.trim());
                } catch (NumberFormatException e) {
                    out.println("Invalid input, please enter a number:");
                    out.flush();
                    continue;
                }

                switch (choice) {
                    case 1: login(); break;
                    case 2: signup(); break;
                    case 3:
                        out.println("Exiting. Goodbye!");
                        out.flush();
                        socket.close();
                        return;
                    default:
                        out.println("Invalid choice, please try again:");
                        out.flush();
                        break;
                }
            }

            // After login success
            out.println("Welcome to THREAD TALK");
            out.flush();
            out.println("Commands:");
            out.flush();
            out.println("/all <Message> - public broadcast");
            out.flush();
            out.println("/pm - private message");
            out.flush();
            out.println("/users - show online users");
            out.flush();
            out.println("/logout - logout");
            out.flush();

            String input;
            while ((input = in.readLine()) != null && loggedin) {
                if (input.startsWith("/all")) {
                    out.println("Please enter your message:");
                    out.flush();
                    String message = in.readLine();
                    broadcast(username, message);
                } else if (input.startsWith("/pm")) {
                    sendPrivateMessage();
                } else if (input.equals("/users")) {
                    listOfUsers();
                } else if (input.equals("/logout")) {
                    logout();
                    break;
                } else if (input.equals("/typing")) {
                    broadcastTyping(this.username);
                } else if (input.equals("/stoptyping")) {
                    clearingTypingStatus();
                } else {
                    out.println("Invalid command, please try again:");
                    out.flush();
                }

                sendingMessageFromQueue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void login() throws IOException, NoSuchAlgorithmException {
        out.println("Enter username:");  // added colon
        out.flush();
        String username = in.readLine();

        out.println("Enter password:");  // added colon
        out.flush();
        String password = in.readLine();

        if (auth.login(username, password)) {
            this.username = username;
            this.loggedin = true;
            out.println("Login successful");
            out.flush();
            broadcast(this.username, "Joined the chat");
            ThreadTalkServer.addClient(username, this);

            ConcurrentLinkedQueue<String> pendingMessages = ThreadTalkServer.getandRemoveUndeliveredMessage(this.username);
            if (pendingMessages != null) {
                while (!pendingMessages.isEmpty()) {
                    String message = pendingMessages.poll();
                    out.println(message);
                    out.flush();
                }
            }
        } else {
            out.println("Login failed");
            out.flush();
        }
    }

    private void signup() throws IOException, NoSuchAlgorithmException {
        out.println("Enter username:");  // added colon
        out.flush();
        String username = in.readLine();

        out.println("Enter password:");  // added colon
        out.flush();
        String password = in.readLine();

        out.println("Enter name:");  // added colon
        out.flush();
        String name = in.readLine();

        out.println("Enter age:");  // added colon
        out.flush();
        int age = Integer.parseInt(in.readLine());

        out.println("Enter email:");  // added colon
        out.flush();
        String email = in.readLine();

        if (auth.userexists(username)) {
            out.println("Username already exists.");
            out.flush();
        } else if (auth.signup(username, password, name, age, email)) {
            out.println("Signup successful, please login now.");
            out.flush();
            this.username = username;
        } else {
            out.println("Signup failed.");
            out.flush();
        }
    }

    private void logout() throws IOException, NoSuchAlgorithmException {
        this.loggedin = false;
        broadcast(this.username, "left the chat");
        ThreadTalkServer.removeClient(username, this);
        out.println("You have been logged out.");
        out.flush();
        this.socket.close();
    }

    private void showMenu() {
        out.println("1. Login");
        out.println("2. Signup");
        out.println("3. Exit");
        out.flush();
    }

    public void broadcast(String username, String message) {
        for (ClientHandler client : ThreadTalkServer.getClients().values()) {
            if (!client.username.equals(username)) {
                client.out.println(username + ": " + message);
                client.out.flush();
            }
        }
    }

    private Map<String, String> showPrivatePrompts() throws IOException {
        out.println("Who do you want to send the message to?");  // ends with '?', so OK
        out.flush();
        showClients();
        String recipientUsername = in.readLine();

        out.println("Enter your message:");  // added colon
        out.flush();
        String message = in.readLine();

        Map<String, String> map = new HashMap<>();
        map.put("username", recipientUsername);
        map.put("message", message);
        return map;
    }

    private void showClients() {
        int count = 0;
        for (ClientHandler user : ThreadTalkServer.getClients().values()) {
            out.println(count++ + ". " + user.username);
            out.flush();
        }
    }

    private void sendPrivateMessage() throws IOException {
        Map<String, String> map = showPrivatePrompts();
        String recipientUsername = map.get("username");
        String message = map.get("message");

        ClientHandler recipientClient = ThreadTalkServer.getTargetClient(recipientUsername);

        if (recipientClient == null) {
            out.println("User not connected. Message saved and will be delivered later.");
            out.flush();
            ThreadTalkServer.addundeliveredMessage(recipientUsername, message);
            return;
        }

        if (!recipientClient.loggedin) {
            out.println("The user is currently offline.");
            out.flush();
            ThreadTalkServer.addundeliveredMessage(recipientUsername, message);
            return;
        }

        try {
            recipientClient.out.println("[private - " + this.username + "] " + message);
            recipientClient.out.flush();

            this.out.println("Message sent to " + recipientUsername + ": " + message);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            this.out.println("Error occurred sending message, it will be saved.");
            out.flush();
            ThreadTalkServer.addundeliveredMessage(recipientUsername, message);
        }
    }

    private void sendingMessageFromQueue() {
        while (!retryqueue.isEmpty()) {
            QueueMessage queueMessage = retryqueue.poll();
            if (queueMessage == null) continue;

            ClientHandler client = ThreadTalkServer.getTargetClient(queueMessage.getRecipientusername());

            if (client != null && client.loggedin) {
                client.out.println("Private from " + this.username + ": " + queueMessage.getMessage());
                client.out.flush();
                this.out.println("Message sent to " + queueMessage.getRecipientusername() + ": " + queueMessage.getMessage());
                out.flush();
            } else {
                retryqueue.add(queueMessage);
            }
        }
    }

    public void broadcastTyping(String username) {
        for (ClientHandler client : ThreadTalkServer.getClients().values()) {
            if (!client.username.equals(username)) {
                client.out.println("@typing " + username);
                client.out.flush();
            }
        }
    }

    private void clearingTypingStatus() {
        for (ClientHandler client : ThreadTalkServer.getClients().values()) {
            if (!client.username.equals(this.username)) {
                client.out.println(" "); // clear typing status line
                client.out.flush();
            }
        }
    }

    private void listOfUsers() {
        for (ClientHandler client : ThreadTalkServer.getClients().values()) {
            String status = client.loggedin ? "ONLINE" : "OFFLINE";
            out.printf("%-20s : %s%n", client.username, status);
            out.flush();
        }
    }
}
