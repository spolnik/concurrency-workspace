package concurrency.workspace.servers.handlers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

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
            buf.compact();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void transmogrify(ByteBuffer buf) {
        buf.flip();
        for (int i = 0; i< buf.limit(); i++) {
            buf.put(i, (byte) transmogrify(buf.get(i)));
        }
    }

    private int transmogrify(int data) {
        return Character.isLetter(data) ? data ^ ' ' : data;
    }
}
