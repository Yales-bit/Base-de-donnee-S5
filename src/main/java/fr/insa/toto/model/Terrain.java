package fr.insa.toto.model;

public class Terrain {

    private Long id;
    private String nom; 
    private boolean estDisponible; 

    public Terrain() {
        this.estDisponible = true; // Par défaut, un terrain est disponible
    }

    public Terrain(String nom) {
        this.nom = nom;
        this.estDisponible = true;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public boolean isEstDisponible() {
        return estDisponible;
    }

    public void setEstDisponible(boolean estDisponible) {
        this.estDisponible = estDisponible;
    }

    @Override
    public String toString() {
        return "Terrain{" +
               "id=" + id +
               ", nom='" + nom + '\'' +
               ", estDisponible=" + estDisponible +
               '}';
    }
}