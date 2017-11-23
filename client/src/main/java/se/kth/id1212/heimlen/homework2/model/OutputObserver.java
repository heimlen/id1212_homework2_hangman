package se.kth.id1212.heimlen.homework2.model;

import java.net.InetSocketAddress;

/**
 * Handles messages from server that are to be outputted to the client
 */
public interface OutputObserver {
    /**
     * Prints the output from the server that is to be printed to a client.
     *
     * @param output the output from server to client.
     */
    public void printToTerminal(String output);

    public void connected(InetSocketAddress address);

    public void disconnected();
}
