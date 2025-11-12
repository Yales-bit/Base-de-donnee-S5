package fr.insa.toto.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import fr.insa.beuvron.utils.database.ClasseMiroir;

public class Equipe extends ClasseMiroir {
    private String nom;
    private int score;

@Override
    protected Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement pst = con.prepareStatement("insert into joueur (surnom, categorie, taille) \n"
                + "values(?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
        pst.setString(1, this.nom);
        pst.setInt(2, this.score);
        pst.executeUpdate();
        return pst;

    }


    public Equipe(String nom, int score) {
        super(); // Call the superclass constructor
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