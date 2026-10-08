package application;
import at.favre.lib.crypto.bcrypt.BCrypt;
import exceptions.*;
import modele.*;
import outils.Streamer;
import video.LecteurVideo;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Stream;

import static service.Authentification.connexion;

public class Controller {
    public static Scanner scan = new Scanner(System.in);
    private Videotheque videotheque = new Videotheque();
    private LecteurVideo lecteur;
    private Streamer streamer;
    private static final List<String> formatFichierNumAccepte = List.of("AVI", "MP4");
    private static final String urlServeur = "rtsp://172.16.120.28:8554/";

    // Affiche le menu principal
    public void afficherMenu() {
        System.out.println("===== GESTION DE LA VIDÉOTHÈQUE =====");
        System.out.println("1. Ajouter une video");
        System.out.println("2. Lister toutes les vidéos");
        System.out.println("3. Rechercher une vidéo");
        System.out.println("4. Supprimer une vidéo");
        System.out.println("5. Lire une video");
        System.out.println("6. Convertir une vidéo");
        System.out.println("7. Démarrer le streaming d'une vidéo");
        System.out.println("8. Diffuser la webcam en streaming");
        System.out.println("9. Arrêter le streaming");
        System.out.println("0. Quitter");
        System.out.println("=====================================");
    }

    public static void authentification(){
        System.out.println("Entrez le mot de passe pour accéder au programme : ");
        String mdp = scan.nextLine();

        //String mdpHash = hasherMdp(mdp);
        if(!connexion(mdp)){
            System.out.println("Mot de passe invalide. Adieu !");
            System.exit(0); //Peut-être un peu radical faire une boucle ? while(false) ou compteur de 3 !
        }

    }

    public void peuplerVideotheque(String extension) {
        String pointExtension = "." + extension;
        File dossier = new File("media/");
        File[] fichiers = dossier.listFiles();

        if (fichiers == null) {
            return;
        }

        if (extension.equalsIgnoreCase("mp4")) {
            for (File f : fichiers) {
                if (f.getName().endsWith(pointExtension)) {

                    FichierVideo fv = new VideoMp4(
                            f.getName().replace(pointExtension, ""),
                            f.getName().replace(pointExtension, "Auteur"),
                            LocalDate.now(),
                            0
                    );

                    videotheque.ajouterVideo(fv);
                }
            }
            return;
        }

        if (extension.equalsIgnoreCase("avi")) {
            for (File f : fichiers) {
                if (f.getName().endsWith(pointExtension)) {

                    FichierVideo fv = new VideoAvi(
                            f.getName().replace(pointExtension, ""),
                            f.getName().replace(pointExtension, "Auteur"),
                            LocalDate.now(),
                            0
                    );

                    videotheque.ajouterVideo(fv);
                }
            }
            return;
        }

        System.out.println("Problème d'extension de fichier (AVI/MP4)");
    }

    public void ajouterVideo() {
        try {
            int typeVideo = saisieInt("Type de vidéo (1 = DVD, 2 = FichierVideo) :");
            if(typeVideo < 1 || typeVideo > 2)
                throw new SaisieInvalideException("Veuillez saisir un nombre valide");
            String titreVideo = saisieString("Saisissez le titre de la vidéo :");
            String realisateurVideo = saisieString("Saisissez le nom du réalisateur :");
            LocalDate dateSortie = saisieDate("Saisissez l'année de sortie jj/mm/aaaa :");
            int dureeVideo = saisieInt("Saisissez la durée de la vidéo (minutes) :");

            Video video = null;
            if(typeVideo == 1) {
                video = ajouterDvd(titreVideo, realisateurVideo, dateSortie, dureeVideo);
            } else {

                video = ajouterFichierVideo(titreVideo, realisateurVideo, dateSortie, dureeVideo);
            }
            if(video==null) {
                throw new SaisieInvalideException("Impossible d'ajouter la vidéo");
            }
            videotheque.ajouterVideo(video);
            System.out.println("Vidéo ajoutée avec succès !");
        } catch (SaisieInvalideException | DateTimeParseException e) {
            System.err.println(e.getMessage());
        }
    }

    private Dvd ajouterDvd(String titre, String realisateur, LocalDate dateSortie, int duree) {
        try {
            String numeroDvd = saisieString("Saisissez le numéro du DVD --> DVD-001 :");
            int zoneDvd = saisieInt("Saisissez la zone du DVD :");

            return new Dvd(titre, realisateur, dateSortie, duree, numeroDvd, zoneDvd);
        } catch(SaisieInvalideException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    private Video ajouterFichierVideo(String titre, String realisateur, LocalDate dateSortie, int duree) {
        try {
            String extension = saisieString("Saisissez l'extension du fichier --> mp4/avi :");
            if(extension.equalsIgnoreCase("mp4")) {
                VideoMp4 mp4 = new VideoMp4(titre, realisateur, dateSortie, duree);
                return mp4;
            }
            if(extension.equalsIgnoreCase("avi")) {
                VideoAvi avi = new VideoAvi(titre, realisateur, dateSortie, duree);
                return avi;
            }
            throw new SaisieInvalideException("L'extension de fichier est invalide --> (avi/mp4)");

        } catch(SaisieInvalideException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public void listerVideos() {
        System.out.println(videotheque.listerVideos());
    }

    public void rechercherVideo() {
        String titreVideo = saisieString("Veuillez saisir le nom de la vidéo à afficher :");
        Video v = videotheque.rechercherVideo(titreVideo);
        System.out.println(v.toString());
    }

    public void supprimerVideo() {
        String titreVideo = saisieString("Veuillez saisir le nom de la vidéo à supprimer :");
        videotheque.supprimerVideo(titreVideo);
    }

    public String saisieString(String msg) throws SaisieInvalideException {
        System.out.println(msg);
        String nom = scan.nextLine();
        if (nom.isEmpty()) {
            throw new SaisieInvalideException("Saisie invalide");
        }
        return nom;
    }

    public LocalDate saisieDate(String msg) throws SaisieInvalideException {
        while(true) {
            System.out.print(msg);
            String saisie = scan.nextLine();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            try {
                LocalDate date = LocalDate.parse(saisie, formatter);
                if (date.isAfter(LocalDate.now()) || date.isBefore(LocalDate.of(1886, 01, 01))) {
                    throw new SaisieInvalideException("Veuillez saisir une année de sortie valide (1886-aujourd'hui)");
                }
                return date;
            } catch (SaisieInvalideException | DateTimeParseException e) {
                System.out.println("\u001B[31mDate non valide --> jj/mm/aaaa (1886-aujourd'hui)\u001B[0m");
            }
        }
    }

    public static int saisieInt(String msg) {
        while (true) {
            System.out.print(msg);
            String saisie = scan.nextLine();
            try {
                return Integer.parseInt(saisie);
            } catch (NumberFormatException e) {
                System.out.println("\u001B[31mVeuillez entrer un nombre entier valide\u001B[0m");
            }
        }
    }

    public static double saisieDouble(String msg) {
        while (true) {
            System.out.print(msg);
            String saisie = scan.nextLine();
            try {
                return Double.parseDouble(saisie);
            } catch (NumberFormatException e) {
                System.out.println("\u001B[31mVeuillez entrer un nombre entier valide\u001B[0m");
            }
        }
    }

    public void convertirVideo() {
        try {
            String titre = saisieString("Saisir le nom du fichier à convertir : ");
            String format = saisieString("Saisir le format de conversion : ");
            videotheque.convertirVideo(titre, format);
        } catch (VideoIntrouvableException | VideothequeVideException | ConversionImpossibleException |
                 SaisieInvalideException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public void lectureVideo() {
        String titre = saisieString("Saisir le titre de la video à regarder : ");
        videotheque.lireVideo(titre);
    }

    public void demarrerStream() {
        String titre = null;
        String extension = null;
        boolean boucle = false;
        String flux = "";
        try {
            titre = saisieString("Saisir le nom de la vidéo à diffuser : ");
            extension = saisieString("Saisir l'extension de la vidéo à diffuser :");
            String reponse = saisieString("Voulez-vous lire la vidéo en boucle ? (oui/non)");
            if(reponse.equalsIgnoreCase("oui")) {
                boucle = true;
            }
            flux = saisieString("Quel répertoire de Stream souhaitez-vous ? --> monsite.com/live");
//            if(flux.equalsIgnoreCase("rtsp")) {
//                flux = "rtsp";
//            }else if (flux.equalsIgnoreCase("rtmp")) {
//                flux = "rtmp";
//            }

        } catch (SaisieInvalideException | VideoIntrouvableException | VideothequeVideException | StreamingException  e) {
            System.out.println(e.getMessage());
        }
        //TODO
        // FichierVideo video, String nomFlux, boolean boucle
        // rtsp ou rtmp // if "rtsp rtsp://172.16.120.28:554/live" | "flv rtmp://172.16.120.28:1935/live"
        Video video = videotheque.rechercherVideoEx(titre, extension);
        if(video == null) {
            throw new VideoIntrouvableException("La vidéo est introuvable");
        }
        if(!(video instanceof FichierVideo))
            throw new SaisieInvalideException("Veuillez saisir un fichier vidéo");
        if(streamer!=null && streamer.estEnCours())
            streamer.arreter();
        streamer = new Streamer(urlServeur);
        streamer.diffuserFichier((FichierVideo)video, flux, boucle);
        System.out.println("Streaming de " + video.getTitre() + " lancée. Bon visionage");

        }
    public void diffuserCameraController() {

        String flux = null;
        String reponse = null;
        boolean son = false;
        try {
            flux = saisieString("Quel répertoire de Stream souhaitez-vous ? --> monsite.com/live");
            reponse = saisieString("Voulez-vous lire la vidéo en boucle ? (oui/non)");
            if(reponse.equalsIgnoreCase("oui")) {
                son = true;
            }
        } catch (SaisieInvalideException e) {
            throw new SaisieInvalideException(e.getMessage());
        }

        if(streamer!=null && streamer.estEnCours())
            streamer.arreter();
        streamer = new Streamer(urlServeur);
        streamer.diffuserCamera(flux, son);
        System.out.println("Streaming de la caméra lancée. Bonne diffusion");

    }

    public void arretStreaming() {
        boolean arret = false;
        if(streamer != null || !streamer.estEnCours())
            throw new StreamingException("Aucun stream en cours");
        try {
            String choix = saisieString("Voulez-vous vraiment arrêter le streaming ? (O/N)");
            if (choix.equalsIgnoreCase("O")) {
               streamer.arreter();
               System.out.println("Arrêt en cours");
            }
        } catch (SaisieInvalideException e) {
            System.out.println("Saisie invalide : " + e.getMessage());
        }
        //TODO
//        if(arret == true) {
//            try {
//                Streamer.arreter();
//            } catch (StreamingException e) {
//                System.out.println(e.getMessage());
//            }
//        }

    }

    public void sauvegarder(Stockage s) throws IOException {
        try {
            s.sauvegarder(videotheque.listerVideos());
        } catch (IOException e) {
            System.err.println(e.getMessage());;
        }
    }

    public boolean charger(Stockage s) throws IOException, IllegalArgumentException {

        try {
            List<Video> videosACharger = s.charger();
            if(videosACharger == null || videosACharger.isEmpty()) {
                return false;
            }
            videotheque.clear();
            for (Video v : videosACharger) {
                videotheque.ajouterVideo(v);
            }
            return true;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }



//    public static String hashPassword(String password) {
//        // Définir
//        int logRounds = 12;
//
//        String salt = BCrypt.gensalt(logRounds);
//        return BCrypt.hashpw(password, salt);
//    }
}
