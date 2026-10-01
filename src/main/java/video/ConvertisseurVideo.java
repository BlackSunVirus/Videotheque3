package video;

import exceptions.ConversionImpossibleException;
import modele.FichierVideo;

import java.io.IOException;

public class ConvertisseurVideo implements Runnable {

    private FichierVideo fichierVideo;
    private String formatCible;
    private Thread thread;

    public ConvertisseurVideo(FichierVideo fichierVideo, String formatCible) {
        this.fichierVideo = fichierVideo;
        this.formatCible = formatCible;
    }

    public void demarrer() {
        thread = new Thread(this);
        thread.setName("Convertisseur-" + fichierVideo.getTitre());
        thread.setDaemon(true);
        thread.start();
        System.out.println(Thread.currentThread().getName() + " - Lancement du thread de conversion.");
    }


    @Override
    public void run() {
        try {
            fichierVideo.convertir(formatCible);
        } catch (ConversionImpossibleException e) {
            System.out.println(e.getMessage());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
