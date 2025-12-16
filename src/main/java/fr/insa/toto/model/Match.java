package fr.insa.toto.model;

import fr.insa.beuvron.utils.database.ClasseMiroir;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Match extends ClasseMiroir {

    private Ronde ronde;
    private Equipe equipe1;
    private Equipe equipe2;
    private Terrain terrain;
    private StatutMatch statut;

    public Match(Ronde ronde, Equipe equipe1, Equipe equipe2, Terrain terrain) {
        this.ronde = ronde;
        this.equipe1 = equipe1;
        this.equipe2 = equipe2;
        this.terrain = terrain;
        this.statut = StatutMatch.EN_ATTENTE; // Par défaut, un nouveau match est "en cours"
    }
    public Match(Ronde ronde, Equipe equipe1, Equipe equipe2) {
        this.ronde = ronde;
        this.equipe1 = equipe1;
        this.equipe2 = equipe2;
        this.statut = StatutMatch.EN_ATTENTE; // Par défaut, un nouveau match est "en cours"
    }
    @Override
protected Statement saveSansId(Connection con) throws SQLException {
    PreparedStatement pst = con.prepareStatement("INSERT INTO Match (idronde, idequipe1, idequipe2, idterrain, statut) VALUES (?, ?, ?, ?, ?)");
    pst.setInt(1, this.ronde.getId());
    pst.setInt(2, this.equipe1.getId());
    pst.setInt(3, this.equipe2.getId());
    pst.setInt(4, this.terrain.getId());
    pst.setString(5, this.statut.toString());
    pst.executeUpdate();
    return pst;
}
    // Getters et Setters

    public Ronde getRonde() { return ronde; }
    public void setRonde(Ronde ronde) { this.ronde = ronde; }

    public Equipe getEquipe1() { return equipe1; }
    public void setEquipe1(Equipe equipe1) { this.equipe1 = equipe1; }

    public Equipe getEquipe2() { return equipe2; }
    public void setEquipe2(Equipe equipe2) { this.equipe2 = equipe2; }

    public StatutMatch getStatut() { return statut; }
    public void setStatut(StatutMatch statut) { this.statut = statut; }

    public Terrain getTerrain() { return terrain; }
    public void setTerrain(Terrain terrain) { this.terrain = terrain; }

    // Méthodes utilitaires
    public boolean estTermine() {
        return this.statut == StatutMatch.TERMINE;
    }
}
