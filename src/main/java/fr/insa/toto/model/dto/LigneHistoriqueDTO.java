package fr.insa.toto.model.dto;

import java.time.LocalDate;

/**
 * DTO pour transporter les données d'une ligne d'historique de match.
 * Gère Victoire, Défaite et Égalité.
 */
public class LigneHistoriqueDTO {

    private LocalDate date;
    private String nomTournoi;
    private int numeroRonde;
    private String adversaires;
    private String score;
    
    // MODIFICATION : On remplace 'boolean victoire' par 'int resultat'
    // 1 = Victoire, 0 = Égalité, -1 = Défaite
    private int resultat;

    public LigneHistoriqueDTO() {
    }

    // Constructeur mis à jour
    public LigneHistoriqueDTO(String nomTournoi, int numeroRonde, String adversaires, String score, int resultat) {
        this.nomTournoi = nomTournoi;
        this.numeroRonde = numeroRonde;
        this.adversaires = adversaires;
        this.score = score;
        this.resultat = resultat;
    }

    // Getters et Setters standard...

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getNomTournoi() { return nomTournoi; }
    public void setNomTournoi(String nomTournoi) { this.nomTournoi = nomTournoi; }

    public int getNumeroRonde() { return numeroRonde; }
    public void setNumeroRonde(int numeroRonde) { this.numeroRonde = numeroRonde; }

    public String getAdversaires() { return adversaires; }
    public void setAdversaires(String adversaires) { this.adversaires = adversaires; }

    public String getScore() { return score; }
    public void setScore(String score) { this.score = score; }

    // MODIFICATION : Getter et Setter pour 'resultat'
    public int getResultat() { return resultat; }
    public void setResultat(int resultat) { this.resultat = resultat; }

    @Override
    public String toString() {
        return "LigneHistoriqueDTO{" +
                "nomTournoi='" + nomTournoi + '\'' +
                ", numeroRonde=" + numeroRonde +
                ", adversaires='" + adversaires + '\'' +
                ", score='" + score + '\'' +
                ", resultat=" + resultat +
                '}';
    }
}