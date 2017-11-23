package se.kth.id1212.heimlen.homework2.startup;

import se.kth.id1212.heimlen.homework2.view.InputInterpreter;

/**
 * The main method that starts the interpreter to be able to handle client input.
 */
public class Main {

    public static void main(String[] args) {
        new InputInterpreter().start();
    }
}
