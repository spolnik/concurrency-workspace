package concurrency.workspace.servers;

import java.net.Socket;
import java.util.function.Consumer;

class SingleThreadedRequestHandler implements Consumer<Socket>, SocketUtils {

    private final Consumer<Socket> baseConsumer;

    SingleThreadedRequestHandler(Consumer<Socket> baseConsumer) {
        this.baseConsumer = baseConsumer;
    }

    @Override
    public void accept(Socket socket) {
        logSocketLifecycle(
            () -> baseConsumer.accept(socket)
        ).accept(socket);
    }
}
