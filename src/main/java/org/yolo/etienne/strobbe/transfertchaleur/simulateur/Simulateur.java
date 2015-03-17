package org.yolo.etienne.strobbe.transfertchaleur.simulateur;

import org.yolo.etienne.strobbe.transfertchaleur.modele.Materiau;
import org.yolo.etienne.strobbe.transfertchaleur.tools.Constantes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Etienne Strobbe
 * Classe s'occupant de la simulation de transmission de chaleur. Tous les calculs sont effectués dans cette classe.
 */
public class Simulateur {
    private static final Logger LOGGER = Logger.getLogger("Simulateur");
    private Double[] mur;
    private BufferedWriter writer;
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
    //private MonitorObject monitorObject;

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
            @Override
            public void run() {
                if(++it >= max){
                    fin = new Date();
                    done = true;
                }
                /*if(it%6 == 0){
                    affiche();
                }*/
            }
        };
        this.barrierCalcul = new CyclicBarrier(7, barrierActionCalcul);
        this.barrierReInit = new CyclicBarrier(7, barrierActionReInit);
        try {
            this.writer = new BufferedWriter(new FileWriter(new File("out.txt")));
        } catch (IOException e) {
            e.printStackTrace();
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
        simulateur.affiche();
        long diff = simulateur.fin.getTime() - simulateur.debut.getTime();
        LOGGER.log(Level.INFO, "Approximately " + Math.round((simulateur.it * Constantes.DT) / 3600) + " hours simulated in " + diff + "ms");
        simulateur.closeWrite();


    }

    /**
     * Calcule la constante C pour la formule de calcul de température
     * selon un certain matériau
     *
     * @param materiau
     * @return
     */
    private Double getconstanteC(Materiau materiau) {
        double lambda = materiau.getLambda();
        double rho = materiau.getRho();
        double c = materiau.getC();
        return (lambda * Constantes.DT) / (rho * c * Constantes.DX * Constantes.DX);
    }

    private void setConstantes() {
        Double constanteMur = this.getconstanteC(Materiau.BRIQUE);
        Double constanteIso = this.getconstanteC(Materiau.LAINE_DE_VERRE);
        for(int i=0;i<6;i++){
            C[i] = constanteMur;
        }
        for (int i = 6; i < 9; i++) {
            C[i] = constanteIso;
        }
        System.out.println("C1 : " + constanteMur + " C2 : " + constanteIso);
    }

    /**
     * Methode qui met a jour la température
     * du mur au point donné
     *
     * @param pos le lieu a mettre a jour
     * @return la nouvelle température calculée
     */
    public Double update(int pos) {
        return mur[pos] + C[pos + 1] * mur[pos + 1] + C[pos - 1] * mur[pos - 1] - (C[pos + 1] + C[pos - 1]) * mur[pos];
    }

    /**
     * Methode qui permet de réinitialiser
     * les valeurs des températures des murs
     * après la fin des calculs
     * (on fait T(x,t) = T(x,t+1) )
     */
    private void reInitMur(int pos,double newValue) {
        mur[pos] = newValue;
    }



    public void simule() {
        createThreads(mur.length);
    }

    private void createThreads(int nb){
        for(int i=1; i<nb-1; i++){
            new Thread(new ThreadSimulation(i)).start();
        }
    }

    /**
     * Méthode qui renvoi la taille
     * de la simulation, ce qui
     * correspond à l'épaisseur du mur
     * que l'on simule
     * @return l'épaisseur du mur que l'on simule
     */
    public int sizeSimulation() {
        return mur.length;
    }

    private String round(Double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue() + "";
    }

    /**
     * Méthode qui affiche l'état courant du mur
     */
    public void affiche() {
        String res = "[ ";
        for (int i=0;i<mur.length-1;i++){
            if (i == 6) {
                res += " - " + round(mur[i - 1]) + ", ";
            }
            res += round(mur[i]) + " , ";
        }
        res += round(mur[mur.length - 1]) + " ]";
        //LOGGER.log(Level.INFO, mur.toString());
        System.out.println(res);
    }

    public void closeWrite() {
        try {
            this.writer.flush();
            this.writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Methode qui ajoute du texte au
     * fichier de sortie
     *
     * @param text
     */
    public void appendToFile(String text) {
        try {
            writer.write(text + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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


}
