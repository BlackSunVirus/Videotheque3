package application;

import exceptions.*;
import modele.GestionVideotheque;
import modele.Video;

import java.util.ArrayList;

public class Videotheque implements GestionVideotheque {

    private ArrayList<Video> videotheque;

    @Override
    public void ajouterVideo(Video v) throws VideoDejaExistanteException, SaisieInvalideException {
        // TODO
    }

    @Override
    public void listerVideos() throws VideothequeVideException {
        // TODO
    }

    @Override
    public Video rechercherVideo(String titre) throws VideoIntrouvableException, VideothequeVideException {
        // TODO
        return null;
    }

    @Override
    public void supprimerVideo(String titre) throws VideoIntrouvableException, VideothequeVideException {
        // TODO
    }

    @Override
    public void lireVideo(String titre) throws VideoIntrouvableException, VideothequeVideException, LectureImpossibleException {
        // TODO
    }

    @Override
    public Video convertirVideo(String titre, String formatCible) throws VideoIntrouvableException, VideothequeVideException, ConversionImpossibleException, SaisieInvalideException {
        // TODO
        return null;
    }
}
