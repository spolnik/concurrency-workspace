package concurrency.workspace.servers.handlers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import static concurrency.workspace.servers.util.Util.transmogrify;

public class PooledReadHandler implements Consumer<SelectionKey> {

    private final ExecutorService pool;
    private final Queue<Runnable> selectorActions;
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    public PooledReadHandler(ExecutorService pool, Queue<Runnable> selectorActions, Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pool = pool;
        this.selectorActions = selectorActions;
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
                System.err.println("Disconnected from " + sc + "(in pooledRead())");
                return;
            }
            if (read > 0) {
                pool.submit(() -> {
                    transmogrify(buf);
                    pendingData.get(sc).add(buf);
                    selectorActions.add(() -> selectionKey.interestOps(SelectionKey.OP_WRITE));
                    selectionKey.selector().wakeup();
                });
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
