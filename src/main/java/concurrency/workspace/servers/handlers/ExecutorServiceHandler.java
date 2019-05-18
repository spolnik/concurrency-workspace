package concurrency.workspace.servers.handlers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;

public class ExecutorServiceHandler<T> extends DecoratingHandler<T> {

    private final ExecutorService executorService;
    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public ExecutorServiceHandler(
        Consumer<T> other,
        ExecutorService executorService,
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler
    ) {
        super(other);
        this.executorService = executorService;
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    public ExecutorServiceHandler(Consumer<T> other, ExecutorService executorService) {
        this(other, executorService, (t, e) -> System.err.println("uncaught: " + t + " error " + e));
    }

    @Override
    public void accept(T socket) {
        executorService.submit(new FutureTask<>(() -> {
            super.accept(socket);
            return null;
        }) {
            @Override
            protected void setException(Throwable t) {
                uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), t);
            }
        });
    }
}
