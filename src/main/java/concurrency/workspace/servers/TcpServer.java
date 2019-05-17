package concurrency.workspace.servers;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

public class TcpServer {

    private static final Consumer<Socket> PRINTING_HANDLER = new PrintingHandler(new TransmogrifyHandler());

    private static final Map<String, Consumer<Socket>> HANDLERS = Map.of(
        "single", PRINTING_HANDLER,
        "multi", new MultiThreadedRequestHandler(PRINTING_HANDLER)
    );

    public static void main(String[] args) throws IOException {
        final var options = options();

        final var parser = new DefaultParser();
        final var formatter = new HelpFormatter();

        try {
            var cmd = parser.parse(options, args);
            var command = cmd.getOptionValue("command");
            var port = Integer.parseInt(cmd.getOptionValue("port", "8080"));

            new TcpServer().run(command, port);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }
    }

    void run(String command, int port) throws IOException {
        try (var serverSocket = new ServerSocket(port)) {
            System.out.println("Listening on localhost:" + port);
            serverLoop(command, serverSocket);
        }
    }

    private void serverLoop(String command, ServerSocket serverSocket) throws IOException {
        while (true) {
            final var requestHandler = ofNullable(HANDLERS.get(command));
            if (requestHandler.isPresent()) {
                final var socket = serverSocket.accept();
                requestHandler.get().accept(socket);
            }
        }
    }

    private static Options options() {
        final var options = new Options();

        final var commandOpt = new Option("c", "command", true, "single, multi, pooled");
        commandOpt.setRequired(true);
        options.addOption(commandOpt);

        final var portOpt = new Option("p", "port", true, "port (default: 8080)");
        portOpt.setRequired(false);
        options.addOption(portOpt);
        return options;
    }
}
