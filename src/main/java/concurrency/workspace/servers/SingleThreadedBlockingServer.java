package concurrency.workspace.servers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SingleThreadedBlockingServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        try (var serverSocket = new ServerSocket(PORT)) {
            System.out.println("Listening on localhost:" + PORT);
            while (true) {
                var socket = serverSocket.accept();
                handle(socket);
            }
        }
    }

    private static void handle(Socket socket) throws IOException {
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
        }

        System.err.println("Disconnected from " + socket);
    }

    private static int transmogrify(int data) {
        return Character.isLetter(data) ? data ^ ' ' : data;
    }
}
