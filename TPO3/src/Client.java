import java.io.*;
import java.net.*;
public class Client {
    private static TranslatorClient translatorClient;
    public static void main(String[] args) {
        try {
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Podaj slowo do przetlumaczecznia:");
            String wordToTranslate = userInput.readLine().trim();
            System.out.println("Podaj kod jezyka");
            String languageCode = userInput.readLine().trim().toLowerCase();
            System.out.println(translatorClient.translate(wordToTranslate,languageCode));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


//        final String serverAddress = "127.0.0.1";
//        final int serverPort = 1234;
//        final int clientPort = 5555;
//
//        try {
//            Socket socket = new Socket(serverAddress, serverPort);
//            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
//            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//
//            System.out.println("Podaj slowo do przetlumaczecznia:");
//            String wordToTranslate = userInput.readLine().trim();
//            System.out.println("Podaj kod jezyka");
//            String languageCode = userInput.readLine().trim().toLowerCase();
//
//
//            out.println(wordToTranslate + "," + languageCode + "," + clientPort);
//
//            ServerSocket responseSocket = new ServerSocket(clientPort);
//
//            Socket clientResponseSocket = responseSocket.accept();
//            BufferedReader in = new BufferedReader(new InputStreamReader(clientResponseSocket.getInputStream()));
//            String response = in.readLine();
//            System.out.println("Odpowiedź od serwera: " + response);
//
//            clientResponseSocket.close();
//            responseSocket.close();
//            socket.close();
//
//
//
//        }catch (IOException e) {
//            e.printStackTrace();
//        }
    }




}
