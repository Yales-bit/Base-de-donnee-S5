package fr.insa.toto.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.insa.beuvron.utils.database.ClasseMiroir;

public class Ronde extends ClasseMiroir {

    private int numero; // Numéro de la ronde dans le tournoi (ex: 1, 2, 3)
    private StatutRonde statut; // Statut de la ronde (voir la classe StatutRonde)
    private int idtournoi; // La ronde appartient à un tournoi 

    public Ronde(int numero, int idtournoi, StatutRonde statut) {
        super();
        this.numero = numero;
        this.idtournoi = idtournoi;
        this.statut = StatutRonde.EN_ATTENTE;
    }

    public Ronde(int id, int numero, StatutRonde statut, int idtournoi) {
        super(id);
        this.numero = numero;
        this.statut = statut;
        this.idtournoi = idtournoi;
    }
    @Override
    protected Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement pst = con.prepareStatement("INSERT INTO Rondes (numero, statut, idtournoi) VALUES (?, ?, ?)");
        pst.setInt(1, this.numero);
        pst.setString(2, this.statut.toString());
        pst.setInt(3, this.idtournoi);
        pst.executeUpdate();
        return pst;
    }

    // Getters et Setters

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public StatutRonde getStatut() {
        return statut;
    }

    public void setStatut(StatutRonde statut) {
        this.statut = statut;
    }

    public int getIdtournoi() {
        return idtournoi;
    }

    public void setIdtournoi(int idtournoi) {
        this.idtournoi = idtournoi;
    }

 
    @Override
    public String toString() {
        return "Ronde{" +
               ", numero=" + numero +
               ", statut=" + statut +
               ", idtournoi=" + idtournoi +
               '}';
    }
}