package se.kth.id1212.heimlen.homework1.controller;

import se.kth.id1212.heimlen.homework1.model.WordHandler;

/**
 * Controller on server side, all calls from clients to the server model pass through this layer to separate the
 * servers net layer, which handles all initial connects, and the servers model layer, where all logic is handled.
 */
public class Controller {
    private WordHandler wordhandler;

    public Controller() {
        wordhandler = new WordHandler();
    }

    public void sendInput(String input) {
        wordhandler.controlInput(input);
    }

}
