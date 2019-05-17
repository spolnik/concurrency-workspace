package concurrency.workspace.servers;

import java.net.Socket;
import java.util.function.Consumer;

public interface SocketUtils {
    default Consumer<Socket> logSocketLifecycle(Runnable action) {
        return socket -> {
            System.err.println("Connected to " + socket);
            try {
                action.run();
            } finally {
                System.err.println("Disconnected from " + socket);
            }
        };
    }
}
