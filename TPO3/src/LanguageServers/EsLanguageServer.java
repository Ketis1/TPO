package LanguageServers;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class EsLanguageServer {
    protected static Map<String,String> translations;

    final static String thisServerLanguageCode ="Es";
    public static void main(String[] args) {
        loadTranslations();
        try {
            int port = 1700;
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println(thisServerLanguageCode +"LanguageServer nasłuchuje na porcie " + port + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nowe połączenie: " + clientSocket);
                new Thread(new EsClientHandler(clientSocket)).start();
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
                if (!line.trim().isEmpty() && line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    translations.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
class EsClientHandler implements Runnable {
    private Socket clientSocket;

    public EsClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String request = in.readLine();
            System.out.println("["+ EsLanguageServer.thisServerLanguageCode +"LanguageServer]"+"Otrzymano zapytanie: " + request);

            String[] parts = request.split(",");
            String wordToTranslate=parts[0].trim();
            String clientAddress=parts[1].trim();
            int clientPort = Integer.parseInt(parts[2].trim());

            String result=EsLanguageServer.translations.get(wordToTranslate);

            Socket responseSocket = new Socket(clientAddress, clientPort);
            PrintWriter responseOut = new PrintWriter(responseSocket.getOutputStream(), true);
            responseOut.println(result);
            responseSocket.close();

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}