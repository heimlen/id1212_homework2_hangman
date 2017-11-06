package se.kth.id1212.heimlen.homework1.controller;

import se.kth.id1212.heimlen.homework1.model.ServerConnector;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Controller class that handles connection between the <code>View</code> and <code>Model</code>
 */
public class Controller {
    private ServerConnector serverConnector;

    public Controller() {
        serverConnector = new ServerConnector();
    }

    public void sendInput(String input) {
        serverConnector.sendInput(input);
    }

    public void connectToServer(String host, int port) {
        try {
            serverConnector.connectToServer(host, port);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void disconnect() throws IOException {
        serverConnector.disconnect();
    }
}
