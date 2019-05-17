package concurrency.workspace.servers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadedBlockingServer {

    private static final int PORT = 8081;

    public static void main(String[] args) throws IOException {
        try (var serverSocket = new ServerSocket(PORT)) {
            System.out.println("Listening on localhost:" + PORT);
            while (true) {
                var socket = serverSocket.accept();
                handle(socket);
            }
        }
    }

    private static void handle(Socket socket) {
        new Thread(() -> {
            System.err.println("Connected to " + socket);
            try (
                socket;
                var in = socket.getInputStream();
                var out = socket.getOutputStream()
            ) {
                int data;
                while ((data = in.read()) != -1) {
                    out.write(transmogrify(data));
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } finally {
                System.err.println("Disconnected from " + socket);
            }
        }).start();
    }

    private static int transmogrify(int data) {
        return Character.isLetter(data) ? data ^ ' ' : data;
    }
}
