package concurrency.workspace.servers;

import concurrency.workspace.servers.handlers.*;

import java.io.IOException;
import java.net.ServerSocket;

public class MultiThreadedServer {

    public static void main(String[] args) throws IOException {
        final var handler = new ThreadedHandler(new PrintingHandler<>(new TransmogrifyHandler()));

        try (var serverSocket = new ServerSocket(8080)) {
            System.out.println("Listening on localhost:" + 8080);
            while (true) {
                final var socket = serverSocket.accept();
                handler.accept(socket);
            }
        }
    }

}
