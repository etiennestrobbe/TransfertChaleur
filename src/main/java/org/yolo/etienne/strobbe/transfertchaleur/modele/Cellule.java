package org.yolo.etienne.strobbe.transfertchaleur.modele;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    /**
     * Getter
     *
     * @return le materiau de la cellule
     */
    public Materiau getMateriau() {
        return this.materiau;
    }

    @Override
    public String toString() {
        BigDecimal bd = new BigDecimal(temperature);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue() + "";
    }
}
