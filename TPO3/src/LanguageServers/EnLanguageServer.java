package LanguageServers;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class EnLanguageServer {
    protected static Map<String,String> translations;
    public static void main(String[] args) {
        loadTranslations();
        try {
            int port = 1500; // port serwera
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("EnLanguageServer nasłuchuje na porcie " + port + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nowe połączenie: " + clientSocket);
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void loadTranslations(){
        translations = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("src/dicts/en_translations.properties"))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Sprawdzamy, czy linia nie jest pusta i czy zawiera znak równości
                if (!line.trim().isEmpty() && line.contains("=")) {
                    // Dzielimy linię na klucz i wartość
                    String[] parts = line.split("=", 2);
                    // Dodajemy do mapy
                    translations.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

            String request = in.readLine(); // odczytanie zapytania od serwera głównego
            System.out.println("Otrzymano zapytanie: " + request);

            // Parsowanie zapytania
            String[] parts = request.split(",");
            String wordToTranslate=parts[0].trim();
            String clientAddress=parts[1].trim();
            int clientPort = Integer.parseInt(parts[2].trim());

            // Obliczenie wyniku
            String result=EnLanguageServer.translations.get(wordToTranslate);

            // Wysłanie odpowiedzi do klienta
            Socket responseSocket = new Socket(clientAddress, clientPort);
            PrintWriter responseOut = new PrintWriter(responseSocket.getOutputStream(), true);
            responseOut.println(result);
            responseSocket.close(); // zamknięcie połączenia z klientem

            // Zamknięcie połączenia
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}