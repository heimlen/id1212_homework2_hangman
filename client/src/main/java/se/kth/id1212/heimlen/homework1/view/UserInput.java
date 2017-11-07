package se.kth.id1212.heimlen.homework1.view;

import se.kth.id1212.heimlen.homework1.exceptions.BadFormattedInputException;
import se.kth.id1212.heimlen.homework1.exceptions.UnknownCommandException;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class represents one line of user input which much start with a valid user-command and then possibly take another parameter.
 * */
public class UserInput {
    private final String enteredInput;
    private static final String PARAM_DELIMITER = " ";
    private String userCmd;
    private String[] parameters;
    private UserCommand userCommand;
    /**
     * Constructor taking a line of user input and parsing it, since this is a type of DTO everything is done in the constructor.
     * @param userInput the command that the user wants to run
     */
    UserInput(String userInput) throws UnknownCommandException, BadFormattedInputException {
        this.enteredInput = userInput;
        parameters = new String[3];
        splitInput(enteredInput);
        parseCommand(userCmd);
    }

    private void splitInput(String enteredInput) {
        if(enteredInput == null) {
            userCmd = null;
            parameters = null;
            return;
        }
        String[] splitInput = enteredInput.split(PARAM_DELIMITER);
        userCmd = splitInput[0].toUpperCase();
        parameters = Arrays.copyOfRange(splitInput,1,splitInput.length);
    }

    private void parseCommand(String userCmd) throws UnknownCommandException, BadFormattedInputException {
        if(userCmd == null) {
            throw new  BadFormattedInputException("This is not a correctly typed input, please enter input as COMMAND parameter instead");
        }

        switch(userCmd) {
            case "START" :
                userCommand = UserCommand.START;
                break;
            case "GUESS" :
                userCommand = UserCommand.GUESS;
                break;
            case "CONNECT" :
                userCommand = UserCommand.CONNECT;
                break;
            case "QUIT" :
                userCommand = UserCommand.QUIT;
                break;
            default:
                throw new UnknownCommandException("the entered command " + userCmd + " is unknown, please enter a known command");
        }
    }

    public UserCommand getUserCommand() {
        return userCommand;
    }

    public String getFirstParam() {
        return parameters[0];
    }

    public String getSecondParam() {
        return parameters[1];
    }

    //TODO Check if user should be able to/need to supply several parameters (connection ip and port??)
    /*private void parseArgs(String parameter) {

    }*/
}
