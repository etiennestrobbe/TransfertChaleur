package org.yolo.etienne.strobbe.transfertchaleur.modele;

import org.yolo.etienne.strobbe.transfertchaleur.tools.Constantes;
import org.yolo.etienne.strobbe.transfertchaleur.tools.Tuples;

/**
 * @author Etienne Strobbe
 *         Classe représentant un mur complet c'est à dire un ensemble de deux couches de tailles différentes et de matériaux différents
 */
public class Mur {
    private Couche murExterieur;
    private Couche isolant;


    /**
     * Constructeur
     */
    public Mur() {
        this.murExterieur = new Couche(Constantes.SIZE_MUR, Materiau.BRIQUE);
        this.isolant = new Couche(Constantes.SIZE_ISOLANT, Materiau.LAINE_DE_VERRE);
    }

    /**
     * Initialise le mur par défaut
     * au premier instant de la simulation.
     */
    public void setInit() {
        for (int i = 0; i < murExterieur.length(); i++) {
            this.murExterieur.setTemperature(i, 20.0);
        }
        for (int i = 0; i < isolant.length(); i++) {
            this.isolant.setTemperature(i, 20.0);
        }
        murExterieur.setTemperature(0, 110);
    }

    /**
     * Getter
     * Renvoi la température du mur à un lieu donné
     *
     * @param id l'id correspondant au lieu donné
     * @return la température cherchée
     */
    public Double getTemp(int id) {
        Tuples<Boolean, Integer> trueId = this.getIdCouche(id);
        if (id < 0) return -1.0;
        if (id >= Constantes.SIZE_ISOLANT + Constantes.SIZE_MUR) return -1.0;
        return (id >= Constantes.SIZE_MUR) ? isolant.getTemperature(id - Constantes.SIZE_MUR) : murExterieur.getTemperature(id);
    }

    /**
     * Setter
     * Défini la température du mur à un lieu donné
     *
     * @param id l'id correspondant au lieu donné
     */
    public void setTemp(int id) {

    }

    private Tuples<Boolean, Integer> getIdCouche(int id) {
        if (id < 0) return new Tuples<Boolean, Integer>(false, -1);
        if (id >= Constantes.SIZE_MUR + Constantes.SIZE_ISOLANT) new Tuples<Boolean, Integer>(false, -1);
        return (id >= Constantes.SIZE_MUR) ? new Tuples<Boolean, Integer>(false, id - Constantes.SIZE_MUR) : new Tuples<Boolean, Integer>(true, id);
    }

    @Override
    public String toString() {
        return murExterieur + " - " + isolant + "\n";
    }
}
