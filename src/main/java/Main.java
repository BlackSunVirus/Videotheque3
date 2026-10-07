
import application.Controller;
import application.Videotheque;
import exceptions.*;
import modele.*;
import outils.Streamer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {

        Controller c = new Controller();
        StockageJson stockage = new StockageJson("donnees/videotheque.json");

        try {
            if(!c.charger(stockage)) {
                System.out.println("Aucune sauvegarde trouvée. Ajout des vidéos dans la vidéothèque");
                c.peuplerVideotheque("mp4");
                c.peuplerVideotheque("avi");
            }

        } catch (IOException | IllegalArgumentException e) {
            System.err.println("impossible de charger le fichier de sauvegarde" + e.getMessage());
        }

        System.out.println("Etat de la vidéothèque : ");
        try {
            c.listerVideos();
        } catch (VideothequeVideException e) {
            System.out.println("Erreur lors de la récupération des fichiers");
        }

        /** Pour tester la classe Streamer **/
        //Streamer s = new Streamer("rtsp://127.0.0.1:8554/");
        //FichierVideo v  = new VideoMp4("planet", "auteur", LocalDate.now(), 1);
        //FichierVideo v  = new VideoAvi("planet", "auteur", LocalDate.now(), 1);
        //s.diffuserFichier(v, "film", false);

        int choix = -1;

        while (true) {
            try {
                c.afficherMenu();
                System.out.print("Choix : ");
                choix = Controller.scan.nextInt();
                Controller.scan.nextLine();
                switch (choix) {
                    case 1:
                        c.ajouterVideo();
                        break;
                    case 2:
                        c.listerVideos();
                        break;
                    case 3:
                        c.rechercherVideo();
                        break;
                    case 4:
                        c.supprimerVideo();
                        break;
                    case 5:
                        c.lectureVideo();
                        break;
                    case 6:
                        c.convertirVideo();
                        break;
                    case 7 :
                        c.demarrerStream();
                        break;
                    case 8 :
                        c.diffuserCameraController();
                        break;
                    case 9 :
                        c.arretStreaming();
                        break;
                    case 0:
                        try {
                            c.sauvegarder(stockage);
                            System.out.println("Sauvegarde effectuée avec succès !");
                        } catch (IOException e) {
                            System.err.println("Erreur lors de l'enregistrement " + e);
                        }
                        System.out.println("Au revoir !");
                        Controller.scan.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("\u001B[31mChoix invalide, veuillez réessayer.\u001B[0m");
                }
            } catch (ConversionImpossibleException | LectureImpossibleException | VideoDejaExistanteException |
                     SaisieInvalideException | VideoIntrouvableException | VideothequeVideException e) {
                System.out.println(e.getMessage());
            } catch (InputMismatchException ime) {
                Controller.scan.nextLine();
                System.out.println("\u001B[31mSaisie non valide\u001B[0m");
            }
        }
    }
}
