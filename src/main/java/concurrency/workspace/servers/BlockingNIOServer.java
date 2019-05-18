package concurrency.workspace.servers;

import concurrency.workspace.servers.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.Executors;

public class BlockingNIOServer {

    public static void main(String[] args) throws IOException {
        final var handler = new ExecutorServiceHandler<>(
            new PrintingHandler<>(new BlockingChannelHandler(new TransmogrifyChannelHandler())),
            Executors.newFixedThreadPool(10)
        );

        try (var ssc = ServerSocketChannel.open()) {
            ssc.bind(new InetSocketAddress(8080));
            System.out.println("Listening on localhost:" + 8080);

            while (true) {
                final var socket = ssc.accept();
                handler.accept(socket);
            }
        }
    }

}
