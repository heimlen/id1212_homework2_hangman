package se.kth.id1212.heimlen.homework1.net;

import se.kth.id1212.heimlen.homework1.controller.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Manages a client connection.
 * */
public class ClientHandler implements Runnable {
    private Controller controller;
    private Server server;
    private Socket clientSocket;
    private Scanner fromClient;
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

    @Override
    public void run() {
        try {
            fromClient = new Scanner(clientSocket.getInputStream());
            toClient = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        //TODO something wrong here, it succesfully starts different threads for each client, however the server throws a
        //TODO NoSuchElementException after one entry, it seems like fromClient.next() tries to read an entry and when it
        //TODO fails it crashes, which leads to client process exit, or rather the client is "done" fix this
        while (connected) {
            if(fromClient.hasNext()) {
                String clientInput = fromClient.next();
                controller.sendInput(clientInput);
            }
        }
    }
}
