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
import fr.insa.beuvron.utils.database.ConnectionSimpleSGBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tournoi extends ClasseMiroir {
    
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

    //constructeur maximal (avec tous les attributs)
    public Tournoi(int nbrjoueursparequipe, int nbrequipes, int dureematch, int nbrequipemax, int nbrequipemin, int nbrrondes, String nom, int nbreterrains, boolean ouvert, boolean fini) {
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
    
    //constructeur secondaire (sans spécification du nombre max/min d'équipes)
    public Tournoi(int nbrjoueursparequipe, int nbrequipes, int dureematch, int nbrrondes, String nom, int nbreterrains, boolean ouvert, boolean fini) {
        this.nbrjoueursparequipe = nbrjoueursparequipe;
        this.nbrequipes = nbrequipes;
        this.dureematch = dureematch;
        this.nbrrondes = nbrrondes;
        this.nom = nom;
        this.nbreterrains = nbreterrains;
        this.ouvert = ouvert;
        this.fini = fini;
    }

  @Override
    protected Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement pst = con.prepareStatement("insert into joueur (surnom, categorie, taille) \n"
                + "values(?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
        pst.setInt(1, this.nbrjoueursparequipe);
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
