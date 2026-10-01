package application;

import exceptions.*;
import modele.Dvd;
import modele.FichierVideo;
import modele.GestionVideotheque;
import modele.Video;

import java.io.File;
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
        // TODO permettre les Video (Dvd...) et pour FichierVideo check si il existe bien dans media/
        for(Video video : videotheque) {
            // Ajoute uniquement les fichier de type FichierVideo ?? (instanceof)
            if(v instanceof FichierVideo) {
                File dossier = new File("media/");
                File[] fichiers = dossier.listFiles();
                if(fichiers==null)
                    throw new VideoIntrouvableException("Aucune vidéo dans le dossier");
                boolean existe = false;
                for(File f : fichiers) {
                    if(f.getName().endsWith(video.getSupport().toLowerCase()))
                        existe = true;
                }
                if(!existe)
                    throw new VideoIntrouvableException("Vidéo non trouvée dans le dossier");
            }
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
    public Video convertirVideo(String titre, String formatCible) throws VideoIntrouvableException, VideothequeVideException, ConversionImpossibleException, SaisieInvalideException {
        // TODO
        return null;
    }
}
