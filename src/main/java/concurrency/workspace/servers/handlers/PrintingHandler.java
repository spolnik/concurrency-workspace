package concurrency.workspace.servers.handlers;

import java.util.function.Consumer;

public class PrintingHandler<T> extends DecoratingHandler<T> {
    public PrintingHandler(Consumer<T> other) {
        super(other);
    }

    public void accept(T socket) {
        System.err.println("Connected to " + socket);
        try {
            super.accept(socket);
        } finally {
            System.err.println("Disconnected from " + socket);
        }
    }
}
