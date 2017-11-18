package se.kth.id1212.heimlen.homework2.model;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import static java.nio.channels.SelectionKey.OP_CONNECT;

/**
 * Class that handles client connection to server. All operations are non-blocking.
 * */
public class ServerConnection implements Runnable {
    //TODO Fråga teo om hur jag får in common och Constants i det här paketet
    private final static int BUFFERSIZE = 2048;
    private InetSocketAddress serverAdress;
    private final Queue<ByteBuffer> inputQueue = new ArrayDeque<>();
    private final ByteBuffer outputFromServer = ByteBuffer.allocateDirect(BUFFERSIZE);
    private final Queue<String> outputReadyForClient = new ArrayDeque<>();
    private final List<OutputObserver> outputObservers = new ArrayList<>();
    private Selector selector;
    private SocketChannel socketChannel;
    private boolean connected;
    private volatile boolean inputRdyToSend = false;

    @Override
    public void run() {
        try {
            initConnection();
            initSelector();

            while(connected || !inputQueue.isEmpty()) {
             if(inputRdyToSend) {
                 socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                 inputRdyToSend = false;
             }

             selector.select(); //blocking select, waiting for the first select
             for(SelectionKey key : selector.selectedKeys()) {
                 selector.selectedKeys().remove(key);
                if(!key.isValid()) {
                     continue;
                 }
                 if(key.isConnectable()) {
                    completeConnection(key);
                 } else if(key.isWritable()) {
                     sendInputToServer(key);
                 } else if(key.isReadable()) {
                    receiveServerOutput(key);
                 }
             }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that connects to the server provided the host address and port.
     * @param host the ip adress to connect to
     * @param port the port to connect to
     */
    public void connectToServer(String host, int port){
        serverAdress = new InetSocketAddress(host, port);
        new Thread(this).start();
    }

    private void initSelector() throws IOException {
        selector = Selector.open();
        socketChannel.register(selector, OP_CONNECT);
    }

    private void initConnection() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(serverAdress);
        connected = true;
    }

    private void completeConnection(SelectionKey key) throws IOException {
        socketChannel.finishConnect();
        key.interestOps(SelectionKey.OP_READ);
    }
    /**
     * Iterates over user-input in the <code>inputQueue</code> which is to be sent to the server, and sends this input to server.
     * @param key the <code>SelectionKey</code> representing the registration of the <code>socketChannel</code> with the selector.
     */
    private void sendInputToServer(SelectionKey key) throws IOException {
        ByteBuffer input;
        synchronized(inputQueue) {
            while((input = inputQueue.peek()) != null) {
                socketChannel.write(input);
                if(input.hasRemaining()) {
                    return;
                }
                inputQueue.remove();
            }
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    /**
     * Method that adds user-input to the inputQueue which contains input that will be sent to the server.
     * @param input the user-input that is to be sent to the server
     */
    public void sendClientInput(String input) {
        synchronized (inputQueue) {
            inputQueue.add(ByteBuffer.wrap(input.getBytes()));
        }
        inputRdyToSend = true;
        selector.wakeup();
    }

    private void receiveServerOutput(SelectionKey key) throws IOException {
        outputFromServer.clear();
        int numOfReadBytes;
        numOfReadBytes = socketChannel.read(outputFromServer);
        if(numOfReadBytes == -1) {
            throw new IOException("Error while receiving message.");
        }
        String receivedInput = extractOutputFromBuffer();
        outputReadyForClient.add(receivedInput);
        while(!outputReadyForClient.isEmpty()) {
            sendServerOutput(outputReadyForClient.remove());
        }
    }

    private String extractOutputFromBuffer() {
        outputFromServer.flip();
        byte[] bytes = new byte[outputFromServer.remaining()];
        outputFromServer.get(bytes);
        return new String(bytes); //This line actually decodes the bytes into a new string using the default charset, nice one!
    }

    /**
     * Disconnects the client from the server
     * @throws IOException
     */
    public void disconnect() throws IOException {
        socketChannel.close();
        socketChannel.keyFor(selector).cancel();
        connected = false;
        System.out.println("disconnected");
    }

    public void addOutputHandler(OutputObserver OutputObserver) {
        outputObservers.add(OutputObserver);
    }

    private void sendServerOutput(String output) {
        Executor pool = ForkJoinPool.commonPool();
        for(OutputObserver observer : outputObservers) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    observer.printServerOutput(output);
                }
            });}
    }
}
