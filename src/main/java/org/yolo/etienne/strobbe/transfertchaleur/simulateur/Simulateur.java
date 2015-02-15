package org.yolo.etienne.strobbe.transfertchaleur.simulateur;

import org.yolo.etienne.strobbe.transfertchaleur.modele.Materiau;
import org.yolo.etienne.strobbe.transfertchaleur.modele.Mur;
import org.yolo.etienne.strobbe.transfertchaleur.tools.BadIndexException;
import org.yolo.etienne.strobbe.transfertchaleur.tools.Constantes;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Etienne Strobbe
 * Classe s'occupant de la simulation de transmission de chaleur. Tous les calculs sont effectués dans cette classe.
 */
public class Simulateur {
    private Mur murCourant;
    private Mur murSuivant;
    private static final Logger LOGGER = Logger.getLogger("Simulateur");

    /**
     * Constructeur
     */
    public Simulateur() {
        this.murCourant = new Mur();
        this.murCourant.setInit();
        this.murSuivant = new Mur();
        this.murSuivant.setInit();
    }

    /**
     * Calcule la constante C pour la formule de calcul de température
     * selon un certain matériau
     *
     * @param materiau
     * @return
     */
    private Double getconstanteC(Materiau materiau) {
        return (materiau.getLambda() * Constantes.DT) / (materiau.getRho() * materiau.getC() * Constantes.DX * Constantes.DX);
    }

    public Double update(int pos) {
        if (pos != 0 && pos != murCourant.size() - 1) {
            Double newTemp;
            Double constanteC;
            try {
                constanteC = getconstanteC(murCourant.getMateriau(pos));
            } catch (BadIndexException e) {
                LOGGER.log(Level.SEVERE, e.getLocalizedMessage());
                constanteC = getconstanteC(Materiau.DEFAULT);
            }
            newTemp = murCourant.getTemp(pos) + constanteC * (murCourant.getTemp(pos + 1) + murCourant.getTemp(pos - 1) - 2 * murCourant.getTemp(pos));
            murSuivant.setTemp(pos, newTemp);
            return newTemp;
        }
        return murCourant.getTemp(pos);
    }

    public void reInit() {
        murCourant = murSuivant;
    }

    public int sizeSimulation() {
        return murCourant.size();
    }


}
