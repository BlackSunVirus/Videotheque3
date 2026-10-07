package outils;

import exceptions.SaisieInvalideException;
import exceptions.StreamingException;
import modele.Dvd;
import modele.FichierVideo;
import modele.VideoMp4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Streamer implements Runnable {
    private final String NCAMERA = "HP True Vision HD Camera";
    private final String NMICRO = "Réseau de microphones (Technologie Intel® Smart Sound pour microphones numériques)";
    private final String NMICRO2 = "@device_cm_{33D9A762-90C8-11D0-BD43-00A0C911CE86}\\wave_{0BEAED99-CCAF-47DB-8D1A-CCF8E7CE5422}";
    private final String urlServeur;     // ex. "rtsp://192.168.1.50:8554"
    private volatile Process processus;  // ffmpeg en cours (partagé entre threads)
    private String fluxEnCours; // nom du chemin diffusé, ex. "film"
    private Thread thread;

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
        //System.out.println("Commande : " + commande);
        lancer(commande, nomFlux);
    }

    /** ffmpeg <entrée caméra selon le système> <encodage direct> <sortie> */
    public void diffuserCamera(String nomFlux, boolean son)
            throws StreamingException, SaisieInvalideException {

        if(estEnCours())
            throw new StreamingException("Un stream est déjà en cours");
        if (nomFlux == null || nomFlux.isEmpty()) {
            throw new SaisieInvalideException("Le format du flux ne peut pas être vide");
        }
        // flux valide
        if (!nomFlux.matches("[A-Za-z0-9_-]+")) {
            throw new SaisieInvalideException("Le format du flux est invalide");
        }
        List<String> commande = new ArrayList<>();
        commande.add("ffmpeg");

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            commande.add("-f"); commande.add("dshow");
            commande.add("-rtbufsize"); commande.add("100M");
            commande.add("-video_size"); commande.add("1280x720");
            commande.add("-framerate"); commande.add("30");

            if (son) {
                // Remplacer NMICRO par NMICRO2 si problème, NMICRO2 correspond à l'id unique DirectShow
                commande.add("-i");
                commande.add("video=" + NCAMERA + ":audio=" + NMICRO);
            } else {
                commande.add("-i");
                commande.add("video=" + NCAMERA);
            }
        } else if (os.contains("nix") || os.contains("nux") || os.contains("linux")) {
            commande.add("-f"); commande.add("v4l2");
            commande.add("-framerate"); commande.add("30");
            commande.add("-video_size"); commande.add("1280x720");
            commande.add("-i"); commande.add("/dev/video0");
            if (son) {
                commande.add("-f"); commande.add("alsa");
                commande.add("-i"); commande.add("default");
            }
        } else if (os.contains("mac")) {
            commande.add("-f"); commande.add("avfoundation");
            commande.add("-framerate"); commande.add("30");
            commande.add("-video_size"); commande.add("1280x720");
            commande.add("-i"); commande.add(son ? "0:0" : "0:none");
        } else {
            throw new StreamingException("Système d'exploitation non supporté.");
        }
        // encodage latence minimale
        commande.add("-c:v"); commande.add("libx264"); commande.add("-preset");
        commande.add("ultrafast"); commande.add("-tune"); commande.add("zerolatency");
        commande.add("-pix_fmt"); commande.add("yuv420p"); commande.add("-g");
        commande.add("30"); commande.add("-b:v"); commande.add("2000k");
        commande.add("-maxrate"); commande.add("2000k");
        commande.add("-bufsize"); commande.add("2000k");
        // Options audio
        if (son) {
            commande.add("-c:a"); commande.add("aac"); commande.add("-b:a");
            commande.add("128k"); commande.add("-ar"); commande.add("44100");
        } else {
            commande.add("-an");
        }
        // options réseau
        commande.addAll(optionsSortie(nomFlux));
        // URL finale
        String urlFinale = urlServeur;
        if (!urlFinale.endsWith("/")) {
            urlFinale += "/";
        }
        urlFinale += nomFlux;
        commande.add(urlFinale);
        // lancement
        this.fluxEnCours = nomFlux;
        //System.out.println("Commande : " +  commande);

        lancer(commande, nomFlux);
    }

    /** Arrête proprement ffmpeg : envoie "q" sur son entrée standard,
     attend 5 s au maximum, sinon destroy(). */
    public void arreter() {
        if (processus == null || !estEnCours()) {
            System.out.println("Pas de stream en cours");
            return;
        }
        try {
            processus.getOutputStream().write("q\n".getBytes());
            processus.getOutputStream().flush();
            //Si le délai de 5 secs est dépassé
            if(!processus.waitFor(5, TimeUnit.SECONDS))
                processus.destroy();
            this.processus = null;
            this.fluxEnCours = null;
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    public boolean estEnCours() { return processus != null && processus.isAlive(); }

    /** URL à donner aux spectateurs, ex. rtsp://.../film */
    public String getUrlLecture() { /* TODO */
        if(fluxEnCours == null)
            return "Aucun flux actif";
        try {
            System.out.println("Diffusion lancée.");
            String urlFlux = urlServeur + fluxEnCours;
            URI uri = new URI(urlFlux);
            URI nouvelleUri = new URI("http", uri.getUserInfo(),
                    uri.getHost(), 8888, uri.getPath(),
                    uri.getQuery(), uri.getFragment());

            String urlsLecture = "VLC / ffplay : " + urlFlux + "\nWeb : " + nouvelleUri.toString();
            return urlsLecture;
        } catch (URISyntaxException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    /** -f rtsp -rtsp_transport tcp rtsp://serveur:8554/nomFlux */
    private List<String> optionsSortie(String nomFlux) { /* TODO */
        List<String> options = new ArrayList<>();
        if (urlServeur.toLowerCase().startsWith("rtsp")) {
            options.add("-f");
            options.add("rtsp");
            options.add("-rtsp_transport");
            options.add("tcp");
        } else if (urlServeur.toLowerCase().startsWith("rtmp")) {
            options.add("-f");
            options.add("flv");
        }
        return options;
    }

    /** Entrée caméra : dshow, v4l2 ou avfoundation selon os.name */
    private List<String> optionsCamera() {
        List<String> options = new ArrayList<>();
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("windows")) {
            options.add("-f");
            options.add("rtsp");
            options.add("-rtsp_transport");
            options.add("tcp");
        } else if (os.startsWith("linux")) {
            options.add("-f");
            options.add("flv");
        } else if (os.startsWith("macos")) {
            options.add("-f");
            options.add("rtsp");
            options.add("-rtsp_transport");
            options.add("tcp");
        }
        return options;
    }

    /** Lance ffmpeg et lit sa sortie dans un thread daemon. */
    private void lancer(List<String> commande, String nomFlux)
            throws StreamingException {
        try {
            ProcessBuilder pb = new ProcessBuilder(commande);
            // Fusionne les flux d'erreurs de FFmpeg avec la sortie standard
            //pb.redirectErrorStream(true);
            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            pb.redirectError(ProcessBuilder.Redirect.DISCARD);
            this.processus = pb.start();

            //arretDemande = false;
            thread = new Thread(this);
            thread.setName("Lecteur-" + fluxEnCours);
            thread.setDaemon(true);
            thread.start();

            // attendre quelque secondes avant vérif
            Thread.sleep(2000);
            if(!processus.isAlive()) {
                int codeErreur = processus.exitValue();
                this.processus = null;
                this.fluxEnCours = null;
                throw new StreamingException("Erreur du stream : processus arrêté avec code : " + codeErreur);
            }

            System.out.println(getUrlLecture());
        } catch (IOException | InterruptedException e) {
            this.processus = null;
            this.fluxEnCours = null;
            Thread.currentThread().interrupt();
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        // try with resources pour fermer automatiquement le reader
        try (BufferedReader sortieProcessus = new BufferedReader(new InputStreamReader(processus.getInputStream()))) {
            String line;
            // lire tant que FFmpeg écrit
            while ((line = sortieProcessus.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Erreur de lecture du fichier.");
        }
    }

}