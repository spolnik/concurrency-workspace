package concurrency.workspace.servers;

import concurrency.workspace.servers.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

public class SelectorWithWorkPoolNonBlockingServer {

    public static void main(String[] args) throws IOException {

        try (var ssc = ServerSocketChannel.open()) {
            ssc.bind(new InetSocketAddress(8080));
            ssc.configureBlocking(false);
            final var selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Listening on localhost:" + 8080);

            final var pool = Executors.newFixedThreadPool(10);
            final Map<SocketChannel, Queue<ByteBuffer>> pendingData = new ConcurrentHashMap<>();
            final Queue<Runnable> selectorActions = new ConcurrentLinkedQueue<>();

            final var acceptHandler = new AcceptHandler(pendingData);
            final var readHandler = new PooledReadHandler(pool, selectorActions, pendingData);
            final var writeHandler = new WriteHandler(pendingData);

            while (true) {
                selector.select();
                processSelectorActions(selectorActions);
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

    private static void processSelectorActions(Queue<Runnable> selectorActions) {
        Runnable action;
        while ((action = selectorActions.poll()) != null) {
            action.run();
        }
    }
}
