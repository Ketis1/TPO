import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;


public class MainServer {

    final String HOSTNAME = "127.0.0.1";
    static final int port = 8511;
    public static void main(String[] args) {
        try {
            int port = 1234; // port głównego serwera
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("MainServer nasłuchuje na porcie " + port + "...");


            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nowe połączenie: " + clientSocket);
                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException e) {
            System.out.println(e);
        }

    }

    private static void handeClient(Socket clientSocket) {
    }
}
class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String request = in.readLine(); // odczytanie zapytania od klienta
            System.out.println("Otrzymano zapytanie: " + request);

            // Parsowanie zapytania
            String[] parts = request.split(",");
            String wordToTranslate = parts[0].trim();
            String languageCode = parts[1].trim();
            int clientsPort = Integer.parseInt(parts[2].trim());

            int languageServerPort;
            if (languageCode.equals("en")) {
                languageServerPort = 1500; // port serwera dodawania
            } else if (languageCode.equals("de")) {
                languageServerPort = 1600; // port serwera mnożenia
            } else if (languageCode.equals("es")) {
                languageServerPort = 1700; // port serwera potęgowania
            } else {
                out.println("Błąd: Niepoprawna operacja.");
                return;
            }

            // Zapisanie adresu klienta
            String clientAddress = clientSocket.getInetAddress().getHostAddress();

            // Utworzenie połączenia z odpowiednim serwerem pomocniczym
            Socket serverSocket = new Socket("localhost", languageServerPort);
            PrintWriter serverOut = new PrintWriter(serverSocket.getOutputStream(), true);
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));


            serverOut.println(wordToTranslate + "," + clientAddress + "," + clientsPort);




            // Odczytanie odpowiedzi od serwera pomocniczego
            String result = serverIn.readLine();
            System.out.println("Odpowiedź od serwera pomocniczego: " + result);

            // Przekazanie odpowiedzi do klienta
            out.println(result);

            // Zamknięcie połączenia
            serverSocket.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
