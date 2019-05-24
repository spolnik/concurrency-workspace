package concurrency.workspace.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;

/**
 * Assures the temp buffer is empty before reading more data. This never requires
 * data copying but may result in more system calls. No post-loop cleanup is needed
 * because the buffer will be empty when the loop is exited.
 */
public class ChannelCopyWithClear {

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

            // Make sure the buffer was fully drained
            while(buffer.hasRemaining()) {
                // write to the channel; might block
                dest.write(buffer);
            }

            // Make the buffer empty, ready for filling
            buffer.clear();
        }
    }
}
