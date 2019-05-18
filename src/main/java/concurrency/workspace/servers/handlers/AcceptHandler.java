package concurrency.workspace.servers.handlers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.function.Consumer;

public class AcceptHandler implements Consumer<SelectionKey> {

    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    public AcceptHandler(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    @Override
    public void accept(SelectionKey selectionKey) {
        var ssc = (ServerSocketChannel) selectionKey.channel();
        try {
            var sc = ssc.accept();
            System.err.println("Connected to " + sc);
            pendingData.put(sc, new ArrayDeque<>());
            sc.configureBlocking(false);
            sc.register(selectionKey.selector(), SelectionKey.OP_READ);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
