package fr.insa.toto.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import fr.insa.beuvron.utils.database.ClasseMiroir;

public class Equipe extends ClasseMiroir {
    private int idequipe;
    private String nom;
    private int score;
    private int idtournoi;
    

    // //Constructeur utilisé quand on ne connait pas encore l'id de l'équipe (elle vient d'être créée)
    public Equipe(String nom, int score, int idtournoi) {
        super(); // associe -1 comme id
        this.nom = nom;
        this.score = score;
        this.idtournoi = idtournoi;
    }

    // Constructeur utilisé quand on connait l'id de l'équipe
    public Equipe(int idequipe, String nom, int score, int idtournoi) {
        super(idequipe);
        this.nom = nom;
        this.score = score;
        this.idtournoi = idtournoi;
    }

    @Override
    protected Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement pst = con.prepareStatement("insert into Equipes (surnom, categorie, taille) \n"
                + "values(?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
        pst.setString(1, this.nom);
        pst.setInt(2, this.score);
        pst.setInt(3, this.idtournoi);
        pst.executeUpdate();
        return pst;

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
    
    public int getIdTournoi() {
        return idtournoi;
    }

    public void setIdTournoi(int idtournoi) {
        this.idtournoi = idtournoi;
    }
}   
