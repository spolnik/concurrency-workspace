package concurrency.workspace.servers.handlers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

import static concurrency.workspace.servers.util.Util.transmogrify;

public class ReadHandler implements Consumer<SelectionKey> {

    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    public ReadHandler(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    @Override
    public void accept(SelectionKey selectionKey) {
        try {
            var sc = (SocketChannel) selectionKey.channel();
            var buf = ByteBuffer.allocateDirect(80);
            var read = sc.read(buf);
            if (read == -1) {
                pendingData.remove(sc);
                sc.close();
                System.err.println("Disconnected from " + sc + "(in read())");
                return;
            }
            if (read > 0) {
                transmogrify(buf);
                pendingData.get(sc).add(buf);
                selectionKey.interestOps(SelectionKey.OP_WRITE);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
