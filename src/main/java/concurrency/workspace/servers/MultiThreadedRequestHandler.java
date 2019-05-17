package concurrency.workspace.servers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;

class MultiThreadedRequestHandler implements RequestHandler, SocketUtils, TextUtils {

    @Override
    public void handle(Socket socket) {
        new Thread(() -> logSocketLifecycle(() -> {
            try (
                socket;
                var in = socket.getInputStream();
                var out = socket.getOutputStream()
            ) {
                int data;
                while ((data = in.read()) != -1) {
                    out.write(transmogrify(data));
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }).accept(socket)).start();
    }
}
