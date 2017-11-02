package se.kth.id1212.heimlen.homework1.net;

import se.kth.id1212.heimlen.homework1.controller.Controller;

import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Manages clients connecting to the server,
 * */
public class ClientHandler {
    private Controller controller;
    private Scanner fromClient;
    private PrintWriter toClient;


    public ClientHandler() {
        controller = new Controller();
    }

}
