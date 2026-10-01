package application;

import audio.ConvertisseurAudio;
import audio.LecteurMp3;
import exceptions.AlbumIntrouvableException;
import exceptions.DiscothequeVideException;
import exceptions.FichierAudioException;
import exceptions.SaisieInvalideException;
import modele.Album;
import modele.CompactDisque;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;

import modele.DisqueVinyle;
import modele.FichierNumerique;

import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Controller {
    public static Scanner scan = new Scanner(System.in);
    private Discotheque discotheque = new Discotheque();
    private LecteurMp3 lecteur;
    private static final List<String> formatAccepte = List.of("aac", "mp3", "flac", "wav");

    // Affiche le menu principal
    public void afficherMenu() {
        System.out.println("===== GESTION DE LA DISCOTHÈQUE =====");
        System.out.println("1. Ajouter un album");
        System.out.println("2. Lister tous les albums");
        System.out.println("3. Rechercher un album");
        System.out.println("4. Supprimer un album");
        System.out.println("5. Modifier la quantité d'un album");
        System.out.println("6. Écouter un album (MP3)");
        System.out.println("7. Arrêter la lecture");
        System.out.println("8. Convertir un album MP3 en AAC");
        System.out.println("0. Quitter");
        System.out.println("=====================================");
    }

    //Durée en MS. taille en Mo, f.extension c'est égal à "mp3, acc..."
    public void peuplerDiscotheque(String extension) {
        String pointExtension = "." + extension;
        File dossier = new File("media/");
        File[] fichiers = dossier.listFiles();
        if (fichiers==null) {
            return;
        }
        for(File f : fichiers) {
            if(f.getName().endsWith(pointExtension)) {
                FichierNumerique fn = new FichierNumerique(f.getName().replace(pointExtension, ""), f.getName().replace(pointExtension, "Auteur"),
                                      LocalDate.now(), 1, extension, f.length(), 0);
                discotheque.ajouterAlbum(fn);
            }
        }
    }

    public void ajouterAlbum() {
        try {
            int typeAlbum = saisieInt("Type d'album (1 = CD, 2 = Vinyle, 3 = Fichier numérique) :");
            if (typeAlbum < 1 || typeAlbum > 3)
                throw new SaisieInvalideException("Veuillez saisir un nombre valide");
            String nomAlbum = saisieNom("Saisissez le nom de l'album :");
            String nomAuteur = saisieNom("Saisissez le nom de l'auteur :");
            LocalDate dAnneeAlbum = saisieDate("Saisissez l'année de l'album jj/mm/aaaa :");

            int quantite = saisieInt("Saisissez le nombre d'album :");

            Album album = null;
            if(typeAlbum == 1) {
                album = ajouterCompactDisque(nomAlbum, nomAuteur, dAnneeAlbum, quantite);
            } else if (typeAlbum == 2) {
                album = ajouterDisqueVinyle(nomAlbum, nomAuteur, dAnneeAlbum, quantite);

            } else {
                album = ajouterFichierNumerique(nomAlbum, nomAuteur, dAnneeAlbum, quantite);
            }

            if(album!=null) {
                discotheque.ajouterAlbum(album);
                System.out.println("Album ajouté avec succès !");
            }

        } catch (SaisieInvalideException | DateTimeParseException e) {
            System.err.println(e.getMessage());
        }
    }

    public CompactDisque ajouterCompactDisque(String nomAlbum, String nomAuteur, LocalDate dAnneeAlbum, int quantite) {
        try {
            String numero = saisieNom("Saisissez le numéro du CD --> (CD-001) :");
            String type = saisieNom("Saisissez le type de CD --> Simple ou Double :");

            return new CompactDisque(nomAlbum, nomAuteur, dAnneeAlbum, quantite, numero, type);
        } catch (SaisieInvalideException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public DisqueVinyle ajouterDisqueVinyle(String nom, String auteur, LocalDate date, int quantite) {
        try {
            String numero = saisieNom("Numéro du vinyle :");
            int taille = saisieInt("Taille du vinyle (diamètre en cm : 17, 25 ou 30) : ");
            if (taille != 17 && taille != 25 && taille != 30)
                throw new SaisieInvalideException("Veuillez saisir un nombre valide (17, 25 ou 30)");
            return new DisqueVinyle(nom, auteur, date, quantite, numero, taille);
        } catch (SaisieInvalideException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public FichierNumerique ajouterFichierNumerique(String nom, String auteur, LocalDate date, int quantite) {
        try {
            String format = saisieNom("Format du fichier --> (AAC, FLAC, MP3, WAV) : ");
            if (!formatAccepte.contains(format.toLowerCase()))
                throw new SaisieInvalideException("Veuillez saisir un format de fichier valide");
            double taille = saisieDouble("Taille du fichier (en Mo) : ");
            int duree = saisieInt("Durée de l'album (en minute) : ");
            return new FichierNumerique(nom, auteur, date, quantite, format, taille, duree);
        } catch (SaisieInvalideException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void afficherDiscotheque() {
        discotheque.listerAblums();
    }

    public void afficherAlbum() throws AlbumIntrouvableException, DiscothequeVideException {
        String aNom = saisieNom("Veuillez saisir le nom de l'album à afficher");
        modele.Album a = discotheque.rechercherAlbum(aNom);
        System.out.println(a.toString());
    }

    public void supprimerAlbum() {
        String nom = saisieNom("Saisir le nom de l'album à supprimer :");
        discotheque.supprimerAlbum(nom);
    }

    public void modifierQuantiteAlbum(){
        String nom = saisieNom("Saisir le nom de l'album dont vous voulez modifier la quantité");
        System.out.println("Saisir la quantité");
        int qte = saisieInt("Saisir la quantité :");
        if(qte<0)throw new SaisieInvalideException("La quantité doit-être supérieur ou égale à 0");
        discotheque.modifierQuantiteAlbum(nom,qte);
    }

    public String saisieNom(String msg) throws SaisieInvalideException {
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

    public void lectureAlbum() throws AlbumIntrouvableException, SaisieInvalideException, FichierAudioException {
        String nom = saisieNom("Saisir le nom de l'album à écouter : ");
        Album a = discotheque.rechercherAlbum(nom);
        if (!(a instanceof FichierNumerique fn))
            throw new FichierAudioException("!! ERREUR : '" + a.getNom() + "est un " + a.getSupport() + " : seul un fichier numérique peut être lu");
        if (lecteur!=null && lecteur.estEnCours()) // TODO arreter la lecture en cours (si il y en a)
            arreterLecture();
        lecteur = new LecteurMp3(fn);
        lecteur.demarrer();
        System.out.println("Lecture de '" + fn.getNom() + "' lancée. Bonne écoute !");
    }

    public void arreterLecture() {
        if (lecteur==null || !lecteur.estEnCours())
            throw new FichierAudioException("Il n'y a pas de lecture en cours.");
        int tempsMs = lecteur.getPosition(); // TODO récuperer le temps de lecture
        lecteur.arreter(); // TODO arrete la lecture
        System.out.println("Lecture arrêtée à " + (tempsMs / 1000) + " secondes");
    }

    public void convertirFichierAudio() throws AlbumIntrouvableException, DiscothequeVideException, FichierAudioException, IOException, InterruptedException {
        String nom = saisieNom("Saisir le nom de l'album à convertir en AAC : ");
        Album a = discotheque.rechercherAlbum(nom);
        if (!(a instanceof FichierNumerique fn))
            throw new FichierAudioException("!! ERREUR : '" + a.getNom() + "est un " + a.getSupport() + " : seul un fichier numérique peut être converti");
        if (fn.getFormat().equalsIgnoreCase("aac"))
            throw new FichierAudioException("Le fichier est déjà au format AAC");
        if (!fn.getFormat().equalsIgnoreCase("mp3"))
            throw new FichierAudioException("Le fichier n'est pas au format mp3");
        //TODO convertir le fichier avec Ffmpeg
        ConvertisseurAudio.mp3VersAac(fn);
        System.out.println("Le fichier a été converti");
    }
}
