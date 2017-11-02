package se.kth.id1212.heimlen.homework1.startup;

import se.kth.id1212.heimlen.homework1.view.View;

import java.util.Scanner;

/**
 * Created by heimlen on 2017-11-02.
 */
public class Main {

    public static void main(String[] args) {
        View view = new View();
        view.welcomeMsg();
        Scanner in = new Scanner(System.in);
        String input = in.next();
        view.sendInput(input);


        /*
        String s = "hello";
        String c = "hel lo";

        if(s.contains(c)) {
            System.out.println("The String " + s + " contains " + c + " :D");
        } else {
            System.out.println("The String " + s + " does not contain " + c + " :(");
        }*/
    }
}
