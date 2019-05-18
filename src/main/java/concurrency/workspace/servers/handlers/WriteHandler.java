package concurrency.workspace.servers.handlers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

import static concurrency.workspace.servers.util.Util.transmogrify;

public class WriteHandler implements Consumer<SelectionKey> {

    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    public WriteHandler(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    @Override
    public void accept(SelectionKey selectionKey) {
        try {
            var sc = (SocketChannel) selectionKey.channel();
            var queue = pendingData.get(sc);
            while (!queue.isEmpty()) {
                var buf = queue.peek();
                int written = sc.write(buf);
                if (written == -1) {
                    sc.close();
                    pendingData.remove(sc);
                    return;
                }
                if (buf.hasRemaining()) return;
                queue.remove();
            }
            selectionKey.interestOps(SelectionKey.OP_READ);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
