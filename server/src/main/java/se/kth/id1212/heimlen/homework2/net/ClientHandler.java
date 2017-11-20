package se.kth.id1212.heimlen.homework2.net;

import se.kth.id1212.heimlen.homework2.controller.Controller;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ForkJoinPool;

import static se.kth.id1212.heimlen.homework2.Constants.BUFFERSIZE;

/**
 * Manages a client connection.
 * */
public class ClientHandler implements Runnable {
    private Controller controller;
    private final ByteBuffer inputFromClient = ByteBuffer.allocateDirect(BUFFERSIZE);
    private final Queue<String> inputReadyForHandling = new ArrayDeque<>();
    private final Queue<ByteBuffer> outputReadyForClient = new ArrayDeque<>();
    private SocketChannel clientChannel;

    /**
     * Creates a new instance of <code>ClientHandler</code> that will manage the connection from a client connected via
     * the specified <code>clientChannel</code>.
     * @param server The server the client is to connect to.
     * @param clientChannel the channel with which the client is connected.
     */
    ClientHandler(Server server, SocketChannel clientChannel) {
        controller = new Controller();
        this.clientChannel = clientChannel;
    }

    /**
     * Sends client guesses to game-logic when it is ready to be handled.
     * */
    @Override
    public void run() {

        while (!inputReadyForHandling.isEmpty()) {
            outputReadyForClient.add(ByteBuffer.wrap(controller.sendInput(inputReadyForHandling.remove()).getBytes()));
            //System.out.println(controller.sendInput(inputReadyForHandling.remove()));
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

    /**
     * Receive the input from the client, and then task a thread in the <code>ForkJoinPool</code> to handle the input.
     * @throws IOException If failed to read message
     */
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
        return new String(bytes);
    }

    /**
     * Closes the connection to the client.
     * @throws IOException if closing the connection fails
     */
    void disconnectClient() throws IOException {
        clientChannel.close();
    }

    /**
     * Sends the servers output to the client.
     * @throws IOException if failed to send message
     */
    void sendServerOutput() throws IOException {
        //if the key is set to readable.
        while(!outputReadyForClient.isEmpty()) {
            ByteBuffer output = outputReadyForClient.remove();
            clientChannel.write(output);
            if(output.hasRemaining()) {
                return;
            }
        }
    }
}
