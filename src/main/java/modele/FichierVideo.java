package modele;

import exceptions.ConversionImpossibleException;
import exceptions.LectureImpossibleException;
import exceptions.SaisieInvalideException;
import outils.Ffmpeg;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class FichierVideo extends Video implements Convertible {

    private String chemin;

    public FichierVideo(String titre, String realisateur, LocalDate dateSortie, int duree) {
        super(titre, realisateur, dateSortie, duree);
        String mediaDir = "media/";
        this.chemin = mediaDir + titre + "." + this.getSupport();
    }

    public File getFichier() {
        return new File(this.chemin);
    }

    public Double getTaille() {
        // TODO
        return null;
    }

    @Override
    public FichierVideo convertir(String formatCible) throws ConversionImpossibleException, IOException, InterruptedException {
        File fichierEntree = getFichier();
        File fichierSortie;
        String nomFichierEntree = getChemin();
        String nomFichierSortie = nomFichierEntree.substring(0, nomFichierEntree.length() - 3);
        FichierVideo fichierConverti;

        switch (formatCible.toLowerCase()) {
            case "mp4":
                nomFichierSortie += "mp4";
                fichierConverti = new VideoMp4(getTitre(), getRealisateur(), getDateSortie(), getDuree());
                break;
            case "avi":
                nomFichierSortie += "avi";
                fichierConverti = new VideoAvi(getTitre(), getRealisateur(), getDateSortie(), getDuree());
                break;
            default:
                throw new ConversionImpossibleException("Conversion impossible");
        }

        fichierSortie = new File(nomFichierSortie);
        System.out.println("Début de la conversion...");
        // TODO Lancer un thread
        Ffmpeg.convertir(fichierEntree, fichierSortie, fichierConverti.optionEncodage());
        System.out.println("Conversion terminée !");
        return fichierConverti;
    }

    @Override
    public String getSupport() {
        // TODO
        return "";
    }

    //Vérifie que le fichier existe sinon erreur
    //Lance ffplay dans un thread daemon
    //

    @Override
    public void lire() throws LectureImpossibleException {
        // TODO
        //if()
    }

    protected abstract List<String> optionEncodage();

    public String getChemin() {
        return chemin;
    }

    public void setChemin(String chemin) {
        this.chemin = chemin;
    }

    @Override
    public String toString() {
        return "FichierVideo{" +
                "chemin='" + chemin + '\'' +
                '}';
    }

}
