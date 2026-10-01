package application;

import exceptions.*;
import modele.*;
import video.ConvertisseurVideo;

import java.io.IOException;
import java.util.ArrayList;

public class Videotheque implements GestionVideotheque {

    private ArrayList<Video> videotheque;

    public Videotheque(ArrayList<Video> videotheque) {
        this.videotheque = videotheque;
    }

    public Videotheque() {
        this.videotheque = new ArrayList<>();
    }

    @Override
    public void ajouterVideo(Video v) throws VideoDejaExistanteException, SaisieInvalideException {
        // TODO
        for(Video video : videotheque) {
            // Ajoute uniquement les fichier de type FichierVideo ?? (instanceof)
            if(!(v instanceof FichierVideo))
                throw new SaisieInvalideException("Veuillez ajouter uniquement les fichiers vidéos à la vidéothèque");
            if(video.getTitre().equalsIgnoreCase(v.getTitre()))
                throw new VideoDejaExistanteException("La vidéo " + v.getTitre() + " existe déjà.");
            videotheque.add(v);
        }
    }

    @Override
    public void listerVideos() throws VideothequeVideException {
        // TODO
        if(videotheque.isEmpty())
            throw new VideothequeVideException("La vidéothèque est vide");
        System.out.println(this);
    }

    @Override
    public Video rechercherVideo(String titre) throws VideoIntrouvableException, VideothequeVideException {
        // TODO
        if(videotheque.isEmpty())
            throw new VideothequeVideException("La vidéothèque est vide");
        for(Video video : videotheque)
            if(video.getTitre().equalsIgnoreCase(titre))
                return video;
        throw new VideoIntrouvableException("La vidéo " + titre + " est introuvable.");
    }

    @Override
    public void supprimerVideo(String titre) throws VideoIntrouvableException, VideothequeVideException {
        // TODO
        Video v = rechercherVideo(titre);
        videotheque.remove(v);
    }

    @Override
    public void lireVideo(String titre) throws VideoIntrouvableException, VideothequeVideException, LectureImpossibleException {
        // TODO
        rechercherVideo(titre).lire();
    }

    @Override
    public Video convertirVideo(String titre, String formatCible) throws VideoIntrouvableException, VideothequeVideException, ConversionImpossibleException, SaisieInvalideException, IOException, InterruptedException {
        FichierVideo fichierAconvertir = (FichierVideo) rechercherVideo(titre);
        FichierVideo fichierConverti;
        if (!(fichierAconvertir instanceof Convertible))
            throw new ConversionImpossibleException("Le fichier n'est pas convertible.");
        if (!(fichierAconvertir.getSupport().equalsIgnoreCase("mp4") ||
                fichierAconvertir.getSupport().equalsIgnoreCase("avi")))
            throw new ConversionImpossibleException("Format du fichier invalide (mp4 ou avi seulement).");
        if (fichierAconvertir.getSupport().equalsIgnoreCase(formatCible))
            throw new ConversionImpossibleException("Le fichier est déjà au format " + formatCible + ".");
        fichierConverti = fichierAconvertir.convertir(formatCible);
        int index = videotheque.indexOf(fichierAconvertir);
        videotheque.set(index, fichierConverti);
        return fichierConverti;
    }
}
