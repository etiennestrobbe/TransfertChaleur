package org.yolo.etienne.strobbe.transfertchaleur.modele;

/**
 * @author Etienne Strobbe
 *         Classe représentant la température d'une partie d'un mur quelconque à un instant et une position donnés.
 *         Cette cellule est représentée par ça température et son matériau.
 */
public class Cellule {

    private Materiau materiau;
    private Double temperature;

    /**
     * Constructeur
     *
     * @param materiau
     */
    public Cellule(Materiau materiau) {
        this.materiau = materiau;
        this.temperature = -1.0;
    }

    /**
     * Setter
     *
     * @param t la nouvelle température
     */
    public void setTemperature(double t) {
        this.temperature = t;
    }

    /**
     * Getter
     *
     * @return la température de la cellule
     */
    public Double getTemperature() {
        return this.temperature;
    }

    @Override
    public String toString() {
        return temperature + "";
    }
}
