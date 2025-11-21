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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import fr.insa.beuvron.utils.database.ClasseMiroir;
import fr.insa.beuvron.utils.database.ConnectionSimpleSGBD;


public class Joueur extends ClasseMiroir {

    private String surnom;
    private StatutSexe sexe;
    private int taille;

    //Constructeur utilisé quand on ne connait pas encore l'id du joueur (il vient d'être créé)
    public Joueur(String surnom, StatutSexe sexe, int taille) {
        super();
        this.surnom = surnom;
        this.sexe = sexe;
        this.taille = taille;
    }

    //Constructeur utilisé quand on connait l'id du joueur (il vient d'avoir son id attribué)
    public Joueur(int id, String surnom, StatutSexe sexe, int taille) {
        super(id);
        this.surnom = surnom;
        this.sexe = sexe;
        this.taille = taille;
    }
    @Override
    protected Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement pst = con.prepareStatement("insert into joueur (surnom, categorie, taille) \n"
                + "values(?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
        pst.setString(1, this.surnom);
        pst.setString(2, this.sexe.toString());
        pst.setInt(3, this.taille);
        pst.executeUpdate();
        return pst;

    }

    /*public static void main(String[] args) {
        try {
            Joueur j1 = new Joueur("testCre2", "J", 152);
            int id = j1.saveInDB(ConnectionSimpleSGBD.defaultCon());
        } catch (SQLException ex) {
            throw new Error(ex);
        }
    }*/

    /**
     * @return the surnom
     */
    public String getSurnom() {
        return surnom;
    }

    /**
     * @param surnom the surnom to set
     */
    public void setSurnom(String surnom) {
        this.surnom = surnom;
    }

    /**
     * @return the categorie
     */
    public StatutSexe getSexe() {
        return sexe;
    }
    
    public void setSexe(StatutSexe sexe) {
        this.sexe = sexe;
    }

    /**
     * @return the taille
     */
    public int getTaille() {
        return taille;
    }

    /**
     * @param taille the taille to set
     */
    public void setTaille(int taille) {
        this.taille = taille;
    }
  
 //test commit Hack   
}
