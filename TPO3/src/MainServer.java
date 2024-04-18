import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;



public class MainServer {


    public static void main(String[] args) {
        try {
            int port = 1234;
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

            String request = in.readLine();
            System.out.println("Otrzymano zapytanie: " + request);


            String[] parts = request.split(",");
            String wordToTranslate = parts[0].trim();
            String languageCode = parts[1].trim();
            int clientsPort = Integer.parseInt(parts[2].trim());

            int languageServerPort;
            if (languageCode.equals("en")) {
                languageServerPort = 1500;
            } else if (languageCode.equals("de")) {
                languageServerPort = 1600;
            } else if (languageCode.equals("es")) {
                languageServerPort = 1700;
            } else {
                out.println("Błąd: Niepoprawna operacja.");
                return;
            }


            String clientAddress = clientSocket.getInetAddress().getHostAddress();


            Socket serverSocket = new Socket("localhost", languageServerPort);
            PrintWriter serverOut = new PrintWriter(serverSocket.getOutputStream(), true);

            serverOut.println(wordToTranslate + "," + clientAddress + "," + clientsPort);




            serverSocket.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
