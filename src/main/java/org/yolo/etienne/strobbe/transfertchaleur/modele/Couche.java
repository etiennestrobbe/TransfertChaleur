package org.yolo.etienne.strobbe.transfertchaleur.modele;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Etienne Strobbe
 *         Classe représentant une couche d'un mur complet (ensemble de cellules de même matériau)
 */
public class Couche {

    private List<Cellule> couche;

    /**
     * Constructeur
     *
     * @param taille
     * @param materiau
     */
    public Couche(int taille, Materiau materiau) {
        this.couche = new ArrayList<Cellule>();
        for (int i = 0; i < taille; i++) {
            couche.add(new Cellule(materiau));
        }
    }

    /**
     * Défini la température d'une cellule dans la couche
     *
     * @param id   l'id représentant la cellule de la couche
     * @param temp la nouvelle température de la cellule
     */
    public void setTemperature(int id, double temp) {
        this.couche.get(id).setTemperature(temp);
    }

    /**
     * Renvoi la température d'une cellule de la couche
     *
     * @param id l'id représentant la cellule de la couche
     * @return la température de la cellule
     */
    public Double getTemperature(int id) {
        return this.couche.get(id).getTemperature();
    }

    /**
     * Renvoi la taille de la couche
     * c'est à dire le nombre de cellules la représentant
     *
     * @return taille de la couche
     */
    public int length() {
        return couche.size();
    }

    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < this.couche.size() - 1; i++) {
            res += couche.get(i).toString() + ",";
        }
        res += couche.get(couche.size() - 1).toString();
        return res;
    }
}