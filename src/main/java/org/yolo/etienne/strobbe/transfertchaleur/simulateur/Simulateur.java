package org.yolo.etienne.strobbe.transfertchaleur.simulateur;

import org.yolo.etienne.strobbe.transfertchaleur.modele.Materiau;
import org.yolo.etienne.strobbe.transfertchaleur.modele.Mur;
import org.yolo.etienne.strobbe.transfertchaleur.tools.Constantes;

/**
 * @author Etienne Strobbe
 * Classe s'occupant de la simulation de transmission de chaleur. Tous les calculs sont effectués dans cette classe.
 */
public class Simulateur {
    private Mur murCourant;
    private Mur murSuivant;

    /**
     * Constructeur
     */
    public Simulateur() {
        this.murCourant = new Mur();
        this.murCourant.setInit();
        this.murSuivant = new Mur();
    }

    private Double getconstanteC(Materiau materiau) {
        return (materiau.getLambda() * Constantes.DT) / (materiau.getRho() * materiau.getC() * Constantes.DX * Constantes.DX);
    }

    private void update(int pos) {
        //TODO faire le calcul pour une seule cellule
        Double newTemp;

        //  C = ( lambda * dt ) / ( rho * c * dx * dx)
        //newTemp = T(x,t) + C*(T(x+1,t) + T(x-1,t) - 2T(x,t))
    }
}
