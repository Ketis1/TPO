import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;

public class Server {

    private ServerSocketChannel serverChannel;
    private Selector selector;
    private Map<SocketChannel, Set<String>> subscriptions = new HashMap<>();
    private Map<String, Set<SocketChannel>> topicSubscribers = new HashMap<>();
    private Charset charset = Charset.forName("ISO-8859-2");
    private SocketChannel adminChannel;
    private String adminPassword = "password";

    public Server() {
        try {
            startServer();
            handleConnections();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startServer() throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress("localhost", 12345));
        serverChannel.configureBlocking(false);

        selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server started. Waiting for connections...");
    }

    private void handleConnections() throws IOException {
        while (true) {
            selector.select();

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    acceptClient(key);
                } else if (key.isReadable()) {
                    readMessage(key);
                }
            }
        }
    }

    private void acceptClient(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        subscriptions.put(clientChannel, new HashSet<>());

        System.out.println("New client connected: " + clientChannel.getRemoteAddress());
    }

    private void readMessage(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        StringBuilder messageBuilder = new StringBuilder();

        int bytesRead;
        while ((bytesRead = clientChannel.read(buffer)) > 0) {
            buffer.flip();
            CharBuffer charBuffer = charset.decode(buffer);
            messageBuilder.append(charBuffer);
            buffer.clear();
        }

        String message = messageBuilder.toString().trim();
        System.out.println("Received from " + clientChannel.getRemoteAddress() + ": " + message);

        processMessage(clientChannel, message);
    }

    private void processMessage(SocketChannel clientChannel, String message) throws IOException {
        String[] parts = message.split(" ", 3);
        String command = parts[0];
        String payload = parts.length > 1 ? parts[1] : "";
        String content = parts.length > 2 ? parts[2] : "";

        switch (command.toLowerCase()) {
            case "subscribe":
                subscribeToTopic(clientChannel, payload);
                break;
            case "unsubscribe":
                unsubscribeFromTopic(clientChannel, payload);
                break;
            case "message":
                if (clientChannel == adminChannel) {
                    sendMessageToTopic(payload, content);
                } else {
                    System.out.println("Unauthorized attempt to send message from " + clientChannel.getRemoteAddress());
                }
                break;
            case "admin":
                authenticateAdmin(clientChannel, payload);
                break;
            default:
                System.out.println("Unknown command from client: " + command);
        }
    }

    private void subscribeToTopic(SocketChannel clientChannel, String topic) throws IOException {
        subscriptions.get(clientChannel).add(topic);
        topicSubscribers.computeIfAbsent(topic, k -> new HashSet<>()).add(clientChannel);
        System.out.println(clientChannel.getRemoteAddress() + " subscribed to topic: " + topic);
    }

    private void unsubscribeFromTopic(SocketChannel clientChannel, String topic) throws IOException {
        subscriptions.get(clientChannel).remove(topic);
        Set<SocketChannel> subscribers = topicSubscribers.get(topic);
        if (subscribers != null) {
            subscribers.remove(clientChannel);
            System.out.println(clientChannel.getRemoteAddress() + " unsubscribed from topic: " + topic);
        }
    }

    private void authenticateAdmin(SocketChannel clientChannel, String password) throws IOException {
        if (password.equals(adminPassword)) {
            adminChannel = clientChannel;
            System.out.println("Admin authenticated from: " + adminChannel.getRemoteAddress());
            adminChannel.write(charset.encode("You are now authenticated as admin.\n"));
        } else {
            System.out.println("Admin authentication failed from: " + clientChannel.getRemoteAddress());
            clientChannel.write(charset.encode("Authentication failed. Access denied.\n"));
        }
    }

    private void sendMessageToTopic(String topic, String message) throws IOException {
        Set<SocketChannel> subscribers = topicSubscribers.get(topic);
        if (subscribers != null) {
            ByteBuffer buffer = charset.encode("[" + topic + "] " + message);

            for (SocketChannel subscriber : subscribers) {
                subscriber.write(buffer);
                buffer.rewind();
            }
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
