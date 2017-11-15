package se.kth.id1212.heimlen.homework2.net;

import com.sun.security.ntlm.Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * The server that clients will connect to to play the hangman game.
 */
public class Server {
    private int portNum = 51234;
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
                if(clientReplyReady) {
                    //TODO add functionality to send reply messages to client
                    clientReplyReady = false;
                }
                selector.select(); //Blocking select operation, waiting for at least one client to connect.
                //TODO why keep an iterator here, when using a for each loop on client side?
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
                        //TODO write code for reading input from client
                    } else if (key.isWritable()) {
                        //TODO write code to write output to client
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
        clientChannel.register(selector, SelectionKey.OP_WRITE);
    }
}
