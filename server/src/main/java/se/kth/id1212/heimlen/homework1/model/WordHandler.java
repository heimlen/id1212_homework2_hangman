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
    private String outputMsg;

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
                randomWords.add(reader.readLine().toLowerCase());
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
    public String controlInput(String input) {
        if (chosenWord.contains(input) && triesRemaining != 0) {
            if (input.length() == 1) {
                char inputChar = input.charAt(0);
                for (int i = 0; i < chosenWordLength; i++) {
                    if (inputChar == chosenWord.charAt(i)) {
                        replaceAt(maskedWord, inputChar, i);
                    }
                }
                outputMsg = createOutMsg();
                if(chosenWord.equals(maskedWord)) {
                    score++;
                    loadNewWord();
                    outputMsg = createOutMsg();
                }
                return outputMsg;
            } else if (chosenWord.equals(input)) {
                score++;
                loadNewWord();
                outputMsg = createOutMsg();
                return outputMsg;
            }
        } else if (triesRemaining != 0) {
            triesRemaining--;
            outputMsg = createOutMsg();
                return outputMsg;
        }
        return zeroTriesLeft();
    }

    private void loadNewWord() {
        chooseWord();
        createMaskedWord();
    }

    /**
     * Replaces character at <code>index</code> in <code>string</code> with <code>character</code>.
     *
     * @param string  the string that is to be changed
     * @param newChar the character to add
     * @param index   the index where the character is to be replaced
     */
    private void replaceAt(String string, char newChar, int index) {
        StringBuilder sb = new StringBuilder(string);
        sb.setCharAt(index, newChar);
        maskedWord = sb.toString();
    }

    private String createOutMsg() {
        if(chosenWord.equals(maskedWord) || triesRemaining == 0) {
            return "||  Masked Word  | Tries remaining | Score | Word ||\n" +
                    "|| " + maskedWord + " | " + triesRemaining + " | " + score + " | " + chosenWord + " ||";
        } else {
            return "||  Masked Word  | Tries remaining | Score | Word ||\n" +
                    "|| " + maskedWord + " | " + triesRemaining + " | " + score + " | " + "?" + " ||";
        }
    }

    private String zeroTriesLeft() {
            score--;
            loadNewWord();
            outputMsg = createOutMsg();
            return outputMsg;
    }
}
