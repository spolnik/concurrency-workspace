package concurrency.workspace.sockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;

import static java.nio.channels.SelectionKey.OP_READ;

public class SelectSockets {

    private static final int PORT_NUMBER = 1234;

    // Use the same byte buffer for all channels. A single thread is
    // serving all the channels, so no danger of concurrent access
    private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

    public static void main(String[] args) throws IOException {
        new SelectSockets().go(args);
    }

    private void go(String[] args) throws IOException {
        var port = PORT_NUMBER;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        System.out.println("Listening on port: " + port);

        // Allocate an unbound server socket channel
        var serverChanel = ServerSocketChannel.open();
        // Get the associated ServerSocket to bind it with
        var serverSocket = serverChanel.socket();

        var selector = Selector.open();

        serverSocket.bind(new InetSocketAddress(port));
        serverChanel.configureBlocking(false);
        serverChanel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            // This may block for a long time. Upon returning,
            // the selected set contains keys of the ready channels
            var n = selector.select();

            if (n == 0) {
                continue;
            }

            var it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                var key = (SelectionKey) it.next();

                // Is a new connection coming in?
                if (key.isAcceptable()) {
                    var server = (ServerSocketChannel) key.channel();
                    var channel = server.accept();

                    registerChannel(selector, channel, OP_READ);
                    sayHello(channel);
                }

                // Is there data to read on this channel?
                if (key.isReadable()) {
                    readDataFromSocket(key);
                }

                // Remove key from selected set; it's been handled
                it.remove();
            }
        }
    }

    /**
     * Register the given channel with the given selector for
     * the given operations of interest
     */
    private void registerChannel(Selector selector, SocketChannel channel, int ops) throws IOException {
        if (channel == null) return;

        channel.configureBlocking(false);
        channel.register(selector, ops);
    }

    /**
     * Sample data handler method for a channel with data ready to read.
     * @param key A SelectionKey object associated with a channel
     *            determined by the selector to be ready for reading. If the
     *            channel returns an EOF condition, it is closed here, which
     *            automatically invalidates the associated key. The selector
     *            will then de-register the channel on the next select call.
     */
    private void readDataFromSocket(SelectionKey key) throws IOException {
        var socketChannel = (SocketChannel) key.channel();
        int count;

        buffer.clear();

        // Loop while data is available; channel is nonblocking
        while ((count = socketChannel.read(buffer)) > 0) {
            buffer.flip();

            // Send the data; don't assume it goes all at once
            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }
            // WARNING: the above loop is evil. Because it's writing back to the same nonblocking
            // channel it read the data from, this code can potentially spin in a busy loop. In real life
            // you'd do something more useful than this :)

            buffer.clear();
        }

        if (count < 0) {
            // Close channel on EOF, invalidates the key
            socketChannel.close();
        }
    }

    private void sayHello(SocketChannel channel) throws IOException {
        buffer.clear();
        buffer.put("Hi there!\n".getBytes());
        buffer.flip();

        channel.write(buffer);
    }
}
