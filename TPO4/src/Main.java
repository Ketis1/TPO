public class Main {
    public static void main(String[] args) {
        Thread server = new Thread(() -> {
            Server.main(args);
        });

        Thread admin = new Thread(() -> {
            AdminClient.main(args);
        });

        Thread c1 = new Thread(() -> {
            Client.main(args);
        });

        Thread c2 = new Thread(() -> {
            Client.main(args);
        });



        server.start();
        admin.start();
        c1.start();
        c2.start();

    }
}