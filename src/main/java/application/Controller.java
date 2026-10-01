package application;
import exceptions.SaisieInvalideException;
import modele.GestionVideotheque;
import modele.Video;

import modele.FichierVideo;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Controller {
    public static Scanner scan = new Scanner(System.in);
    private static final List<String> formatFichierNumAccepte = List.of("AVI", "MP4");

    // Affiche le menu principal
    public void afficherMenu() {
        System.out.println("===== GESTION DE LA VIDÉOTHÈQUE =====");
        System.out.println("1. Ajouter une video");
        System.out.println("2. Lister toutes les vidéos");
        System.out.println("3. Rechercher une vidéo");
        System.out.println("4. Supprimer une vidéo");
        System.out.println("5. Lire une video");
        System.out.println("6. Convertir une vidéo");
        System.out.println("0. Quitter");
        System.out.println("=====================================");
    }

    public void peuplerVideotheque(String extension) {
        String pointExtension = "." + extension;
        File dossier = new File("media/");
        File[] fichiers = dossier.listFiles();
        if (fichiers==null) {
            return;
        }
        for(File f : fichiers) {
            if(f.getName().endsWith(pointExtension)) {
                FichierVideo fv = new FichierVideo(f.getName().replace(pointExtension, ""), f.getName().replace(pointExtension, "Auteur"),
                        LocalDate.now(), 0) {
                };
                videotheque.ajouterVideo(fv);
            }
        }
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


    public void lectureVideo(GestionVideotheque videotheque) {
        String titre = saisieNom("Saisir le titre de la video à regarder : ");
        Video v = videotheque.rechercherVideo(titre);
        //if(!(v instanceof VideoAvi avi | v instanceof VideoMp4 mp4))

    }
}
