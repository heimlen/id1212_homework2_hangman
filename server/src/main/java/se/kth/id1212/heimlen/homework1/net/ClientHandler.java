package se.kth.id1212.heimlen.homework1.net;

import se.kth.id1212.heimlen.homework1.controller.Controller;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Manages a client connection.
 * */
public class ClientHandler implements Runnable {
    private Controller controller;
    private Server server;
    private Socket clientSocket;
    private BufferedReader fromClient;
    private PrintWriter toClient;
    private boolean connected;

    /**
     * Creates a new instance of <code>ClientHandler</code> that will manage the connection from a given
     * <code>clientSocket</code> to the specified <code>server</code>.
     * @param server The server the client is to connect to.
     * @param clientSocket the Socket with which the client is connected.
     */
    ClientHandler(Server server, Socket clientSocket) {
        controller = new Controller();
        this.server = server;
        this.clientSocket = clientSocket;
        connected = true;
    }

    /**
     * Overrides the default run method in runnable. This method will when a client is connected read input from the client
     * and deliver it to the server as well as read output from the server and deliver it to the client. This is the server
     * side link between clients and server.
     */
    @Override
    public void run() {
        try {
            fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            toClient = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        while (connected) {
            try {
                String clientInput = fromClient.readLine();
                String serverOutput = controller.sendInput(clientInput);
                toClient.println(serverOutput);
                toClient.flush();
            } catch (IOException e) {
                disconnectClient();
                e.printStackTrace();
            }
        }
    }

    private void disconnectClient() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connected = false;
    }
}
