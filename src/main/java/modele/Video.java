package modele;

import exceptions.LectureImpossibleException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class Video implements Lisible {

    private String titre;
    private String realisateur;
    private LocalDate dateSortie;
    private int duree;

    public Video(String titre, String realisateur, LocalDate dateSortie, int duree) {
        this.titre = titre;
        this.realisateur = realisateur;
        this.dateSortie = dateSortie;
        this.duree = duree;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getRealisateur() {
        return realisateur;
    }

    public void setRealisateur(String realisateur) {
        this.realisateur = realisateur;
    }

    public LocalDate getDateSortie() {
        return dateSortie;
    }

    public void setDateSortie(LocalDate dateSortie) {
        this.dateSortie = dateSortie;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public abstract String getSupport();

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String sDate = LocalDate.now().format(formatter);
        return "Vidéo : " + titre +
                " (" + duree + "mins) réalisée par " +
                realisateur + " le " + sDate;
    }

    @Override
    public abstract void lire() throws LectureImpossibleException ;


}
