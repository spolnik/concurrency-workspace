package concurrency.workspace.servers.test;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class DoSAttack {

    public static void main(String[] args) throws IOException, InterruptedException {
        final var socket = new Socket[5000];

        for (int i = 0; i < socket.length; i++) {
            socket[i] = new Socket("localhost", 8080);
        }

        TimeUnit.SECONDS.sleep(10);
    }
}
