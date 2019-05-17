package concurrency.workspace.servers;

import java.net.Socket;

public interface RequestHandler {

    void handle(Socket socket);
}
