package se.kth.id1212.heimlen.homework2.model;

/**
 * Handles messages from server that are to be outputted to the client
 */
public interface OutputHandler {
    /**
     * Prints the output from the server that is to be printed to a client.
     *
     * @param output the output from server to client.
     */
    public void printServerOutput(String output);
}
