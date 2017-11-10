package se.kth.id1212.heimlen.homework1.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Class that handles client connection to server.
 * */
public class ServerConnector {
    private Socket socket;
    private static final int TIMEOUT_20_SECONDS = 20000;
    private static final int TIMEOUT_30_MINUTES = 1800000;
    private BufferedReader streamFromServer;
    private PrintWriter streamToServer;
    private boolean connected;

    /**
     * Method that connects to the server provided the host address and port.
     * @param host the ip adress to connect to
     * @param port the port to connect to
     * @throws IOException if fails to connect
     */
    public void connectToServer(String host, int port, OutputHandler outputHandler) throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), TIMEOUT_20_SECONDS);
        socket.setSoTimeout(TIMEOUT_30_MINUTES);
        connected = true;
        streamFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        streamToServer = new PrintWriter(socket.getOutputStream());
        new Thread(new Listener(outputHandler)).start();
    }

    /**
     * Sends the input from the user to the server
     * @param input the input to be sent to the server
     */
    public void sendInput(String input) {
       streamToServer.println(input);
       streamToServer.flush();
    }

    /**
     * Disconnects the client from the server
     * @throws IOException
     */
    public void disconnect() throws IOException {
    socket.close();
    socket = null;
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
