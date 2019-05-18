package concurrency.workspace.servers.handlers;

import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

public class BlockingChannelHandler extends DecoratingHandler<SocketChannel> {

    public BlockingChannelHandler(Consumer<SocketChannel> other) {
        super(other);
    }

    @Override
    public void accept(SocketChannel socketChannel) {
        while(socketChannel.isConnected()) {
            super.accept(socketChannel);
        }
    }
}
