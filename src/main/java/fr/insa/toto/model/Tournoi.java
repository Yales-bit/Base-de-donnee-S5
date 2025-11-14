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
    
    private int nbrjoueurparequipe;
    private int nbrequipes;

    public Tournoi(int nbrjoueurparequipe ) {
        this.nbrjoueurparequipe = nbrjoueurparequipe;
        this.nbrequipes = nbrequipes;
    }


  @Override
    protected Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement pst = con.prepareStatement("insert into joueur (surnom, categorie, taille) \n"
                + "values(?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
        pst.setInt(1, this.nbrjoueurparequipe);
        pst.executeUpdate();
        return pst;

    }

    
    public int getNbrJoueursParEquipe() {
        return nbrjoueurparequipe;
    }

    public void setNbrJoueursParEquipe(int nbrjoueurparequipe) {
        this.nbrjoueurparequipe = nbrjoueurparequipe;
    }

    public int getNbEquipes() {
        return nbrequipes;
    }

    public void setNbEquipes(int nbrequipes) {
        this.nbrequipes = nbrequipes;
    }

    
}
