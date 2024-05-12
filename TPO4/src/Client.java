import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client extends JFrame {

    private SocketChannel channel;
    private JTextArea chatArea;
    private JTextField messageField;
    private Charset charset = Charset.forName("ISO-8859-2");

    public Client() {
        super("Client");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        messageField.addActionListener(e -> sendMessage());
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);

        try {
            connectToServer();
            startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectToServer() throws IOException {
        channel = SocketChannel.open(new InetSocketAddress("localhost", 12345));
        System.out.println("Connected to server.");
    }

    private void startListening() {
        new Thread(() -> {
            try {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                while (true) {
                    buffer.clear();
                    int readBytes = channel.read(buffer);
                    if (readBytes > 0) {
                        buffer.flip();
                        CharBuffer charBuffer = charset.decode(buffer);
                        String message = charBuffer.toString();
                        appendMessage(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            try {
                ByteBuffer buffer = charset.encode(message + "\n");
                channel.write(buffer);
                messageField.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }
}
