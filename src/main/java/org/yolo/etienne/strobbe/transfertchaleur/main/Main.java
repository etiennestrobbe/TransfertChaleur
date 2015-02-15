package org.yolo.etienne.strobbe.transfertchaleur.main;


import org.yolo.etienne.strobbe.transfertchaleur.simulateur.Simulateur;

public class Main {
    /*public static void main(String[] args) {
		JavaWebSocketServer.getInstance();// Init the server.
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Simulateur simulateur = new Simulateur();
		
		for(int i = 0; i < 200; i++) {
            simulateur.time = i;
		
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


            String msg = simulateur.print();
            System.out.println("emit message" + i + " : \n" + msg);
			JavaWebSocketServer.getInstance().broadcastMessage(msg);
            simulateur.update();
		}
	}*/

    public static void sendMsg(Simulateur simulateur, int time, int pos) {
        String message = "<elt>";
        message += "<time>" + time + "</time>";
        message += "<X>" + pos + "</X>";
        message += "<value>" + simulateur.update(pos) + "</value>";
        message += "</elt>\n";
        JavaWebSocketServer.getInstance().broadcastMessage(message);
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        System.out.println("Début simulation");

        JavaWebSocketServer.getInstance();// Init the server.

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        System.out.println("fin simulation");

    }
}
