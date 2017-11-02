package se.kth.id1212.heimlen.homework1.model;

import jdk.internal.util.xml.impl.Input;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Method that connects the client to the server
 * */
public class ServerConnector {
    private Socket socket;
    private static final int TIMEOUT_HALF_MINUTE = 20000;
    private Scanner streamFromServer;
    private PrintWriter streamToServer;

    /**
     * Method that connects to the server provided the host address and port
     * @param host the ip adress to connect to
     * @param port the port to connect to
     * @throws IOException if fails to connect
     */
    public void connectToServer(String host, int port) throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), TIMEOUT_HALF_MINUTE);
        streamFromServer = new Scanner(socket.getInputStream());
        streamToServer = new PrintWriter(socket.getOutputStream());
    }

    /**
     * Sends the input from the user to the server
     * @param input the input to be sent to the server
     */
    public void sendInput(String input) {
//       streamToServer.write(input);
//       streamToServer.flush();
        System.out.println("Message " + input + " delivered from SrvConn to Server");
    }
}
