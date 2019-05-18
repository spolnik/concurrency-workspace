package concurrency.workspace.servers;

import concurrency.workspace.servers.handlers.PrintingHandler;
import concurrency.workspace.servers.handlers.TransmogrifyHandler;

import java.io.IOException;
import java.net.ServerSocket;

public class SingleThreadedServer {

    public static void main(String[] args) throws IOException {
        final var handler = new PrintingHandler<>(new TransmogrifyHandler());

        try (var serverSocket = new ServerSocket(8080)) {
            System.out.println("Listening on localhost:" + 8080);

            while (true) {
                final var socket = serverSocket.accept();
                handler.accept(socket);
            }
        }
    }

}
