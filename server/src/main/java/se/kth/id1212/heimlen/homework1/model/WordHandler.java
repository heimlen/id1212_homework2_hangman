package se.kth.id1212.heimlen.homework1.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class randomly chooses a word from a wordlist and then takes input in form of letters or words, and tries to
 * match the input with the chosen chosenWord. The class holds all logic to calculate the # of tries left, as well as what
 * letters that have been found in the chosen chosenWord.
 * */
public class WordHandler {
    private int triesRemaining;
    private static final int INITIAL_SCORE = 0;
    private int score;
    private static final String PATH = "common/words.txt";
    private static List<String> randomWords;
    private String chosenWord;
    private int chosenWordLength;
    private String maskedWord;

    public WordHandler() {
        //TODO randomly chose a word from a list of words.
        score = INITIAL_SCORE;
        loadWords();
        chooseWord();
        createMaskedWord();
    }

    private void createMaskedWord() {
        maskedWord = new String(new char[chosenWordLength]).replace('\0', '_');
    }

    private void loadWords() {
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(PATH));
            randomWords = new ArrayList<>();
            while (reader.readLine() != null) {
                randomWords.add(reader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void chooseWord() {
        Random rnd = new Random();
        chosenWord = randomWords.get(rnd.nextInt(randomWords.size()));
        chosenWordLength = chosenWord.length();
        triesRemaining = chosenWordLength;
    }

        /**
     * This method compares the users guess against the chosen chosenWord.
     * If chosen chosenWord contains users guess, return the updated layout of the chosenWord with the guess added.
     * If user guesses for the correct chosenWord, return updated layout and finish game.
     * If user guess is not in chosen chosenWord, decrement # of guesses and inform user.
     */
    public void controlInput(String input) {
        if (chosenWord.contains(input)) {
            if (input.length() == 1) {
                char inputChar = input.charAt(0);
                for (int i = 0; i < chosenWordLength; i++) {
                    if (inputChar == chosenWord.charAt(i)) {
                        replaceAt(maskedWord, inputChar, i);
                    }
                }
                System.out.println("The chosen word now looks like this " + maskedWord + " and " + triesRemaining + " tries remain!");
            } else if (input.length() == chosenWordLength) {
                    System.out.println("Congratz, you found the chosen word " + chosenWord + " with " + triesRemaining + " tries left!");
                    loadNewWord();
                }
        } else {
            if (input.length() == 1) {
                --triesRemaining;
                System.out.println("That character is not in the word!");
                System.out.println("The word now looks like this " + maskedWord + "and " + triesRemaining+ " tries remaining!");
            } else {
                --triesRemaining;
                System.out.println("That was not the word searched for! " + triesRemaining + " tries remaining!");
                System.out.println("The word now looks like this " + maskedWord + "and " + triesRemaining+ " tries remaining!");
            }
        }
    }
    private void loadNewWord() {
        chooseWord();
        System.out.println("You now have " + score + " points, and a new word has been added that looks like this " + maskedWord);
    }

    /**
     * Replaces character at <code>index</code> in <code>string</code> with <code>character</code>.
     * @param string the string that is to be changed
     * @param newChar the character to add
     * @param index the index where the character is to be replaced
     */
    private void replaceAt(String string, char newChar, int index) {
        StringBuilder sb = new StringBuilder(string);
        sb.setCharAt(index, newChar);
        maskedWord = sb.toString();
    }
    private boolean containsInput(String input) {
        return chosenWord.contains(input);
    }

}
