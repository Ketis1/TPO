import java.io.*;
import java.net.*;

public class TranslatorClient {
    private static final String serverAddress = "127.0.0.1";
    private static final int serverPort = 1234;
    private static final int clientPort = 5555;

    public static String translate(String wordToTranslate, String languageCode) {
        try {
            Socket socket = new Socket(serverAddress, serverPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println(wordToTranslate + "," + languageCode + "," + clientPort);

            ServerSocket responseSocket = new ServerSocket(clientPort);

            Socket clientResponseSocket = responseSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientResponseSocket.getInputStream()));
            String response = in.readLine();

            clientResponseSocket.close();
            responseSocket.close();
            socket.close();

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
