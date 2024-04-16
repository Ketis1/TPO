import java.io.*;
import java.net.*;
public class Client {
    public static void main(String[] args) {
        final String serverAddress = "localhost"; // Adres serwera głównego
        final int serverPort = 1234; // Port serwera głównego
        final int clientPort = 5555; // Port, na którym klient oczekuje na odpowiedź

        try {
            // Utworzenie połączenia z serwerem głównym
            Socket socket = new Socket(serverAddress, serverPort);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Podaj slowo do przetlumaczecznia:");
            String wordToTranslate = userInput.readLine().trim();
            System.out.println("Podaj kod jezyka");
            String languageCode = userInput.readLine().trim().toLowerCase();


            // Wysłanie zapytania do serwera głównego
            out.println(wordToTranslate + "," + languageCode + "," + clientPort);

            // Odczytanie odpowiedzi od serwera pomocniczego
            ServerSocket responseSocket = new ServerSocket(clientPort);

            Socket clientResponseSocket = responseSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientResponseSocket.getInputStream()));
            String response = in.readLine();
            System.out.println("Odpowiedź od serwera: " + response);

            // Zamknięcie połączenia
            clientResponseSocket.close();
            responseSocket.close();
            socket.close();
            while (true){

            }


        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
