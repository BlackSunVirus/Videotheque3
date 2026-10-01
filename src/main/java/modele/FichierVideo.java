package modele;

import exceptions.ConversionImpossibleException;
import exceptions.LectureImpossibleException;
import exceptions.SaisieInvalideException;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public abstract class FichierVideo extends Video implements Convertible {

    private String chemin;

    public FichierVideo(String titre, String realistaeur, LocalDate dateSortie, int duree, String chemin) {
        super(titre, realistaeur, dateSortie, duree);
        this.chemin = chemin;
    }

    // type File : pas sur de moi
    public File getFichier() {
        // TODO
        return null;
    }

    public Double getTaille() {
        // TODO
        return null;
    }



    @Override
    public FichierVideo convertir(String formatCible) throws ConversionImpossibleException, SaisieInvalideException {
        // TODO
        return null;
    }

    @Override
    public String getSupport() {
        // TODO
        return "";
    }

    @Override
    public void lire() throws LectureImpossibleException {
        // TODO
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
