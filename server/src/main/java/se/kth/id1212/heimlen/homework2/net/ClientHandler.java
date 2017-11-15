package se.kth.id1212.heimlen.homework2.net;

import se.kth.id1212.heimlen.homework2.controller.Controller;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Manages a client connection.
 * */
public class ClientHandler implements Runnable {
    private Controller controller;
    private final static int BUFFERSIZE = 2048;
    private Server server;
    private final ByteBuffer inputFromClient = ByteBuffer.allocateDirect(BUFFERSIZE);
    private SocketChannel clientChannel;
    private boolean connected;

    /**
     * Creates a new instance of <code>ClientHandler</code> that will manage the connection from a client connected via
     * the specified <code>clientChannel</code>.
     * @param server The server the client is to connect to.
     * @param clientChannel the channel with which the client is connected.
     */
    ClientHandler(Server server, SocketChannel clientChannel) {
        controller = new Controller();
        this.server = server;
        this.clientChannel = clientChannel;
        connected = true;
    }

    /**
     * Receives and handles messages from the client.
     * */
    @Override
    public void run() {
        try {

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

    void receiveMessage() {

    }

    private void disconnectClient() throws IOException {
        clientChannel.close();
    }
}
