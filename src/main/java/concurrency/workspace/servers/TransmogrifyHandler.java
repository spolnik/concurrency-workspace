package concurrency.workspace.servers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.function.Consumer;

public class TransmogrifyHandler implements Consumer<Socket> {

    @Override
    public void accept(Socket socket) {
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
    }

    private int transmogrify(int data) {
        return Character.isLetter(data) ? data ^ ' ' : data;
    }
}
