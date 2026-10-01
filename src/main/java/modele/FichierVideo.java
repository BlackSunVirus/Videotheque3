package modele;

import exceptions.ConversionImpossibleException;
import exceptions.LectureImpossibleException;
import exceptions.SaisieInvalideException;
import exceptions.VideoIntrouvableException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

public abstract class FichierVideo extends Video implements Convertible {

    private String chemin;

    public FichierVideo(String titre, String realistaeur, LocalDate dateSortie, int duree, String chemin) {
        super(titre, realistaeur, dateSortie, duree);
        this.chemin = chemin;
    }

    // type File -> pas necessaire comme le chemin est dans les attributs (getFile)
    public File getFichier() {
        // TODO
        return null;
    }

    //Récupère la taille du fichier en Mo
    public Double getTaille(File file) {
        // TODO
        if(!file.exists()) {
            throw new VideoIntrouvableException("\u001B[31mFichier introuvable.\u001B[0m");
        }

        double tailleMo=0;
        try {
            long sizeInBytes = Files.size(file.toPath());
            tailleMo = (sizeInBytes / (1024.0*1024.0));
        } catch(IOException e) {
            System.out.println("\u001B[31mImpossible de lire la taille du fichier " +file.getName()+"\u001B[0m");
        }
        return tailleMo;
    }



    @Override
    public FichierVideo convertir(String formatCible) throws ConversionImpossibleException, SaisieInvalideException {
        // TODO
        return null;
    }


    //Vérifie que le fichier existe sinon erreur
    //Lance ffplay (outils) dans un thread daemon
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
