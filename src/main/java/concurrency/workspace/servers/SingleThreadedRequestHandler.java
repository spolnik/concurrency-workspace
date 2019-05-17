package concurrency.workspace.servers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;

class SingleThreadedRequestHandler implements RequestHandler, TextUtils, SocketUtils {

    @Override
    public void handle(Socket socket) {
        logSocketLifecycle(() -> {
            try (
                socket;
                var in = socket.getInputStream();
                var out = socket.getOutputStream()
            ) {
                int data;
                while ((data = in.read()) != -1) {
                    out.write(transmogrify(data));
                }
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }).accept(socket);
    }
}
