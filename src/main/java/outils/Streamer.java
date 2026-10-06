package outils;

import exceptions.SaisieInvalideException;
import exceptions.StreamingException;
import modele.Dvd;
import modele.FichierVideo;
import modele.VideoMp4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Streamer {

    private final String urlServeur;     // ex. "rtsp://192.168.1.50:8554"
    private volatile Process processus;  // ffmpeg en cours (partagé entre threads)
    private String fluxEnCours;          // nom du chemin diffusé, ex. "film"

    public Streamer(String urlServeur) { this.urlServeur = urlServeur; }

    /** ffmpeg -re [-stream_loop -1] -i fichier <options du format> <sortie> */
    // nomFlux --> film par exemple
    public void diffuserFichier(FichierVideo video, String nomFlux, boolean boucle)
            throws StreamingException, SaisieInvalideException {

        if(!(video instanceof FichierVideo)) {
            throw new SaisieInvalideException("Erreur : Seuls les fichiers numériques peuvent être diffusés");
        }

        if (estEnCours()) {
            throw new StreamingException("Un stream est déjà en cours !");
        }

        // Entrées valides
        if (nomFlux == null || nomFlux.isEmpty()) {
            throw new SaisieInvalideException("Le format du flux ne peut pas être vide");
        }

        // flux valide
        if (!nomFlux.matches("[A-Za-z0-9_-]+")) {
            throw new SaisieInvalideException("Le format du flux est invalide");
        }

        // Chemin valide
        if (video.getChemin() == null || video.getChemin().isEmpty()) {
            throw new SaisieInvalideException("Le chemin du fichier vidéo est introuvable");
        }

        List<String> commande = new ArrayList<>();
        commande.add("ffmpeg");
        commande.add("-re");

        if (boucle) {
            commande.add("-stream_loop");
            commande.add("-1");
        }

        commande.add("-i");
        commande.add(video.getChemin());

        List<String> options = video.getOptionsStreaming();
        if (options == null || options.isEmpty()) {
            throw new StreamingException("Erreur : les options de streaming sont vides");
        }
        commande.addAll(options);

        //ajout config réseau (en fonction du protocole)
        commande.addAll(optionsSortie(nomFlux));

        //ce flux devient actif
        this.fluxEnCours = nomFlux;


        String urlFinale = urlServeur;
        if (!urlFinale.endsWith("/")) {
            urlFinale += "/";
        }
        urlFinale += nomFlux;

        commande.add(urlFinale);

        System.out.println("Commande : " + commande);

        lancer(commande, nomFlux);

        try {
            ProcessBuilder pb = new ProcessBuilder(commande);

            // Fusionne les flux d'erreurs de FFmpeg avec la sortie standard
            pb.redirectErrorStream(true);

            Process processus = pb.start();

            // IMPORTANT : On lit les logs FFmpeg dans un thread séparé
            // pour éviter que FFmpeg ne se fige (mémoire tampon saturée)
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(processus.getInputStream()))) {
                    String ligne;
                    while ((ligne = reader.readLine()) != null) {
                        System.out.println("[FFmpeg] " + ligne);
                    }
                } catch (IOException e) {
                    System.err.println("Erreur de lecture du flux FFmpeg : " + e.getMessage());
                }
            }).start();

        } catch (IOException e) {
            throw new StreamingException("Impossible de lancer le processus d'encodage FFmpeg : " + e.getMessage());
        }
    }

    /** ffmpeg <entrée caméra selon le système> <encodage direct> <sortie> */
    public void diffuserCamera(String nomFlux)
            throws StreamingException, SaisieInvalideException { /* TODO */ }

    /** Arrête proprement ffmpeg : envoie "q" sur son entrée standard,
     attend 5 s au maximum, sinon destroy(). */
    public void arreter() { /* TODO */ }

    public boolean estEnCours() { return processus != null && processus.isAlive(); }

    /** URL à donner aux spectateurs, ex. rtsp://.../film */
    public String getUrlLecture() { /* TODO */
        return "";
    }

    /** -f rtsp -rtsp_transport tcp rtsp://serveur:8554/nomFlux */
    private List<String> optionsSortie(String nomFlux) { /* TODO */
        List<String> options = new ArrayList<>();
        if (urlServeur.toLowerCase().startsWith("rtsp")) {
            options.add("-f");
            options.add("rtsp");
            options.add("-rtsp_transport");
            options.add("-tcp");
        } else if (urlServeur.toLowerCase().startsWith("rtmp")) {
            options.add("-f");
            options.add("flv");
        }

        String base = urlServeur.endsWith("/") ? urlServeur : urlServeur + "/";
        options.add(base + nomFlux);
        return options;
    }

    /** Entrée caméra : dshow, v4l2 ou avfoundation selon os.name */
    private List<String> optionsCamera() { /* TODO */
        return List.of();
    }

    /** Lance ffmpeg et lit sa sortie dans un thread daemon. */
    private void lancer(List<String> commande, String nomFlux)
            throws StreamingException { /* TODO */ }
}