package concurrency.workspace.servers.handlers;

import java.net.Socket;
import java.util.function.Consumer;

public class ThreadedHandler extends DecoratingHandler<Socket> {

    public ThreadedHandler(Consumer<Socket> other) {
        super(other);
    }

    @Override
    public void accept(Socket socket) {
        new Thread(() -> super.accept(socket)).start();
    }
}
