package fr.insa.toto.model;

public class Equipe {
    private String nom;
    private int score;

    public Equipe(String nom, int score) {
        this.nom = nom;
        this.score = score;
    }

    // Getters and Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}