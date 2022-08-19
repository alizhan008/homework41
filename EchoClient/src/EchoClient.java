import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EchoClient {
    private final int port;
    private final String host;

    private EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static EchoClient connectTo(int port) {
        String localhost = "127.0.0.1";
        return new EchoClient(localhost, port);
    }

    public void run() {

        try (Socket socket = new Socket(host, port)) {
            Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);

            try (PrintWriter writer = new PrintWriter(socket.getOutputStream())) {
                while (true) {
                    System.out.printf("Напишите 'bye' чтобы выйти%n");
                    System.out.print("Введите любой текст: ");
                    String message = sc.nextLine();
                    writer.write(message);
                    writer.write(System.lineSeparator());

                    writer.flush();
                    if ("bye".equalsIgnoreCase(message)) {
                        return;
                    }

                    InputStream input = socket.getInputStream();
                    InputStreamReader isr = new InputStreamReader(input);
                    Scanner scr = new Scanner(isr);
                    String msg = scr.nextLine().strip();
                    System.out.printf("Server replay: %s%n%n", msg);
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("Connection dropped!");
        } catch (IOException e) {
            System.out.printf("Can't connect to %s:%s !%n", host, port);
            e.printStackTrace();
        }
    }
}
