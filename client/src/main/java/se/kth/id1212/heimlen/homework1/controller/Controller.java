package se.kth.id1212.heimlen.homework1.controller;

import se.kth.id1212.heimlen.homework1.model.OutputHandler;
import se.kth.id1212.heimlen.homework1.model.ServerConnector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;

/**
 * Controller class that handles connection between the <code>View</code> and <code>Model</code>
 */
public class Controller {
    private final ServerConnector serverConnector;

    /**
     * Constructor that creates a new instance of <code>ServerConnector</code>
     */
    public Controller() {
        serverConnector = new ServerConnector();
    }

    /**
     * Delivers the task of connecting to the provided server to the common thread pool, and then returns immediatly.
     * @param host the hostname or ip adress to connect to
     * @param port the port to connect to
     */
    public void connectToServer(String host, int port, OutputHandler outputHandler) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnector.connectToServer(host, port, outputHandler);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    /**
     * Delivers the provided input to the serverconnector via a thread from the common thread pool and then returns immediatly.
     * @param input the input from the client
     */
    public void sendInput(String input) {
        CompletableFuture.runAsync(() -> serverConnector.sendInput(input));
    }

    /**
     * Disconnects from the server.
     * @throws IOException
     */
    public void disconnect() throws IOException {
        serverConnector.disconnect();
    }
}
