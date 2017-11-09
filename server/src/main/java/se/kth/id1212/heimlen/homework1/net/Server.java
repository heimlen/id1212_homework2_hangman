package se.kth.id1212.heimlen.homework1.net;

import se.kth.id1212.heimlen.homework1.controller.Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * The server that clients will connect to to play the hangman game.
 */
public class Server {
    private Controller controller;
    private int portNum = 51234;
    private static final int LINGER_TIME = 5000;
    private static final int TIMEOUT_TIME = 30000;

    /**
     * Starts the server by starting a listening-socket on port defaulted in <code>portNum</code>. All clients connecting
     * will be served their own thread and their own <code>ClientHandler</code>.
     * @param args
     */
    public static void main(String[] args) {
        Server server = new Server();
        System.out.println("Starting server MAIN METHOD");
        server.serve();
    }

    private void serve() {
        try {
            ServerSocket listeningSocket = new ServerSocket(portNum);
            System.out.println("in serve method server class on server");
            while(true) {
                Socket clientSocket = listeningSocket.accept();
                startClientHandler(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Failed to start server.");
        }
    }

    private void startClientHandler(Socket clientSocket) throws SocketException {
        clientSocket.setSoLinger(true, LINGER_TIME);
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        Thread clientThread = new Thread(clientHandler);
        clientThread.setPriority(Thread.MAX_PRIORITY);
        clientThread.start();
        System.out.println("starting client in server class");
    }
}
