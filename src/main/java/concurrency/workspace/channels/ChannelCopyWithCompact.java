package concurrency.workspace.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;

/**
 * Copies data from the src channel and writes it to the dest channel until EOF on src.
 * This implementations make use of compact() on the temp buffer to pack down the data
 * if the buffer wasn't fully drained. This may result in data copying, but minimizes
 * system calls. It also requires a cleanup loop to make sure all the data gets sent.
 */
public class ChannelCopyWithCompact {

    public static void main(String[] args) throws IOException {

        try (final var source = Channels.newChannel(System.in);
             final var dest = Channels.newChannel(System.out)) {
            channelCopy(source, dest);
        }
    }

    private static void channelCopy(ReadableByteChannel src, WritableByteChannel dest) throws IOException {
        final var buffer = ByteBuffer.allocateDirect(16 * 1024);

        while (src.read(buffer) != -1) {
            // prepare to be drained
            buffer.flip();

            // write to the channel; might block
            dest.write(buffer);

            // If partial transfer, shift reminder down,
            // or if buffer is empty - same as `clear()`
            buffer.compact();
        }

        // EOF will leave buffer in fill state
        buffer.flip();

        // Make sure that the buffer is fully drained
        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }
}
