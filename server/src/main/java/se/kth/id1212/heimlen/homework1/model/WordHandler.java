package se.kth.id1212.heimlen.homework1.model;

/**
 * This class randomly chooses a word from a wordlist and then takes input in form of letters or words, and tries to
 * match the input with the chosen chosenWord. The class holds all logic to calculate the # of tries left, as well as what
 * letters that have been found in the chosen chosenWord.
 * */
public class WordHandler {
    private int triesRemaining;
    private final String chosenWord = "halebop";
    private int chosenWordLength;
    private String maskedWord;

    public WordHandler() {
        //TODO randomly chose a word from a list of words.
        chosenWordLength = chosenWord.length();
        triesRemaining = chosenWordLength;
        maskedWord = new String(new char[chosenWordLength]).replace('\0', '_');
    }

    /**
     * This method compares the users guess against the chosen chosenWord.
     * If chosen chosenWord contains users guess, return the updated layout of the chosenWord with the guess added.
     * If user guesses for the correct chosenWord, return updated layout and finish game.
     * If user guess is not in chosen chosenWord, decrement # of guesses and inform user.
     */
    public void controlInput(String input) {
        if(input.length() == 1) {
            char inputChar = input.charAt(0);
            for(int i = 0; i < chosenWordLength; i++) {
                if(inputChar == chosenWord.charAt(i)) {
                    replaceAt(maskedWord, inputChar, i);
                }
            }
            triesRemaining--;
            System.out.println("The new word now is " + maskedWord);
        } else if(input.length() == chosenWordLength) {
            if(containsInput(input)) {
              maskedWord = chosenWord;
              triesRemaining--;
              System.out.println("Congratz, you found the word with " + triesRemaining + " tries left!");
            }
            triesRemaining--;
            System.out.println("That was not correct! Tries remaining: " + triesRemaining);
        } else {
            System.out.println("Please guess either a letter or the entire word, no cheatzy doodle!");
        }
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

    //TODO make sure this is enough to check all possible input. If not, add more checks to make sure both chars as well
    //TODO as chosenWord guesses can be correctly handled
    private boolean containsInput(String input) {
        return chosenWord.contains(input);
    }

}
