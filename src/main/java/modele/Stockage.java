package modele;

import java.io.IOException;
import java.util.List;

public interface Stockage {
    void sauvegarder(List<Video> appareils) throws IOException;
    List<Video> charger() throws IOException;
}
