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

    /**
     * dummy print to check if works
     */
    public void welcomeMsg() {
        System.out.println("welcome to the hangman game");
    }

    public void sendInput(String input) {
        controller.sendInput(input);
    }
}
