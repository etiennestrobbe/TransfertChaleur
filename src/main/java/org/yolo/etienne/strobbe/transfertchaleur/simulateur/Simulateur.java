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
     * @param it l'iteration en cours
     * @param tmp l'ancienne valeur en x
     * @param avant l'ancienne valeur en x-1
     * @param apres l'ancienne valeur en x+1
     * @return la nouvelle temperature calculee
     */
    public double update(double tmp, double avant, double apres, int pos, int it) {
        double value = tmp + C[pos + 1] * apres + C[pos - 1] * avant - (C[pos + 1] + C[pos - 1]) * tmp;
        mur[it][pos] = value;
        return value;
    }

    /**
     * Lance la simulation
     * - creation des threads
     * - calcul du temps d'execution
     */
    public void simule() {
        debut = new Date();
        createThreads();
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
     * on associe a chaque thread les deux objets Rdv necessaires
     *
     */
    private void createThreads() {
        Rdv rdv1 = new Rdv();
        Rdv rdv2 = new Rdv();
        for (int j = 1; j < 8; j++) {
            new ThreadSimulation(j, this.max, rdv1, rdv2).start();
            rdv1 = rdv2;
            rdv2 = new Rdv();
        }
    }

    private int round(Double value) {
        if (value == null) return -1;
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(0, RoundingMode.HALF_UP);
        return bd.intValue();
    }

    /**
     * Methode qui affiche l'etat du mur
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
    private class ThreadSimulation extends Thread {
        private int position;
        private Rdv rdvL;
        private Rdv rdvR;
        private int maxIt;
        private boolean through = false;

        /**
         * Constructeur
         *
         * @param position  representant la tranche du mur
         * @param iteration le nombre max d'iterations
         * @param left      l'objet Rdv a gauche du thread
         * @param right     l'objet Rdv a droite du thread
         */
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
                if (!through) {
                    if (position == 7) {
                        if ((int) tmp == 21) {
                            System.out.printf("Dernière tranche change de température -> time=%d s\n", Math.round((it * DT)));
                            through = true;
                        }
                    }
                }
                tmpXAvant = (position == 1) ? 110.0 : rdvL.echange(tmp);
                tmpXApres = (position == 7) ? 20.0 : rdvR.echange(tmp);
                if (position != 0 && position != 8) tmp = update(tmp, tmpXAvant, tmpXApres, position, it);
            }
            done[position - 1] = true;

        }
    }

    /**
     * Classe interne Rendez vous
     */
    private class Rdv {
        private double alpha;
        private double beta;
        private int enAttente;

        /**
         * Constructeur
         */
        public Rdv() {
            alpha = -1.0;
            beta = -1.0;
            enAttente = 0;
        }

        /**
         * Methode qui permet de faire un echange entre
         *  deux threads qui prennent rendez vous.
         * @param temp la valeur qu'un thread echange
         * @return la valeur que l'autre thread a donne
         */
        public synchronized double echange(double temp) {
            if (enAttente == 0) {
                enAttente = 1;
                alpha = temp;
                try {
                    wait();
                } catch (InterruptedException e) {
                }
                return beta;
            } else {
                enAttente = 0;
                beta = temp;
                notify();
                return alpha;
            }
        }

    }

    /**
     * Main de test
     *
     * @param args
     */
    public static void main(String[] args) {
        int k = 100000;
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
        //simulateur.affiche();
        long diff = simulateur.fin.getTime() - simulateur.debut.getTime();
        LOGGER.log(Level.INFO, "Approximately " + Math.round((k * DT) / 3600) + " hours simulated in " + diff + "ms");


    }


}
