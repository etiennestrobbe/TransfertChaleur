package org.yolo.etienne.strobbe.transfertchaleur.simulateur;

import org.yolo.etienne.strobbe.transfertchaleur.modele.Materiau;
import org.yolo.etienne.strobbe.transfertchaleur.modele.Mur;
import org.yolo.etienne.strobbe.transfertchaleur.tools.Constantes;

import java.util.Date;

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

    private void update(int pos) {
        //TODO faire le calcul pour une seule cellule
        Double newTemp;
        Double constanteC = getconstanteC(murCourant.getMateriau(pos));
        // TODO voir les cas limites (extrémités)
        newTemp = murCourant.getTemp(pos) + constanteC * (murCourant.getTemp(pos + 1) + murCourant.getTemp(pos - 1) - 2 * murCourant.getTemp(pos));
        murSuivant.setTemp(pos, newTemp);
        //newTemp = T(x,t) + C*(T(x+1,t) + T(x-1,t) - 2T(x,t))
    }

    public void update() {
        //System.out.println("##################");
        //System.out.print("MUR AVANT : " + murSuivant);

        for (int i = 1; i < murCourant.size() - 1; i++) {
            update(i);
        }
        murCourant = murSuivant;
        murCourant.setTemp(0, 110.0);
        //System.out.print("MUR APRES : "+murSuivant);
        //System.out.println("##################");
    }

    public void print() {
        System.out.println(murCourant);
    }

    public static void main(String[] Args) {
        Simulateur simulateur = new Simulateur();
        int i = 0;
        simulateur.print();
        Date debut = new Date();
        while (i < 10000000) {
            simulateur.update();
            /*try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            i++;
        }
        Date fin = new Date();
        long diff = fin.getTime() - debut.getTime();
        simulateur.print();
        System.out.println("Execution : " + i + " iterations in " + diff + " ms");
    }
}
