package concurrency.workspace.servers;

import concurrency.workspace.servers.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executors;

public class SingleThreadedNonBlockingPollingServer {

    public static void main(String[] args) throws IOException {
        final var handler = new TransmogrifyChannelHandler();

        try (var ssc = ServerSocketChannel.open()) {
            ssc.bind(new InetSocketAddress(8080));
            ssc.configureBlocking(false);
            System.out.println("Listening on localhost:" + 8080);

            Collection<SocketChannel> sockets = new ArrayList<>();
            while (true) {
                final var sc = ssc.accept();
                if (sc != null) {
                    sockets.add(sc);
                    System.out.println("Connected to " + sc);
                    sc.configureBlocking(false);
                }
                for (var socket : sockets) {
                    if (socket.isConnected()) {
                        handler.accept(socket);
                    }
                }

                sockets.removeIf(socket -> !socket.isConnected());
            }
        }

    }

}
