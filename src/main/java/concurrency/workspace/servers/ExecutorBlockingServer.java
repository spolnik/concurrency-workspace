package concurrency.workspace.servers;

import concurrency.workspace.servers.handlers.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;

public class ExecutorBlockingServer {

    public static void main(String[] args) throws IOException {
        final var handler = new ExecutorServiceHandler<>(
            new PrintingHandler<>(new TransmogrifyHandler()),
            Executors.newFixedThreadPool(10)
        );

        try (var serverSocket = new ServerSocket(8080)) {
            System.out.println("Listening on localhost:" + 8080);
            while (true) {
                final var socket = serverSocket.accept();
                handler.accept(socket);
            }
        } finally {
            Executors.newFixedThreadPool(10).shutdownNow();
        }
    }

}
