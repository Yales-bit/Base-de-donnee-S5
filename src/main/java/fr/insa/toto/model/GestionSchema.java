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

import fr.insa.beuvron.utils.database.ConnectionSimpleSGBD;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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
                st.executeUpdate("create table Joueurs ( "
                        + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                        + " surnom varchar(30) not null unique,"
                        + " taille integer,"                   
                        + " sexe varchar(15) not null "
                        + ") "
                );
                st.executeUpdate("CREATE TABLE Tournois ( "
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

                st.executeUpdate("create table Terrains ( "            
                        + "id integer,"
                        + " description text"
                        + ") "
                );
                st.executeUpdate("create table Matchs ( "
                        + " id integer not null,"
                        + " idloisir integer not null,"
                        + " statut integer not null "
                        + ") "
                );
                st.executeUpdate("create table Rondes ( "
                        + "id integer not null unique,"
                        + " numero integer not null,"
                        + " statut varchar(30) not null,"
                        + " idtournoi integer not null,"
                        + " FOREIGN KEY (idtournoi) REFERENCES Tournois(idtournoi)" // Ajout de la contrainte de clé étrangère
                        + ") "
                );

                st.executeUpdate("create table Equipes ( "
                        + " id integer not null,"
                        + " nom varchar(30) not null,"
                        + " score integer not null, "
                        + " idmatch integer not null "
                        + ") "
                );
                st.executeUpdate("create table Composition ( "
                        + " idequipe integer not null,"
                        + " idjoueur integer not null"                     
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
                st.executeUpdate("drop table Joueurs");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table Terrains");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table Matchs");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table Equipes");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table Composition");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table Tournois");
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
