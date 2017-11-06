package se.kth.id1212.heimlen.homework1.view;

import se.kth.id1212.heimlen.homework1.controller.Controller;

/**
 * Very easy UI that allows the user to enter a letter or a word
 */
public class View {
    private Controller controller;

    public View() {
        controller = new Controller();
    }

    //TODO Clean up the view and move everything that has to do with server connecting etc. to another class.

    /**
     * dummy print to check if works
     */
    public void welcomeMsg() {
        System.out.println("welcome to the hangman game");
    }

    /**
     * Dummy method to connect to server
     * @param host the ip address of the host
     * @param port the port the server resides on
     */
    public void connectToServer(String host, int port) {
        controller.connectToServer(host, port);
    }

    public void sendInput(String input) {
        controller.sendInput(input);
    }
}
