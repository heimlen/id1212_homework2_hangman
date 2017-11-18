package se.kth.id1212.heimlen.homework2.net;

import se.kth.id1212.heimlen.homework2.controller.Controller;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ForkJoinPool;

/**
 * Manages a client connection.
 * */
public class ClientHandler implements Runnable {
    private Controller controller;
    private final static int BUFFERSIZE = 2048;
    private Server server;
    private final ByteBuffer inputFromClient = ByteBuffer.allocateDirect(BUFFERSIZE);
    private final Queue<String> inputReadyForHandling = new ArrayDeque<>();
    private final Queue<ByteBuffer> outputReadyForClient = new ArrayDeque<>();
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
     * Sends client guesses to game-logic when it is ready to be handled.
     * */
    @Override
    public void run() {

        while (!inputReadyForHandling.isEmpty()) {
            outputReadyForClient.add(ByteBuffer.wrap(controller.sendInput(inputReadyForHandling.remove()).getBytes()));
            //System.out.println(controller.sendInput(inputReadyForHandling.remove()));
            //TODO add some way to communicate back to view.
        }
       /*     try {
                String clientInput = fromClient.readLine();
                String serverOutput = controller.sendInput(clientInput);
                toClient.println(serverOutput);
                toClient.flush();
            } catch (IOException e) {
                disconnectClient();
                e.printStackTrace();
            }
        }*/
    }

    void receiveInput() throws IOException {
        inputFromClient.clear();
        int numOfReadBytes;
        numOfReadBytes = clientChannel.read(inputFromClient);
        if(numOfReadBytes == -1) {
            throw new IOException("Client has closed connection.");
        }
        String receivedInput = extractInputFromBuffer();
        inputReadyForHandling.add(receivedInput);
        System.out.println("This is what the server received : " + receivedInput);
        ForkJoinPool.commonPool().execute(this);
    }

    private String extractInputFromBuffer() {
        inputFromClient.flip();
        byte[] bytes = new byte[inputFromClient.remaining()];
        inputFromClient.get(bytes);
        return new String(bytes); //This line actually decodes the bytes into a new string using the default charset, nice one!
    }

    private void disconnectClient() throws IOException {
        clientChannel.close();
    }

    void sendServerOutput() throws IOException {
        //TODO add a way to send output form server back to client, this method should be called from the Server class
        //if the key is set to readable.
        while(!outputReadyForClient.isEmpty()) {
            clientChannel.write(outputReadyForClient.remove());
        }
        /*if() {
            throw new IOException("Could not send message");
        }*/
    }
}
