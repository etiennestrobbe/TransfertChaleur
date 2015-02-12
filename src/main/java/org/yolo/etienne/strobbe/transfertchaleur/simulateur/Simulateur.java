package org.yolo.etienne.strobbe.transfertchaleur.simulateur;

import org.yolo.etienne.strobbe.transfertchaleur.modele.Mur;

/**
 * Created by Etienne on 12-Feb-15.
 */
public class Simulateur {
    private Mur murCourant;
    private Mur murSuivant;
    private Double C = 1.0;

    public Simulateur() {
        this.murCourant = new Mur();
        this.murCourant.setInit();
        this.murSuivant = new Mur();
    }

    private void update(int pos) {
        //TODO faire le calcul pour une seule cellule
        Double newTemp;
        //newTemp = T(x,t) + C*(T(x+1,t) + T(x-1,t) - 2T(x,t))
    }
}
