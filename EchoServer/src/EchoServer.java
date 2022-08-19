import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EchoServer {

    private final int port;

    private EchoServer(int port) {
        this.port = port;
    }

    public static EchoServer bindToPort(int port) {
        return new EchoServer(port);
    }

    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            try (Socket clientSocket = server.accept()) {
                handle(clientSocket);
            }
        } catch (IOException e) {
            System.out.printf("Вероятнее всего порт %s занят.%n", port);
            e.printStackTrace();
        }
    }

    private void handle(Socket socket) throws IOException {
        InputStream input = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(input, StandardCharsets.UTF_8);

        try (Scanner sc = new Scanner(isr)) {
            while (true) {
                String message = sc.nextLine().strip();
                System.out.printf("Got: %s%n", message);
                if ("bye".equalsIgnoreCase(message)) {
                    System.out.println("bye bye!");
                    return;
                }
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                writer.write(reverseString(message));
                writer.write(System.lineSeparator());
                writer.flush();
            }
        } catch (NoSuchElementException e) {
            System.out.print("Client dropped the connection");
            e.printStackTrace();
        }
    }
    public static String reverseString(String str) {
        return new StringBuilder(str).reverse().toString();
    }
}
