package modele;

import java.time.LocalDate;

public abstract class Video implements Lisible {

    private String titre;
    private String realistaeur;
    private LocalDate dateSortie;
    private int duree;

    public Video(String titre, String realistaeur, LocalDate dateSortie, int duree) {
        this.titre = titre;
        this.realistaeur = realistaeur;
        this.dateSortie = dateSortie;
        this.duree = duree;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getRealistaeur() {
        return realistaeur;
    }

    public void setRealistaeur(String realistaeur) {
        this.realistaeur = realistaeur;
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
        return "Video{" +
                "titre='" + titre + '\'' +
                ", realistaeur='" + realistaeur + '\'' +
                ", dateSortie=" + dateSortie +
                ", duree=" + duree +
                '}';
    }
}
