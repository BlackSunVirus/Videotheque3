
import application.Controller;
import audio.LecteurMp3;
import exceptions.*;
import modele.Musique;

import java.io.IOException;
import java.util.InputMismatchException;

public class Main {
    public static void main(String[] args) {

        Controller c = new Controller();
        //Ajoute les fichiers mp3 automatiquement à la discothèque
        c.peuplerDiscotheque("aac");
        //static LecteurMp3 lecteur = new LecteurMp3(null);
        int choix = -1;

        while (true) {
            try {
                c.afficherMenu();
                System.out.print("Choix :");
                choix = Controller.scan.nextInt();
                Controller.scan.nextLine();
                switch (choix) {
                    case 1:
                        //TODO
                        break;
                    case 2:
                        //TODO
                        break;
                    case 3:
                        //TODO
                        break;
                    case 4:
                        //TODO
                        break;
                    case 5:
                        //TODO
                        break;
                    case 6:
                        //TODO
                        break;
                    case 7:
                        //TODO
                        break;
                    case 8:
                        //TODO
                        break;
                    case 0:
                        System.out.println("Au revoir !");
                        Controller.scan.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("\u001B[31mChoix invalide, veuillez réessayer.\u001B[0m");
                }
            } catch (AlbumDejaExistantException | AlbumIntrouvableException | DiscothequeVideException |
                     SaisieInvalideException | FichierAudioException | IOException | InterruptedException e) {
                System.out.println(e.getMessage());
            } catch (InputMismatchException ime) {
                Controller.scan.nextLine();
                System.out.println("\u001B[31mSaisie non valide\u001B[0m");
            }
        }
    }


}
