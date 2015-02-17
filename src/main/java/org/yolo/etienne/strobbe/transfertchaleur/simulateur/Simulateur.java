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
        //Double prout = (materiau.getLambda() * Constantes.DT) / (materiau.getRho() * materiau.getC() * Constantes.DX * Constantes.DX);
        //System.out.println("CONSTANTE c = "+c);
        double lambda = materiau.getLambda();
        double rho = materiau.getRho();
        double c = materiau.getC();
        return (lambda * Constantes.DT) / (rho * c * Constantes.DX * Constantes.DX);
        //return c;
    }

    public Double update(int pos) {
        if (pos != 0 && pos != murCourant.size() - 1) {
            Double newTemp;
            Double constanteC = getconstanteC(murCourant.getMateriau(pos));
            //System.out.println("##CONSTANTE C = "+constanteC);
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

    /*public void update() {
        this.msg = "";
        //System.out.println("##################");
        //System.out.print("MUR AVANT : " + murSuivant);

        for (int i = 1; i < murCourant.size() - 1; i++) {
            update(i);
        }
        murCourant = murSuivant;
        murCourant.setTemp(0, 110.0);
        //System.out.print("MUR APRES : "+murSuivant);
        //System.out.println("##################");
    }*/


    public void affiche() {
        System.out.println(murCourant);
    }

    public static void main(String[]args){
        Simulateur simulateur = new Simulateur();
        Date debut = new Date();
        int k = 0;
        simulateur.affiche();
        while((k++)<600000000){
            for(int i=0;i<simulateur.sizeSimulation();i++){
                simulateur.update(i);

            }
            simulateur.reInit();

        }
        simulateur.affiche();
        Date fin = new Date();
        long diff = fin.getTime() - debut.getTime();
        System.out.println( (k*Constantes.DT)+" seconds simulated in "+diff+"ms");


    }

}
