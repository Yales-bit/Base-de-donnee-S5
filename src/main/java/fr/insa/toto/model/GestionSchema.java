/*
Copyright 2000- Francois de Bertrand de Beuvron

This file is ecole of CoursBeuvron.

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
import java.sql.SQLException;
import java.sql.Statement;

import fr.insa.beuvron.utils.database.ConnectionSimpleSGBD;

/**
 *
 * @author francois
 */
public class GestionSchema {

    /**
     *
     * @param con
     * @throws SQLException
     */
    public static void creeSchema(Connection con)
            throws SQLException {
        try {
            con.setAutoCommit(false);
            try (Statement st = con.createStatement()) {
                // creation des tables
                st.executeUpdate("create table joueur ( "
                        + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                        + " surnom varchar(30) not null unique,"
                        + " taille integer,"                   
                        + " sexe varchar(15) not null "
                        + ") "
                );
                st.executeUpdate("CREATE TABLE tournoi ( "
                        + "idtournoi INTEGER NOT NULL UNIQUE, "
                        + "nom VARCHAR(255) NOT NULL, "
                        + "nbrjoueursparequipe INT NOT NULL, "
                        + "nbrequipes INT NOT NULL, "
                        + "dureematch INT NOT NULL, "
                        + "nbrequipemax INT, "
                        + "nbrequipemin INT, "
                        + "nbrrondes INT NOT NULL, "
                        + "nbreterrains INT NOT NULL, "
                        + "ouvert BOOLEAN DEFAULT FALSE, "
                        + "fini BOOLEAN DEFAULT FALSE "
                        + ")");

                st.executeUpdate("create table Terrain ( "            
                        + "id integer,"
                        + " description text"
                        + ") "
                );
                st.executeUpdate("create table Match ( "
                        + " id integer not null,"
                        + " idterrain integer not null,"
                        + " statut varchar(30) not null, "
                        + " FOREIGN KEY (id_ronde) REFERENCES Ronde(id),"
                        + " FOREIGN KEY (id_equipe1) REFERENCES Equipe(id),"
                        + " FOREIGN KEY (id_equipe2) REFERENCES Equipe(id)"
                        + " FOREIGN KEY (idterrain) REFERENCES Terrain(id)"
                        + ") "
                );
                st.executeUpdate("create table Ronde ( "
                        + " id integer not null unique,"
                        + " numero integer not null,"
                        + " statut varchar(30) not null,"
                        + " idtournoi integer not null,"
                        + " FOREIGN KEY (idtournoi) REFERENCES Tournoi(idtournoi)" // Ajout de la contrainte de clé étrangère
                        + ") "
                );

                st.executeUpdate("create table Equipe ( "
                        + " id integer not null,"
                        + " nom varchar(30) not null,"
                        + " score integer not null "
                        + ") "
                );
                st.executeUpdate("create table Composition ( "
                        + " idequipe integer not null,"
                        + " idjoueur integer not null"   
                        + " FOREIGN KEY (idequipe) REFERENCES Equipe(id),"
                        + " FOREIGN KEY (idjoueur) REFERENCES Joueur(id)"                  
                        + ") "
                );

                
                con.commit();
            }
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(true);
        }
    }

    /**
     *
     * @param con
     * @throws SQLException
     */
    public static void deleteSchema(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            try {
                st.executeUpdate("drop table Joueur");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table Terrain");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table Match");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table Equipe");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table Composition");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table Tournoi");
            } catch (SQLException ex) {
            }
        }
    }

    /**
     *
     * @param con
     * @throws SQLException
     */
    public static void razBdd(Connection con) throws SQLException {
        deleteSchema(con);
        creeSchema(con);
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        try (Connection con = ConnectionSimpleSGBD.defaultCon()) {
            razBdd(con);
        } catch (SQLException ex) {
            throw new Error(ex);
        }
    }

}
