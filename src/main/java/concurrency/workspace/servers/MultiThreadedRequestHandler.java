package concurrency.workspace.servers;

import java.net.Socket;
import java.util.function.Consumer;

class MultiThreadedRequestHandler implements Consumer<Socket>, SocketUtils {

    private final Consumer<Socket> basicHandler;

    MultiThreadedRequestHandler(Consumer<Socket> basicHandler) {
        this.basicHandler = basicHandler;
    }

    @Override
    public void accept(Socket socket) {
        new Thread(() -> logSocketLifecycle(
            () -> basicHandler.accept(socket)
        ).accept(socket)).start();
    }
}
