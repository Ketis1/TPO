package LanguageServers;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class DeLanguageServer {
    protected static Map<String,String> translations;
    String gowno = "["+thisServerLanguageCode +"LanguageServer]";
    final static String thisServerLanguageCode ="De";
    public static void main(String[] args) {
        loadTranslations();
        try {
            int port = 1600; // port serwera
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println(thisServerLanguageCode +"LanguageServer nasłuchuje na porcie " + port + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nowe połączenie: " + clientSocket);
                new Thread(new DeClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void loadTranslations(){
        translations = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("src/dicts/"+ thisServerLanguageCode +"_translations.properties"))) {
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
class DeClientHandler implements Runnable {
    private Socket clientSocket;

    public DeClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String request = in.readLine(); // odczytanie zapytania od serwera głównego
            System.out.println("["+ DeLanguageServer.thisServerLanguageCode +"LanguageServer]"+"Otrzymano zapytanie: " + request);

            // Parsowanie zapytania
            String[] parts = request.split(",");
            String wordToTranslate=parts[0].trim();
            String clientAddress=parts[1].trim();
            int clientPort = Integer.parseInt(parts[2].trim());

            // Obliczenie wyniku
            String result=DeLanguageServer.translations.get(wordToTranslate);

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
