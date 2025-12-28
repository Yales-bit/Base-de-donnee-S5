package fr.insa.toto.model.dto;
import fr.insa.toto.model.Joueur;

/**
 * DTO (Data Transfer Object) simple pour représenter une ligne dans la grille de classement.
 * N'est pas une entité de base de données.
 */
public class LigneClassement {
    private int rang;
    private Joueur joueur;
    private int totalPoints;

    public LigneClassement(int rang, Joueur joueur, int totalPoints) {
        this.rang = rang;
        this.joueur = joueur;
        this.totalPoints = totalPoints;
    }

    public int getRang() { return rang; }
    public Joueur getJoueur() { return joueur; }
    public int getTotalPoints() { return totalPoints; }
    
    // Helper pour l'affichage du nom
    public String getNomAffichage() {
        if (joueur == null) return "Inconnu";
        return joueur.getPrenom() + " " + joueur.getNom().toUpperCase() + 
               (joueur.getSurnom() != null && !joueur.getSurnom().isEmpty() ? " (" + joueur.getSurnom() + ")" : "");
    }
}