/*
Copyright 2000- Francois de Bertrand de Beuvron

This file is part of CoursBeuvron.

CoursBeuvron is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CoursBeuvron is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CoursBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.insa.toto.model;

/**
 *
 * @author vicbl
 */

import fr.insa.beuvron.utils.database.ClasseMiroir;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.beuvron.utils.database.ConnectionSimpleSGBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.ObjectUtils.Null;

public class Tournoi extends ClasseMiroir {
    private int id;
    private int nbrjoueursparequipe;
    private int nbrequipes;
    private int dureematch;
    private int nbrequipemax;
    private int nbrequipemin;
    private int nbrrondes;
    private String nom;
    private int nbreterrains;
    private boolean ouvert = false;
    private boolean fini = false;

    //constructeur maximal (avec tous les attributs), sans id
    public Tournoi(int nbrjoueursparequipe, int nbrequipes, int dureematch, int nbrequipemax, int nbrequipemin, int nbrrondes, String nom, int nbreterrains, boolean ouvert, boolean fini) {
        super();
        this.nbrjoueursparequipe = nbrjoueursparequipe;
        this.nbrequipes = nbrequipes;
        this.dureematch = dureematch;
        this.nbrequipemax = nbrequipemax;
        this.nbrequipemin = nbrequipemin;
        this.nbrrondes = nbrrondes;
        this.nom = nom;
        this.nbreterrains = nbreterrains;
        this.ouvert = ouvert;
        this.fini = fini;

    }
// SECOND CONSTRUCTEURS
    // Constructeur maximal avec id
    public Tournoi(int id, int nbrjoueursparequipe, int nbrequipes, int dureematch, int nbrequipemax, int nbrequipemin, int nbrrondes, String nom, int nbreterrains, boolean ouvert, boolean fini) {
        super(id);
        this.nbrjoueursparequipe = nbrjoueursparequipe;
        this.nbrequipes = nbrequipes;
        this.dureematch = dureematch;
        this.nbrequipemax = nbrequipemax;
        this.nbrequipemin = nbrequipemin;
        this.nbrrondes = nbrrondes;
        this.nom = nom;
        this.nbreterrains = nbreterrains;
        this.ouvert = ouvert;
        this.fini = fini;
    }
    
    //constructeur secondaire (sans spécification du nombre max/min d'équipes), sans id
    public Tournoi(int nbrjoueursparequipe, int nbrequipes, int dureematch, int nbrrondes, String nom, int nbreterrains, boolean ouvert, boolean fini) {
        super();
        this.nbrjoueursparequipe = nbrjoueursparequipe;
        this.nbrequipes = nbrequipes;
        this.dureematch = dureematch;
        this.nbrrondes = nbrrondes;
        this.nom = nom;
        this.nbreterrains = nbreterrains;
        this.ouvert = ouvert;
        this.fini = fini;
        this.nbrequipemin = 2; 
        this.nbrequipemax = 0; // Convention : 0 signifie "illimité"
    }

    // Constructeur secondaire avec id
    public Tournoi(int id, int nbrjoueursparequipe, int nbrequipes, int dureematch, int nbrrondes, String nom, int nbreterrains, boolean ouvert, boolean fini) {
        super(id);
        this.nbrjoueursparequipe = nbrjoueursparequipe;
        this.nbrequipes = nbrequipes;
        this.dureematch = dureematch;
        this.nbrrondes = nbrrondes;
        this.nom = nom;
        this.nbreterrains = nbreterrains;
        this.ouvert = ouvert;
        this.fini = fini;
        this.nbrequipemin = 2; 
        this.nbrequipemax = 0;
    }

//SECTION METHODES
    public static void creerTournoi (Tournoi T) throws Exception{
        if (T.getNom() == null || T.getNom().isEmpty()){ 
            throw new Exception("Le nom du tournoi est obligatoire");
        }
        if (T.getNbEquipes()<2){
            throw new Exception("Le tournoi doit avoir au moins 2 equipes");
        }
        if (T.getDureeMatch()<=0){
            throw new Exception("La duree du match doit etre positive");
        }
        if (T.getNbrJoueursParEquipe() <= 0) {
            throw new Exception("Il doit y avoir au moins 1 joueur par équipe");
        }
        if (T.getNbrRondes() <= 0) {
            throw new Exception("Le tournoi doit comporter au moins une ronde");
        }
        if (T.getNbrTerrains() <= 0) {
            throw new Exception("Il faut définir au moins un terrain disponible");
        }
        if (T.getNbrEquipeMin() < 2) {
             throw new Exception("Le seuil minimum d'équipes ne peut pas être inférieur à 2");
        }
        if (T.getNbrEquipeMax() > 0 && T.getNbrEquipeMax() < T.getNbrEquipeMin()) {
             throw new Exception("Le nombre maximum d'équipes ne peut pas être inférieur au minimum requis");
        }

        // --- SAUVEGARDE EN BASE DE DONNÉES ---
        try (Connection con = ConnectionPool.getConnection()) {
            // On appelle la méthode saveInDB de l'objet (héritée de ClasseMiroir)
            T.saveInDB(con);
        } catch (SQLException ex) {
            throw new Exception("Erreur technique lors de l'enregistrement du tournoi en base de données", ex);
        }
    }





@Override
protected Statement saveSansId(Connection con) throws SQLException {
    PreparedStatement pst = con.prepareStatement("insert into tournoi (nbrjoueursparequipe, nbrequipes, dureematch, nbrequipemax, nbrequipemin, nbrrondes, nom, nbreterrains, ouvert, fini) \n"
            + "values(?,?,?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
    pst.setInt(1, this.nbrjoueursparequipe);
    pst.setInt(2, this.nbrequipes);
    pst.setInt(3, this.dureematch);
    pst.setInt(4, this.nbrequipemax);
    pst.setInt(5, this.nbrequipemin);
    pst.setInt(6, this.nbrrondes);
    pst.setString(7, this.nom);
    pst.setInt(8, this.nbreterrains);
    pst.setBoolean(9, this.ouvert);
    pst.setBoolean(10, this.fini);
    pst.executeUpdate();
    return pst;
}


    public int getNbrJoueursParEquipe() {
        return nbrjoueursparequipe;
    }

    public void setNbrJoueursParEquipe(int nbrjoueurparequipe) {
        this.nbrjoueursparequipe = nbrjoueurparequipe;
    }

    public int getNbEquipes() {
        return nbrequipes;
    }

    public void setNbEquipes(int nbrequipes) {
        this.nbrequipes = nbrequipes;
    }

    public int getDureeMatch() {
        return dureematch;
    }

    public void setDureeMatch(int dureematch) {
        this.dureematch = dureematch;
    }

    public int getNbrEquipeMax() {
        return nbrequipemax;
    }

    public void setNbrEquipeMax(int nbrequipemax) {
        this.nbrequipemax = nbrequipemax;
    }

    public int getNbrEquipeMin() {
        return nbrequipemin;
    }

    public void setNbrEquipeMin(int nbrequipemin) {
        this.nbrequipemin = nbrequipemin;
    }

    public int getNbrRondes() {
        return nbrrondes;
    }

    public void setNbrRondes(int nbrrondes) {
        this.nbrrondes = nbrrondes;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getNbrTerrains() {
        return nbreterrains;
    }

    public void setNbrTerrains(int nbreterrains) {
        this.nbreterrains = nbreterrains;
    }

    public boolean isOuvert() {
        return ouvert;
    }

    public void setOuvert(boolean ouvert) {
        this.ouvert = ouvert;
    }

    public boolean isFini() {
        return fini;
    }

    public void setFini(boolean fini) {
        this.fini = fini;
    }
    
    public int getId() {
        return id;
    }

@Override
    public String toString() {
        return "Tournoi{" +
                "nom='" + nom + '\'' +
                ", nbrjoueursparequipe=" + nbrjoueursparequipe +
                ", nbrequipes=" + nbrequipes +
                ", dureematch=" + dureematch +
                ", nbrequipemax=" + nbrequipemax +
                ", nbrequipemin=" + nbrequipemin +
                ", nbrrondes=" + nbrrondes +
                ", nbreterrains=" + nbreterrains +
                ", ouvert=" + ouvert +
                ", fini=" + fini +
                '}';
    }
}
