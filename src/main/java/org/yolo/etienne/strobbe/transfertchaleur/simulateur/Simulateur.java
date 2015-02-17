package org.yolo.etienne.strobbe.transfertchaleur.simulateur;

import org.yolo.etienne.strobbe.transfertchaleur.modele.Materiau;
import org.yolo.etienne.strobbe.transfertchaleur.modele.Mur;
import org.yolo.etienne.strobbe.transfertchaleur.tools.Constantes;

import java.util.Date;
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
        double lambda = materiau.getLambda();
        double rho = materiau.getRho();
        double c = materiau.getC();
        return (lambda * Constantes.DT) / (rho * c * Constantes.DX * Constantes.DX);
    }

    /**
     * Methode qui met a jour la température
     * du mur au point donné
     *
     * @param pos le lieu a mettre a jour
     * @return la nouvelle température calculée
     */
    public Double update(int pos) {
        if (pos != 0 && pos != murCourant.size() - 1) {
            Double newTemp;
            Double constanteC = getconstanteC(murCourant.getMateriau(pos));
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

    public void affiche() {
        LOGGER.log(Level.INFO, murCourant.toString());
    }

    public static void main(String[] args) {
        Simulateur simulateur = new Simulateur();
        Date debut = new Date();
        int k = 0;
        simulateur.affiche();
        while ((k++) < 10000000) {
            for (int i = 0; i < simulateur.sizeSimulation(); i++) {
                simulateur.update(i);

            }
            simulateur.reInit();

        }
        simulateur.affiche();
        Date fin = new Date();
        long diff = fin.getTime() - debut.getTime();
        LOGGER.log(Level.INFO, (k * Constantes.DT) + " seconds simulated in " + diff + "ms");


    }

}
