package se.kth.id1212.heimlen.homework1.model;

/**
 * This class randomly chooses a word from a wordlist and then takes input in form of letters or words, and tries to
 * match the input with the chosen word. The class holds all logic to calculate the # of tries left,
 * */
public class WordHandler {
    private int triesRemaining;
    private final String word = "halebop";


    private boolean containsInput(String input) {
        return word.contains(input);
    }

}
