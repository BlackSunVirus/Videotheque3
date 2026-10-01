package modele;

import java.time.LocalDate;
import java.util.List;

public class VideoAvi extends FichierVideo {

    public VideoAvi(String titre, String realistaeur, LocalDate dateSortie, int duree) {
        super(titre, realistaeur, dateSortie, duree);
    }

    @Override
    protected List<String> optionEncodage() {
        // TODO
        return List.of();
    }
}
