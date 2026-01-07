package fr.insa.toto.model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import fr.insa.beuvron.utils.database.ClasseMiroir;





public class Terrain extends ClasseMiroir {

    private String nom; 
    private boolean estDisponible; 

    public Terrain() {
        super();
        this.estDisponible = true; // Par défaut, un terrain est disponible
    }

    // Constructeur sans ID (nouveau terrain)
    public Terrain(String nom) {
        super();
        this.nom = nom;
        this.estDisponible = true;
    }

    // Constructeur avec ID (depuis la BDD)
    public Terrain(int id, String nom, boolean estDisponible) {
        super(id);
        this.nom = nom;
        this.estDisponible = estDisponible;
    }

    @Override
    protected Statement saveSansId(Connection con) throws SQLException {
        // Attention : table "terrain" au singulier
        PreparedStatement pst = con.prepareStatement(
            "INSERT INTO terrain (nom, est_disponible) VALUES (?, ?)", 
            Statement.RETURN_GENERATED_KEYS
        );
        pst.setString(1, this.nom);
        pst.setBoolean(2, this.estDisponible);
        pst.executeUpdate();
        return pst;
    }


    // Getters et Setters

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
               ", nom='" + nom + '\'' +
               ", estDisponible=" + estDisponible +
               '}';
    }
}