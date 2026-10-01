package modele;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VideoAvi extends FichierVideo {

    public VideoAvi(String titre, String realisateur, LocalDate dateSortie, int duree) {
        super(titre, realisateur, dateSortie, duree);
    }

    @Override
    public String getSupport() {
        return "AVI";
    }

    @Override
    protected List<String> optionEncodage() {
        List<String> options = new ArrayList<>();
        options.add("-c:v");
        options.add("mpeg4");
        options.add("-q:v");
        options.add("5");
        options.add("-c:a");
        options.add("libmp3lame");
        options.add("-b:a");
        options.add("192k");

        return options;
    }

    @Override
    public String toString() {
        return super.toString() + "AVI";
    }
}
