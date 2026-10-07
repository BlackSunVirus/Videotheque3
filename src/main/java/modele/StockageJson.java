package modele;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.type.CollectionType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class StockageJson implements Stockage {

    private String path;
    // validateur requis par Jackson 3 pour sécuriser et autoriser le polymorphisme sur les packages
    private final PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
            .allowIfBaseType("modele.") // autorise toutes les classes sous le package modele
            .build();


    public StockageJson(String path) {
        this.path = path;
    }

    @Override
    public void sauvegarder(List<Video> videos) throws IOException {
        JsonMapper mapper = JsonMapper.builder().build();

        // Créer le tableau JSON
        File fichierDestination = new File(path);
        File dossierParent = fichierDestination.getParentFile();
        if (dossierParent != null && !dossierParent.exists()) {
            // Crée le dossier s'il n'existe pas
            Files.createDirectories(dossierParent.toPath());
        }

        CollectionType type =
                mapper.getTypeFactory()
                        .constructCollectionType(List.class, Video.class);

        mapper.writerFor(type)
                .withDefaultPrettyPrinter()
                .writeValue(fichierDestination, videos);

        // sauvegarde la liste avec une indentation propre
//        System.out.println(videos.get(0).getClass());
//        mapper.writerWithDefaultPrettyPrinter().writeValue(fichierDestination, videos);
    }

    @Override
    public List<Video> charger() throws IOException {
        File fichier = new File(path);

        // Si le fichier n'existe pas, on s'arrête ici
        if (!fichier.exists()) {
            return new ArrayList<>();
        }

        // création mapper via builder
        JsonMapper mapper = JsonMapper.builder().build();

        // reconstitution d'une liste de Video
        CollectionType lType = mapper.getTypeFactory().constructCollectionType(List.class, Video.class);

        // jackson 3 gère tous les types (LocalDate)
        return mapper.readValue(fichier, lType);
    }
}
