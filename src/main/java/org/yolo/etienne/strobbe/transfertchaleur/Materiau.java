package org.yolo.etienne.strobbe.transfertchaleur;

/**
 * Enum représentant les différents matériau disponible composant un mur complet.
 *
 * @author Etienne Strobbe
 */
public enum Materiau {
    LAINE_DE_VERRE,
    BRIQUE,
    GRANITE;

    /**
     * Récupère la valeur lambda du materiau
     * qui correspond à la conductivité thermique du matériau
     *
     * @return lambda
     */
    public double getLambda() {
        switch (this) {
            case LAINE_DE_VERRE:
                return 0.04;
            case BRIQUE:
                return 0.84;
            case GRANITE:
                return 2.2;
        }
        return -1;
    }

    /**
     * Récupère la valeur rho du matériau
     * qui correspond à la masse volumique du matériau
     *
     * @return rho
     */
    public int getRho() {
        switch (this) {
            case LAINE_DE_VERRE:
                return 30;
            case BRIQUE:
                return 1400;
            case GRANITE:
                return 2700;
        }
        return -1;
    }

    /**
     * Récupère la valeur c du matériau
     * qui correspond à la chaleur spécifique massique du matériau
     *
     * @return c
     */
    public int getC() {
        switch (this) {
            case LAINE_DE_VERRE:
                return 900;
            case BRIQUE:
                return 840;
            case GRANITE:
                return 790;
        }
        return -1;
    }
}
