package org.yolo.etienne.strobbe.transfertchaleur.modele;

/**
 * @author Etienne Strobbe
 *         Classe représentant un mur complet c'est à dire un ensemble de deux couches de tailles différentes et de matériaux différents
 */
public class Mur {
    private Couche mur;
    private Couche isolant;

    /**
     * Constructeur
     */
    public Mur() {
        this.mur = new Couche(5, Materiau.BRIQUE);
        this.isolant = new Couche(3, Materiau.LAINE_DE_VERRE);
    }

    /**
     * Initialise le mur par défaut
     * au premier instant de la simulation.
     */
    public void setInit() {
        for (int i = 0; i < mur.length(); i++) {
            this.mur.setTemperature(i, 20.0);
        }
        for (int i = 0; i < isolant.length(); i++) {
            this.isolant.setTemperature(i, 20.0);
        }
        mur.setTemperature(0, 110);
    }

    @Override
    public String toString() {
        return mur + " - " + isolant + "\n";
    }
}
