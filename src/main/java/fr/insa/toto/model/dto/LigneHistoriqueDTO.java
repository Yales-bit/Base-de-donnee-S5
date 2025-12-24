package fr.insa.toto.model.dto;

import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) pour transporter les données d'une ligne
 * d'historique de match d'un joueur.
 * Ce n'est pas une classe miroir, juste un objet simple pour l'affichage.
 */
public class LigneHistoriqueDTO {

    private LocalDate date; // Date du match (optionnel si non stockée)
    private String nomTournoi; // Nom du tournoi
    private int numeroRonde; // Numéro de la ronde (1, 2, 3...)
    private String adversaires; // Surnom(s) des adversaires (ex: "Spider, Wolf")
    private String score; // Score formaté "12 - 10"
    private boolean victoire; // Indicateur de victoire pour affichage

    // Constructeur par défaut
    public LigneHistoriqueDTO() {
    }

    // Constructeur complet
    public LigneHistoriqueDTO(String nomTournoi, int numeroRonde, String adversaires, String score, boolean victoire) {
        this.nomTournoi = nomTournoi;
        this.numeroRonde = numeroRonde;
        this.adversaires = adversaires;
        this.score = score;
        this.victoire = victoire;
    }

    // Getters et Setters

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNomTournoi() {
        return nomTournoi;
    }

    public void setNomTournoi(String nomTournoi) {
        this.nomTournoi = nomTournoi;
    }

    public int getNumeroRonde() {
        return numeroRonde;
    }

    public void setNumeroRonde(int numeroRonde) {
        this.numeroRonde = numeroRonde;
    }

    public String getAdversaires() {
        return adversaires;
    }

    public void setAdversaires(String adversaires) {
        this.adversaires = adversaires;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public boolean isVictoire() {
        return victoire;
    }

    public void setVictoire(boolean victoire) {
        this.victoire = victoire;
    }

    @Override
    public String toString() {
        return "LigneHistoriqueDTO{" +
                "nomTournoi='" + nomTournoi + '\'' +
                ", numeroRonde=" + numeroRonde +
                ", adversaires='" + adversaires + '\'' +
                ", score='" + score + '\'' +
                ", victoire=" + victoire +
                '}';
    }
}
