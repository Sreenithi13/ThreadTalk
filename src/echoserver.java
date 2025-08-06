import java.io.*;
import java.net.*;

public class echoserver {
    public static void main(String[] args) {
        int port = 1234; // port to listen on
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Echo server is running on port " + port);

            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                out.println("Echo: " + inputLine); // send message back to client
            }

            System.out.println("Client disconnected.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
