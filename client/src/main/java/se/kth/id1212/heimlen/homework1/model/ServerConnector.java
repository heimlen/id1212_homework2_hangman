package se.kth.id1212.heimlen.homework1.model;

import jdk.internal.util.xml.impl.Input;
import org.omg.CORBA.TIMEOUT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Method that connects the client to the server
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
    public void connectToServer(String host, int port) throws IOException {
        System.out.println("in serverconnector");
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), TIMEOUT_20_SECONDS);
        socket.setSoTimeout(TIMEOUT_30_MINUTES);
        connected = true;
        streamFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        streamToServer = new PrintWriter(socket.getOutputStream());
        //TODO Start a thread that contains a listener, that listens for incoming data from the server
    }

    /**
     * Sends the input from the user to the server
     * @param input the input to be sent to the server
     */
    public void sendInput(String input) {
        //TODO THIS FIXED THE ISSUE, CHANGING PRINT to PRINTLN .-.-.-.-.-.-
       streamToServer.println(input);
       streamToServer.flush();
    }

    public void disconnect() throws IOException {
    socket.close();
    socket = null;
    connected = false;
    }
}
