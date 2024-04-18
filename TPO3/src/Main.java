import LanguageServers.DeLanguageServer;
import LanguageServers.EnLanguageServer;
import LanguageServers.EsLanguageServer;

public class Main {
    public static void main(String[] args) {
        Thread threadMainServer = new Thread(() -> {
            MainServer.main(args);
        });

        Thread threadDe = new Thread(() -> {
            DeLanguageServer.main(args);
        });

        Thread threadEn = new Thread(() -> {
            EnLanguageServer.main(args);
        });

        Thread threadEs = new Thread(() -> {
            EsLanguageServer.main(args);
        });

        Thread threadClient = new Thread(() -> {
            Client.main(args);
        });

        threadMainServer.start();
        threadDe.start();
        threadEn.start();
        threadEs.start();
        threadClient.start();
    }
}