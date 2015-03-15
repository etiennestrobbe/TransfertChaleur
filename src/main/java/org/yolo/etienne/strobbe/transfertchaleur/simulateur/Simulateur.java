package org.yolo.etienne.strobbe.transfertchaleur.simulateur;

import org.yolo.etienne.strobbe.transfertchaleur.modele.Materiau;
import org.yolo.etienne.strobbe.transfertchaleur.modele.Mur;
import org.yolo.etienne.strobbe.transfertchaleur.tools.Constantes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Etienne Strobbe
 * Classe s'occupant de la simulation de transmission de chaleur. Tous les calculs sont effectués dans cette classe.
 */
public class Simulateur {
    private static final Logger LOGGER = Logger.getLogger("Simulateur");
    private Mur murCourant;
    private Mur murSuivant;
    private BufferedWriter writer;
    private Double[] constantes;
    private MyBarrier barrier;
    private Runnable barrierAction;
    private boolean done = false;
    private Date debut;
    private Date fin;
    private MonitorObject monitorObject;

    /**
     * Constructeur
     */
    public Simulateur(final int iterations) {
        this.murCourant = new Mur();
        this.murCourant.setInit();
        this.murSuivant = new Mur();
        this.murSuivant.setInit();
        this.constantes = new Double[2];
        this.setConstantes();
        this.monitorObject = new MonitorObject();
        this.barrier = new MyBarrier();
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
        int k = 1000;
        Simulateur simulateur = new Simulateur(k);
        simulateur.debut = new Date();
        //simulateur.affiche();
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
        LOGGER.log(Level.INFO, "Approximately " + Math.round((k * Constantes.DT) / 3600) + " hours simulated in " + diff + "ms");
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
        this.constantes[0] = this.getconstanteC(Materiau.BRIQUE);
        this.constantes[1] = this.getconstanteC(Materiau.LAINE_DE_VERRE);
    }

    /**
     * Methode qui met a jour la température
     * du mur au point donné
     *
     * @param pos le lieu a mettre a jour
     * @return la nouvelle température calculée
     */
    public Double update(int pos) {
        if (pos != 0 && pos != murCourant.size() - 1) {
            Double newTemp;
            Double constanteC = ((murCourant.getMateriau(pos)) == Materiau.BRIQUE) ? constantes[0] : constantes[1];
            newTemp = murCourant.getTemp(pos) + constanteC * (murCourant.getTemp(pos + 1) + murCourant.getTemp(pos - 1) - 2 * murCourant.getTemp(pos));
            murSuivant.setTemp(pos, newTemp);
            barrier.await();
            return newTemp;
        }
        barrier.await();
        return murCourant.getTemp(pos);
    }

    private void reInitMur(int pos) {
        if (pos != 0 && pos != murCourant.size() - 1) {
            murCourant.setTemp(pos, murSuivant.getTemp(pos));
        }
        barrier.await();
    }

    /**
     * Methode qui permet de réinitialiser
     * les valeurs des températures des murs
     * après la fin des calculs
     * (on fait T(x,t) = T(x,t+1) )
     */
    public void reInit() {
        murCourant = murSuivant;
    }

    public void simule() {
        createThreads(murCourant.size());
    }

    private void createThreads(int nb){
        for(int i=0; i<nb; i++){
            new ThreadSimulation(i).start();
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
        return murCourant.size();
    }

    /**
     * Méthode qui affiche l'état courant du mur
     */
    public void affiche() {
        LOGGER.log(Level.INFO, murCourant.toString());
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

    private class ThreadSimulation extends Thread{
        private int position;

        public ThreadSimulation(int position){
            super();
            this.position = position;
        }
        public void run(){
            while (!done){
                update(this.position);
                reInitMur(this.position);
            }
        }
    }

    private class MyBarrier{

        private MyNotif notif = new MyNotif();

        public void await() {
            synchronized (monitorObject) {
                notif.addThread();
                while (!notif.isAllThreadDone()) {
                    try {
                        monitorObject.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                monitorObject.notifyAll();
            }
        }

    }

    private class MyNotif {
        private int nbThreadDone = 0;
        private int iteration = 0;
        private int max = 100000;

        public synchronized boolean isAllThreadDone(){
            if(this.nbThreadDone >= murCourant.size()){
                //murCourant = murSuivant;
                //
                if(++iteration >= max){
                    done = true;
                    fin = new Date();
                }

                //resetNb();
                return true;
            }
            return false;
        }

        public synchronized void resetNb(){
            this.nbThreadDone = 0;
        }

        public synchronized void addThread(){
            nbThreadDone++;
        }
    }

    private class MonitorObject{
    }

}
