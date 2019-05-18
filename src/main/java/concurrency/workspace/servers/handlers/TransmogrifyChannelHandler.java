package concurrency.workspace.servers.handlers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

import static concurrency.workspace.servers.util.Util.transmogrify;

public class TransmogrifyChannelHandler implements Consumer<SocketChannel> {

    @Override
    public void accept(SocketChannel socket) {
        try {
            var buf = ByteBuffer.allocateDirect(80);
            int read = socket.read(buf);
            if (read == -1) {
                socket.close();
                return;
            }
            if (read > 0) {
                transmogrify(buf);
                while (buf.hasRemaining()) {
                    socket.write(buf);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
