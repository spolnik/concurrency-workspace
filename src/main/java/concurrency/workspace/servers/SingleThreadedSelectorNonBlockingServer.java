package concurrency.workspace.servers;

import concurrency.workspace.servers.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class SingleThreadedSelectorNonBlockingServer {

    public static void main(String[] args) throws IOException {

        try (var ssc = ServerSocketChannel.open()) {
            ssc.bind(new InetSocketAddress(8080));
            ssc.configureBlocking(false);
            final var selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Listening on localhost:" + 8080);

            final Map<SocketChannel, Queue<ByteBuffer>> pendingData = new HashMap<>();
            final var acceptHandler = new AcceptHandler(pendingData);
            final var readHandler = new ReadHandler(pendingData);
            final var writeHandler = new WriteHandler(pendingData);

            while (true) {
                selector.select();
                final var keys = selector.selectedKeys();
                for (Iterator<SelectionKey> it = keys.iterator(); it.hasNext(); ) {
                    SelectionKey key = it.next();
                    it.remove();
                    if (key.isValid()) {
                        if (key.isAcceptable()) {
                            acceptHandler.accept(key);
                        } else if (key.isReadable()) {
                            readHandler.accept(key);
                        } else if (key.isWritable()) {
                            writeHandler.accept(key);
                        }
                    }
                }
            }
        }
    }
}
