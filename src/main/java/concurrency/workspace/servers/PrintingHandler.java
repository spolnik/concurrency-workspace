package concurrency.workspace.servers;

import java.net.Socket;
import java.util.function.Consumer;

public class PrintingHandler implements Consumer<Socket> {
    private final Consumer<Socket> baseHandler;

    public PrintingHandler(Consumer<Socket> baseHandler) {
        this.baseHandler = baseHandler;
    }

    public void accept(Socket socket) {
        System.err.println("Connected to " + socket);
        try {
            baseHandler.accept(socket);
        } finally {
            System.err.println("Disconnected from " + socket);
        }
    }
}
