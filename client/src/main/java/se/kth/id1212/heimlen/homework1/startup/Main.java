package se.kth.id1212.heimlen.homework1.startup;

import jdk.internal.util.xml.impl.Input;
import se.kth.id1212.heimlen.homework1.view.InputInterpreter;
import se.kth.id1212.heimlen.homework1.view.View;

import java.util.Scanner;

/**
 * The main method that starts the interpreter to be able to handle client input.
 */
public class Main {

    public static void main(String[] args) {
        new InputInterpreter().start();
    }

        /*
        String s = "hello";
        String c = "hel lo";

        if(s.contains(c)) {
            System.out.println("The String " + s + " contains " + c + " :D");
        } else {
            System.out.println("The String " + s + " does not contain " + c + " :(");
        }*/
    }
