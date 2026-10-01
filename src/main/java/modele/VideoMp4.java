package modele;

import java.time.LocalDate;
import java.util.List;

public class VideoMp4 extends FichierVideo {


    public VideoMp4(String titre, String realistaeur, LocalDate dateSortie, int duree) {
        super(titre, realistaeur, dateSortie, duree);
    }

    @Override
    protected List<String> optionEncodage() {
        // TODO
        return List.of();
    }
}
