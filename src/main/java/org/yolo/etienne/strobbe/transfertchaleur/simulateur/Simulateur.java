package org.yolo.etienne.strobbe.transfertchaleur.simulateur;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Etienne Strobbe
 * Classe s'occupant de la simulation de transmission de chaleur. Tous les calculs sont effectues dans cette classe.
 */
public class Simulateur {
    private static final Logger LOGGER = Logger.getLogger("Simulateur");
    private Double[] mur;
    private Double[] C;
    private CyclicBarrier barrierCalcul;
    private CyclicBarrier barrierReInit;
    private Runnable barrierActionCalcul;
    private Runnable barrierActionReInit;
    private boolean done = false;
    private Date debut;
    private Date fin;
    private int it = 0;
    private int max;
    public static final double DT = 600.0;
    public static final double DX = 0.04;

    /**
     * Constructeur
     */
    public Simulateur(final int iterations) {
        this.max = iterations;
        this.mur = new Double[]{110.0, 20.0, 20.0, 20.0, 20.0, 20.0, 20.0, 20.0, 20.0};
        this.C = new Double[10];
        this.setConstantes();
        this.barrierActionCalcul = new Runnable() {
            @Override
            public void run() {
            }
        };
        this.barrierActionReInit = new Runnable() {
            boolean last = false;
            @Override
            public void run() {
                /*if(mur[7]>20 && !last){
                    System.out.println("Derniere couche atteinte a l'iteration "+it);
                    last = true;
                }*/
                if(++it >= max){
                    fin = new Date();
                    done = true;
                }
                if (it % 6 == 0) {
                    System.out.print("t=" + (it / 6) + "h ->");
                    affiche();
                }
            }
        };
        this.barrierCalcul = new CyclicBarrier(7, barrierActionCalcul);
        this.barrierReInit = new CyclicBarrier(7, barrierActionReInit);
    }

    private void setConstantes() {
        Double constanteMur = (0.84 * DT) / (1400 * 840 * DX * DX);
        Double constanteIso = (0.04 * DT) / (30 * 900 * DX * DX);
        for(int i=0;i<6;i++){
            C[i] = constanteMur;
        }
        for (int i = 6; i < 9; i++) {
            C[i] = constanteIso;
        }
        System.out.println("C1 : " + constanteMur + " C2 : " + constanteIso);
    }

    /**
     * Methode qui met a jour la temperature
     * du mur au point donne
     *
     * @param pos le lieu a mettre a jour
     * @return la nouvelle temperature calculee
     */
    public Double update(int pos) {
        return mur[pos] + C[pos + 1] * mur[pos + 1] + C[pos - 1] * mur[pos - 1] - (C[pos + 1] + C[pos - 1]) * mur[pos];
    }

    /**
     * Methode qui permet de reinitialiser
     * les valeurs des temperatures des murs
     * apres la fin des calculs
     * (on fait T(x,t) = T(x,t+1) )
     */
    private void reInitMur(int pos,double newValue) {
        mur[pos] = newValue;
    }

    /**
     * Lance la simulation
     */
    public void simule() {
        createThreads(mur.length);
    }

    /**
     * Creer les differents threads necessaires pour faires les calculs
     *
     * @param nb
     */
    private void createThreads(int nb){
        for(int i=1; i<nb-1; i++){
            new Thread(new ThreadSimulation(i)).start();
        }
    }

    private int round(Double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(0, RoundingMode.HALF_UP);
        return bd.intValue();
    }

    /**
     * Methode qui affiche l'etat courant du mur
     */
    public void affiche() {
        String res = "[";
        for (int i=0;i<mur.length-1;i++){
            if (i == 6) {
                res += " - " + round(mur[i - 1]) + ",";
            }
            if (i != 5) res += round(mur[i]) + ",";
            else res += round(mur[i]);
        }
        res += round(mur[mur.length - 1]) + " ]";
        System.out.println(res);
    }

    /**
     * Thread qui calcul la nouvelle temperature a un point donne
     * ce Thread est synchronise par une barriere
     */
    private class ThreadSimulation implements Runnable {
        private int position;
        private double value;

        public ThreadSimulation(int position) {
            super();
            this.position = position;
            this.value = 0.0;
        }

        public void run() {
            while (!done) {
                try {
                    value = update(this.position);
                    barrierCalcul.await();
                    reInitMur(this.position, this.value);
                    barrierReInit.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Main de test
     *
     * @param args
     */
    public static void main(String[] args) {
        int k = 60;
        Simulateur simulateur = new Simulateur(k);
        System.out.print("t=0h ->");

        simulateur.affiche();
        simulateur.debut = new Date();

        simulateur.simule();
        while (!simulateur.done) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long diff = simulateur.fin.getTime() - simulateur.debut.getTime();
        LOGGER.log(Level.INFO, "Approximately " + Math.round((simulateur.it * DT) / 3600) + " hours simulated in " + diff + "ms");


    }


}
