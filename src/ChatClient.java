import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ChatClient {

    private JFrame frame;
    private JTextArea chatArea;
    private JTextField inputField;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    public ChatClient(String serverAddress, int port) {
        setupGUI();

        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Thread to listen for messages from server
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        String finalMessage = message;
                        SwingUtilities.invokeLater(() -> chatArea.append(finalMessage + "\n"));
                    }
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> chatArea.append("Disconnected from server.\n"));
                }
            }).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Unable to connect to server: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void setupGUI() {
        frame = new JFrame("Simple Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        inputField = new JTextField();
        frame.add(inputField, BorderLayout.SOUTH);

        // Send message on Enter key
        inputField.addActionListener(e -> {
            String message = inputField.getText().trim();
            if (!message.isEmpty()) {
                out.println(message);
                inputField.setText("");
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClient("localhost", 1234));
    }
}
