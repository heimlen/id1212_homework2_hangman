package se.kth.id1212.heimlen.homework2.model;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import static java.nio.channels.SelectionKey.OP_CONNECT;
import static se.kth.id1212.heimlen.homework2.Constants.BUFFERSIZE;

/**
 * Class that handles client connection to server. All operations are non-blocking.
 * */
public class ServerConnection implements Runnable {
    private InetSocketAddress serverAddress;
    private final Queue<ByteBuffer> inputQueue = new ArrayDeque<>();
    private final ByteBuffer outputFromServer = ByteBuffer.allocateDirect(BUFFERSIZE);
    private final Queue<String> outputReadyForClient = new ArrayDeque<>();
    private OutputObserver outputObserver;
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
                    receiveServerOutput();
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
        serverAddress = new InetSocketAddress(host, port);
        new Thread(this).start();
    }

    private void initSelector() throws IOException {
        selector = Selector.open();
        socketChannel.register(selector, OP_CONNECT);
    }

    private void initConnection() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(serverAddress);
        connected = true;
    }

    private void completeConnection(SelectionKey key) throws IOException {
        socketChannel.finishConnect();
        key.interestOps(SelectionKey.OP_READ);
        try {
            InetSocketAddress remoteAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
            notifyConnectionDone(remoteAddress);
        } catch (IOException couldNotGetRemAddrUsingDefaultInstead) {
            notifyConnectionDone(serverAddress);
        }
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

    private void receiveServerOutput() throws IOException {
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
     * @throws IOException if failed to disconnect
     */
    public void disconnect() throws IOException {
        socketChannel.close();
        socketChannel.keyFor(selector).cancel();
        connected = false;
        notifyDisconnectionDone();

    }

    public void addOutputHandler(OutputObserver outputObserver) {
        this.outputObserver = outputObserver;
    }

    private void sendServerOutput(String output) {
        Executor pool = ForkJoinPool.commonPool();
        pool.execute(() -> outputObserver.printToTerminal(output));
    }

    private void notifyConnectionDone(InetSocketAddress address) {
        Executor pool = ForkJoinPool.commonPool();
        pool.execute(() -> outputObserver.connected(address));
    }

    private void notifyDisconnectionDone() {
        Executor pool = ForkJoinPool.commonPool();
        pool.execute(() -> outputObserver.disconnected());}

}
