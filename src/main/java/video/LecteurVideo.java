package video;

import exceptions.VideoIntrouvableException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import modele.FichierVideo;
import modele.VideoAvi;
import modele.VideoMp4;

import java.io.FileInputStream;
import java.io.IOException;

public class LecteurVideo implements Runnable {
    private final FichierVideo video;
    private volatile Player player; // partagé entre deux threads
    private volatile boolean arretDemande;
    private Thread thread;

    /**
     * Vérifie le format (avi ou mp4) et l'existence du fichier,
     * sinon lève VideoIntrouvableException.
     */
    public LecteurVideo(FichierVideo video) throws VideoIntrouvableException {
        if (video.getChemin() == null) {
            throw new VideoIntrouvableException("Le fichier n'existe pas");
        }
        if (!(video instanceof VideoAvi avi | video instanceof VideoMp4 mp4)) {
            throw new VideoIntrouvableException("Veuillez saisir une video au bon format");
        }
        this.video = video;
    }

    /**
     * Crée le thread (daemon) et le démarre.
     * Ne fait rien si une lecture est déjà en cours.
     * 1 seule lecture à la fois
     */
    public void demarrer() {
        if (thread != null && thread.isAlive()) {
            return;
        }
        arretDemande = false;
        thread = new Thread(this);
        thread.setName("Lecteur-" + video.getTitre());
        thread.setDaemon(true);
        thread.start();
        System.out.println(Thread.currentThread().getName() + " lancement du thread");
    }

    /**
     * Exécuté DANS le thread : ouvre le flux, crée le Player, appelle play(). *
     */
    @Override
    public void run() {
        try (FileInputStream f = new FileInputStream(video.getChemin())) {
            player = new Player(f);
            if (arretDemande) {
                player.close();
                return;
            }
            player.play();
        } catch (IOException | JavaLayerException e) {
            System.err.println("\u001B[31m!! Erreur : \u001B[0m" + e.getMessage());
        }

    }

    /**
     * Arrête la lecture depuis un autre thread : arrêt de la video.
     */
    public void arreter() {
        arretDemande = true;
        if (player != null) {
            player.close();
        }
    }

    public boolean estEnCours() {
        //System.out.println("En cours --> " + Thread.currentThread().getName());
        return thread != null && thread.isAlive();
    }

    /**
     * Bloque le thread appelant jusqu'à la fin de la lecture (le programme appelant reste bloqué).
     */
    public void attendreFin() throws InterruptedException {
        thread.join();
    }

    /**
     * Position de lecture en millisecondes.
     */
    public int getPosition() {
        //Si Thread alive && player == null
        //Position de la lecture à 0 sec
        if(player == null) {
            return 0;
        }
        return player.getPosition();
    }

}
