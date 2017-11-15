package se.kth.id1212.heimlen.homework2.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;

import static java.nio.channels.SelectionKey.OP_CONNECT;

/**
 * Class that handles client connection to server. All operations are non-blocking.
 * */
public class ServerConnection implements Runnable {
    private InetSocketAddress serverAdress;
    private Selector selector;
    private SocketChannel socketChannel;
    private final Queue<ByteBuffer> inputQueue = new ArrayDeque<>();
    private BufferedReader streamFromServer;
    private PrintWriter streamToServer;
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
                //TODO ask Leif why if(!key.isValid()) is here in his code
                 if(!key.isValid()) {
                     continue;
                 }
                 if(key.isConnectable()) {
                    completeConnection(key);
                 } else if(key.isWritable()) {
                     sendInputToServer(key);
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

    /**
     * Disconnects the client from the server
     * @throws IOException
     */
    public void disconnect() throws IOException {
        //TODO write new functionality for disconnecting
    connected = false;
    }

    private class Listener implements Runnable {
        private final OutputHandler outputHandler;

        private Listener(OutputHandler outputHandler) {
            this.outputHandler = outputHandler;
        }

        @Override
        public void run() {
            try {
                for(;;) {
                   outputHandler.printServerOutput(streamFromServer.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
