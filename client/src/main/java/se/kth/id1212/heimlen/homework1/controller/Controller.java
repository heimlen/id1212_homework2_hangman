package se.kth.id1212.heimlen.homework1.controller;

import com.sun.security.ntlm.Server;
import se.kth.id1212.heimlen.homework1.model.ServerConnector;

/**
 * Controller class that handles connection between the <code>View</code> and <code>Model</code>
 */
public class Controller {
    private ServerConnector srvconn;

    public Controller() {
        srvconn = new ServerConnector();
    }

    public void sendInput(String input) {
        srvconn.sendInput(input);
    }
}
