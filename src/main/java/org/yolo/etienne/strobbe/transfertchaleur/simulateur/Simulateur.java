package org.yolo.etienne.strobbe.transfertchaleur.simulateur;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Etienne Strobbe
 * Classe s'occupant de la simulation de transmission de chaleur. Tous les calculs sont effectues dans cette classe.
 */
public class Simulateur {
    private static final Logger LOGGER = Logger.getLogger("Simulateur");
    private Double[][] mur;
    private Double[] C;
    private boolean done[] = {false, false, false, false, false, false, false};
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
        this.mur = new Double[iterations][9];//{110.0, 20.0, 20.0, 20.0, 20.0, 20.0, 20.0, 20.0, 20.0};
        for (int i = 1; i < 9; i++) {
            mur[0][i] = 20.0;
        }
        for (int i = 0; i < iterations; i++) {
            mur[i][0] = 110.0;
            mur[i][8] = 20.0;

        }
        this.C = new Double[10];
        this.setConstantes();
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

    private boolean isDone() {
        return done[0] && done[1] && done[2] && done[3] && done[4] && done[5] && done[6];
    }

    /**
     * Methode qui met a jour la temperature
     * du mur au point donne
     *
     * @param pos le lieu a mettre a jour
     * @return la nouvelle temperature calculee
     */
    public Double update(int pos) {
        return 0.0;///mur[pos] + C[pos + 1] * mur[pos + 1] + C[pos - 1] * mur[pos - 1] - (C[pos + 1] + C[pos - 1]) * mur[pos];
    }

    public double update(double tmp, double avant, double apres, int pos, int it) {
        double value = tmp + C[pos + 1] * apres + C[pos - 1] * avant - (C[pos + 1] + C[pos - 1]) * tmp;
        mur[it][pos] = value;
        return value;
    }

    /**
     * Methode qui permet de reinitialiser
     * les valeurs des temperatures des murs
     * apres la fin des calculs
     * (on fait T(x,t) = T(x,t+1) )
     */
    private void reInitMur(int pos,double newValue) {
        // mur[pos] = newValue;
    }

    /**
     * Lance la simulation
     */
    public void simule() {
        debut = new Date();
        createThreads(9);
        while (!isDone()) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        fin = new Date();
    }

    /**
     * Creer les differents threads necessaires pour faires les calculs
     *
     * @param nb
     */
    private void createThreads(int nb){
        Rdv[] listRdv = new Rdv[8];
        for (int i = 0; i < listRdv.length; i++) {
            listRdv[i] = new Rdv();
        }
        for (int j = 1; j < nb - 1; j++) {
            new Thread(new ThreadSimulation(j, this.max, listRdv[j - 1], listRdv[j])).start();
        }
    }

    private int round(Double value) {
        if (value == null) return -1;
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(0, RoundingMode.HALF_UP);
        return bd.intValue();
    }

    /**
     * Methode qui affiche l'etat it du mur
     */
    public void affiche() {
        System.out.println("Tableau des valeurs : \n");

        for (int i = 0; i < mur.length; i++) {
            for (int j = 0; j < mur[0].length; j++) {
                if (j == 6) System.out.print("-  " + round(mur[i][j - 1]) + " ");
                System.out.print(round(mur[i][j]) + " ");
            }
            System.out.print("\n");
        }
    }

    /**
     * Methode qui affiche l'etat it du mur
     */
    public void affiche(int k) {
        System.out.println("Tableau des valeurs : \n");

        for (int j = 0; j < mur[k].length; j++) {
            System.out.print(round(mur[k][j]) + " ");
        }
        System.out.print("\n");
    }

    /**
     * Thread qui calcul la nouvelle temperature a un point donne
     * ce Thread est synchronise par une barriere
     */
    private class ThreadSimulation implements Runnable {
        private int position;
        private Rdv rdvL;
        private Rdv rdvR;
        private int maxIt;

        public ThreadSimulation(int position, int iteration, Rdv left, Rdv right) {
            super();
            this.position = position;
            rdvL = left;
            rdvR = right;
            this.maxIt = iteration;
        }

        public void run() {
            double tmp = mur[0][position];
            double tmpXAvant = 0.0, tmpXApres = 0.0;
            int it;
            for (it = 0; it < maxIt; it++) {
                //System.out.printf("Thread %d started iteration %d\n",position,it);
                try {
                    synchronized (this) {
                        tmpXAvant = (position == 1) ? 110.0 : rdvL.getValueFromLeft(tmp);
                        rdvL.leftParti();
                        tmpXApres = (position == 7) ? 20.0 : rdvR.getValueFromRight(tmp);
                        rdvR.rightParti();
                        //System.out.printf("Call update(%f,%f,%f,%d,%d)\n",tmp,tmpXAvant,tmpXApres,position,it);
                        tmp = update(tmp, tmpXAvant, tmpXApres, position, it);
                        //System.out.println(tmp);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //System.out.printf("Thread %d DONE\n",position);
            done[position - 1] = true;

        }
    }

    /**
     * Classe Rendez vous
     */
    private class Rdv {
        private double alpha;
        private double beta;
        private boolean aArrive;
        private boolean bArrive;

        public Rdv() {
            aArrive = false;
            bArrive = false;
        }

        synchronized public double getValueFromLeft(double a) throws InterruptedException {
            alpha = a;
            aArrive();
            while (!bArrive) {
                wait();
            }
            notify();
            return beta;
        }

        synchronized public double getValueFromRight(double b) throws InterruptedException {
            beta = b;
            bArrive();
            while (!aArrive) {
                wait();
            }
            notify();
            return alpha;
        }

        synchronized public void aArrive() {
            this.aArrive = true;
        }

        synchronized public void bArrive() {
            this.bArrive = true;
        }

        synchronized public void leftParti() {
            this.aArrive = true;
        }

        synchronized public void rightParti() {
            this.bArrive = true;
        }


    }

    /**
     * Main de test
     *
     * @param args
     */
    public static void main(String[] args) {
        int k = 100;
        Simulateur simulateur = new Simulateur(k);

        //simulateur.affiche(0);
        simulateur.simule();
        while (!simulateur.isDone()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        simulateur.affiche();
        long diff = simulateur.fin.getTime() - simulateur.debut.getTime();
        LOGGER.log(Level.INFO, "Approximately " + Math.round((simulateur.it * DT) / 3600) + " hours simulated in " + diff + "ms");


    }


}
