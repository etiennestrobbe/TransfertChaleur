package org.yolo.etienne.strobbe.transfertchaleur.main;


import org.yolo.etienne.strobbe.transfertchaleur.simulateur.Simulateur;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger("Main");
    private Main() {
    }

    public static void sendMsg(Simulateur simulateur, int time, int pos) {
        String message = "<elt>";
        message += "<time>" + time + "</time>";
        message += "<X>" + pos + "</X>";
        message += "<value>" + simulateur.update(pos) + "</value>";
        message += "</elt>\n";
        JavaWebSocketServer.getInstance().broadcastMessage(message);
    }

    public static void main(String[] args) {
        LOGGER.log(Level.INFO, "Début simulation");

        JavaWebSocketServer.getInstance();// Init the server.

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Thread.sleep a planté");
        }

        Simulateur simulateur = new Simulateur();
        for (int i = 0; i < 100000; i++) {
            for (int j = 0; j < simulateur.sizeSimulation(); j++) {
                if (i % 1000 == 0) {
                    sendMsg(simulateur, i, j);
                }

            }
            simulateur.reInit();
        }
        LOGGER.log(Level.INFO, "fin simulation");

    }
}
