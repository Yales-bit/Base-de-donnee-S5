/*
 * Copyright 2000- Francois de Bertrand de Beuvron
 * ... (le reste de la licence) ...
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

    public static void creeSchema(Connection con) throws SQLException {
        try {
            con.setAutoCommit(false);
            try (Statement st = con.createStatement()) {
                // --- 1. TABLES MÈRES (Indépendantes) ---

                // Joueur : OK
                st.executeUpdate("create table Joueur ( "
                        + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                        + " surnom varchar(30) not null unique,"
                        + " taille integer,"
                        + " sexe varchar(15) not null, "
                        + " prenom varchar(30), "
                        + " nom varchar(30), "
                        + " mois integer, "
                        + " jour integer, "
                        + " annee integer "
                        + ") "
                );

                // Tournoi : CORRECTION (Utilisation de la méthode standard pour l'ID)
                // Note: J'utilise 'id' au lieu de 'idtournoi' pour simplifier et rester cohérent avec ClasseMiroir
                st.executeUpdate("CREATE TABLE Tournoi ( "
                        + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                        + "nom VARCHAR(255) NOT NULL UNIQUE, "
                        + "nbrjoueursparequipe INT NOT NULL, "
                        //+ "nbrequipes INT NOT NULL, "
                        + "dureematch INT NOT NULL, "
                        + "nbrequipemax INT, "
                        + "nbrequipemin INT, "
                        + "nbrrondes INT NOT NULL, "
                        + "nbreterrains INT NOT NULL, "
                        + "ouvert BOOLEAN DEFAULT TRUE, "
                        + "fini BOOLEAN DEFAULT FALSE "
                        + ")");

                // Terrain : CORRECTION (Ajout de la clé primaire)
                st.executeUpdate("create table Terrain ( "
                        + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                        + " nom varchar(30) not null unique,"
                        + " est_disponible boolean not null "
                        + ") "
                );

                // --- 2. TABLES INTERMÉDIAIRES ---

                // Ronde : CORRECTION (ID standard et nom de colonne étrangère cohérent)
                st.executeUpdate("create table Ronde ( "
                        + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                        + " numero integer not null,"
                        + " statut varchar(30) not null,"
                        + " idtournoi integer not null,"
                        // Attention : j'ai changé Tournoi(idtournoi) en Tournoi(id) suite à la modif plus haut
                        + " FOREIGN KEY (idtournoi) REFERENCES Tournoi(id)"
                        + ") "
                );

                // Equipe : CORRECTION (Ajout de la clé primaire)
                st.executeUpdate("create table Equipe ( "
                        + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                        + " nom varchar(30) not null,"
                        + " score integer not null, "
                        + " idronde integer not null,"
                        + " FOREIGN KEY (idronde) REFERENCES Ronde(id)"
                        + ") "
                );

                // --- 3. TABLES FILLES (Dépendantes) ---

                // Matchs : (Renommé au pluriel pour éviter le mot-clé réservé)
                st.executeUpdate("create table Matchs ( "
                        + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                        + " idronde integer not null,"
                        + " idequipe1 integer not null,"
                        + " idequipe2 integer not null,"
                        + " idterrain integer not null,"
                        + " statut varchar(30) not null, "
                        + " FOREIGN KEY (idronde) REFERENCES Ronde(id),"
                        + " FOREIGN KEY (idequipe1) REFERENCES Equipe(id),"
                        + " FOREIGN KEY (idequipe2) REFERENCES Equipe(id),"
                        + " FOREIGN KEY (idterrain) REFERENCES Terrain(id)"
                        + ") "
                );

                // Composition : OK
                st.executeUpdate("create table Composition ( "
                        + " idequipe integer not null,"
                        + " idjoueur integer not null,"
                        + " FOREIGN KEY (idequipe) REFERENCES Equipe(id),"
                        + " FOREIGN KEY (idjoueur) REFERENCES Joueur(id),"
                        // Bonne pratique sur une table de liaison : clé primaire composite
                        + " PRIMARY KEY (idequipe, idjoueur)"
                        + ") "
                );
                st.executeUpdate("create table Points ( " //Pour la gestion multi-tournoi
                        + " idjoueur integer not null,"
                        + " idtournoi integer not null,"
                        + " points integer not null DEFAULT 0,"
                        + " FOREIGN KEY (idtournoi) REFERENCES Tournoi(id),"
                        + " FOREIGN KEY (idjoueur) REFERENCES Joueur(id),"
                        // Bonne pratique sur une table de liaison : clé primaire composite
                        + " PRIMARY KEY (idjoueur, idtournoi)"
                        + ") "
                );

                st.executeUpdate("create table Inscription("
                        + " idjoueur integer not null,"
                        + " idtournoi integer not null,"
                        + " FOREIGN KEY (idjoueur) REFERENCES Joueur(id),"
                        + " FOREIGN KEY (idtournoi) REFERENCES Tournoi(id),"
                        // Bonne pratique sur une table de liaison : clé primaire composite
                        + " PRIMARY KEY (idjoueur, idtournoi)"
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

    public static void deleteSchema(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            // Suppression des filles d'abord
            try { st.executeUpdate("drop table Composition"); } catch (SQLException ex) { System.out.println("Info: Table Composition non supprimée"); }
            try { st.executeUpdate("drop table Matchs"); } catch (SQLException ex) { System.out.println("Info: Table Matchs non supprimée"); } // Nom au pluriel 
            try { st.executeUpdate("drop table Points"); } catch (SQLException ex) { System.out.println("Info: Table Points non supprimée"); }
            try { st.executeUpdate("drop table Inscription"); } catch (SQLException ex) { System.out.println("Info: Table Inscription non supprimée"); }
            // Suppression des mères ensuite
            try { st.executeUpdate("drop table Equipe"); } catch (SQLException ex) { System.out.println("Info: Table Equipe non supprimée"); }
            try { st.executeUpdate("drop table Ronde"); } catch (SQLException ex) { System.out.println("Info: Table Ronde non supprimée"); }
            try { st.executeUpdate("drop table Terrain"); } catch (SQLException ex) { System.out.println("Info: Table Terrain non supprimée"); }
            try { st.executeUpdate("drop table Joueur"); } catch (SQLException ex) { System.out.println("Info: Table Joueur non supprimée"); }
            try { st.executeUpdate("drop table Tournoi"); } catch (SQLException ex) { System.out.println("Info: Table Tournoi non supprimée"); }
        }
    }

    public static void razBdd(Connection con) throws SQLException {
        deleteSchema(con);
        creeSchema(con);
    }

    public static void main(String[] args) {
        try (Connection con = ConnectionSimpleSGBD.defaultCon()) {
            razBdd(con);
            System.out.println("Base de données réinitialisée avec succès !");
        } catch (SQLException ex) {
            // Affiche l'erreur complète pour le débogage
            ex.printStackTrace();
            throw new Error(ex);
        }
    }
}