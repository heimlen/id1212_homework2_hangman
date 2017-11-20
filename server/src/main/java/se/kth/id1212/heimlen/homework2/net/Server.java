package se.kth.id1212.heimlen.homework2.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

/**
 * The server that clients will connect to to play the hangman game. This is the first class on the server side that the client
 * encounters, and therefore contains code to handle new clients connecting.
 */
public class Server {
    private int portNum = 51234;
    private static final int LINGER_TIME = 5000;
    private Selector selector;
    private ServerSocketChannel listeningSocketChannel;
    private volatile boolean clientReplyReady = false;

    /**
     * Starts the server by starting a listening-socket on port defaulted in <code>portNum</code>. All clients connecting
     * will be served their own thread and their own <code>ClientHandler</code>.
     * @param args
     */
    public static void main(String[] args) {
        Server server = new Server();
        server.serve();
    }

    private void serve() {
        try {
            initSelector();
            initListeningSocketChannel();

            while(true) {
                selector.select(); //Blocking select operation, waiting for at least one client to connect.
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        startClientHandler(key);
                    } else if (key.isReadable()) {
                        receiveInputFromClient(key);
                    } else if (key.isWritable()) {
                        sendOutputToClient(key);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initSelector() throws IOException {
        selector = Selector.open();
    }

    private void initListeningSocketChannel() throws IOException {
        listeningSocketChannel = ServerSocketChannel.open();
        listeningSocketChannel.configureBlocking(false);
        listeningSocketChannel.bind(new InetSocketAddress(portNum));
        listeningSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void startClientHandler(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        ClientHandler clientHandler = new ClientHandler(this,clientChannel);
        clientChannel.register(selector, SelectionKey.OP_READ, new Client(clientHandler));
        clientChannel.setOption(StandardSocketOptions.SO_LINGER, LINGER_TIME);
    }

    private void receiveInputFromClient(SelectionKey key) throws IOException {
        Client client = (Client) key.attachment();
        try {
            client.clientHandler.receiveInput();
            key.interestOps(SelectionKey.OP_WRITE);
        } catch (IOException clientHasClosedConnection) {
            removeClient(key);
        }

    }

    private void removeClient(SelectionKey clientKey) throws IOException {
        Client client = (Client) clientKey.attachment();
        client.clientHandler.disconnectClient();
        clientKey.cancel();
    }

    private void sendOutputToClient(SelectionKey key) {
        Client client = (Client) key.attachment();
        try {
            client.clientHandler.sendServerOutput();
            key.interestOps(SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Client {
        private final ClientHandler clientHandler;

        private Client(ClientHandler clientHandler) {
            this.clientHandler = clientHandler;
        }
    }
}
