package modele;

import exceptions.LectureImpossibleException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// 1. ANNOTATIONS : Restent sur le package com.fasterxml (Spécificité Jackson 3)
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
        //visible = true // Indispensable pour forcer Jackson à écrire le type sans les getters
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Dvd.class, name = "dvd"),
        @JsonSubTypes.Type(value = VideoMp4.class, name = "mp4"),
        @JsonSubTypes.Type(value = VideoAvi.class, name = "avi"),
})

public abstract class Video implements Lisible {

    private String titre;
    private String realisateur;
    private LocalDate dateSortie;
    private int duree;

    public Video() {
    }

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
