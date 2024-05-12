import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class AdminClient extends JFrame {

    private SocketChannel channel;
    private JTextField commandField;
    private JTextArea logArea;
    private Charset charset = Charset.forName("ISO-8859-2");

    public AdminClient() {
        super("Admin Client");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        commandField = new JTextField();
        commandField.addActionListener(e -> sendCommand());
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendCommand());
        inputPanel.add(commandField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);

        try {
            connectToServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectToServer() throws IOException {
        channel = SocketChannel.open(new InetSocketAddress("localhost", 12345));
        System.out.println("Connected to server.");
    }

    private void sendCommand() {
        String command = commandField.getText();
        if (!command.isEmpty()) {
            try {
                ByteBuffer buffer = charset.encode(command + "\n");
                channel.write(buffer);
                commandField.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminClient::new);
    }
}
