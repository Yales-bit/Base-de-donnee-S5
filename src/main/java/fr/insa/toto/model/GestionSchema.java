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
 * Classe responsable de la création et de la suppression du schéma de base de données.
 * @author francois
 */
public class GestionSchema {

    /**
     * Crée toutes les tables de la base de données dans le bon ordre.
     * @param con La connexion active à la base de données.
     * @throws SQLException En cas d'erreur SQL.
     */
    public static void creeSchema(Connection con) throws SQLException {
        // On désactive l'auto-commit pour gérer la transaction manuellement
        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {
            // --- 1. TABLES MÈRES (Sans clés étrangères vers d'autres tables de ce schéma) ---

            // Table Joueur
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
            
            // Table Utilisateur (pour l'administration/connexion)
            st.executeUpdate("create table Utilisateur ( "
                    + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                    + " identifiant varchar(30) not null unique,"
                    + " mdp varchar(20) not null,"
                    + " role integer not null "
                    + ") "
            );

            // Table Tournoi
            st.executeUpdate("CREATE TABLE Tournoi ( "
                    + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                    + "nom VARCHAR(255) NOT NULL UNIQUE, "
                    + "nbrjoueursparequipe INT NOT NULL, "
                    + "dureematch INT NOT NULL, "
                    + "nbrequipemax INT, "
                    + "nbrequipemin INT, "
                    + "nbrrondes INT NOT NULL, "
                    + "nbreterrains INT NOT NULL, "
                    + "ouvert BOOLEAN DEFAULT TRUE, "
                    + "fini BOOLEAN DEFAULT FALSE "
                    + ")");

            // Table Terrain
            st.executeUpdate("create table Terrain ( "
                    + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                    + " nom varchar(30) not null unique,"
                    + " est_disponible boolean not null "
                    + ") "
            );

            // --- 2. TABLES INTERMÉDIAIRES (Dépendent des tables mères) ---

            // Table Ronde (Dépend de Tournoi)
            st.executeUpdate("create table Ronde ( "
                    + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                    + " numero integer not null,"
                    + " statut varchar(30) not null,"
                    + " idtournoi integer not null,"
                    + " FOREIGN KEY (idtournoi) REFERENCES Tournoi(id)"
                    + ") "
            );

            // Table Equipe (Dépend de Ronde)
            st.executeUpdate("create table Equipe ( "
                    + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                    + " nom varchar(30) not null,"
                    + " score integer not null, "
                    + " idronde integer not null,"
                    + " FOREIGN KEY (idronde) REFERENCES Ronde(id)"
                    + ") "
            );

            // --- 3. TABLES FILLES & DE LIAISON (Dépendent de plusieurs tables) ---

            // Table Matchs (Dépend de Ronde et Equipe)
            st.executeUpdate("create table Matchs ( "
                    + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                    + " idronde integer not null,"
                    + " idequipe1 integer not null,"
                    + " idequipe2 integer not null,"
                    //+ " idterrain integer not null," // Terrain désactivé pour l'instant
                    + " statut varchar(30) not null, "
                    + " FOREIGN KEY (idronde) REFERENCES Ronde(id),"
                    + " FOREIGN KEY (idequipe1) REFERENCES Equipe(id),"
                    + " FOREIGN KEY (idequipe2) REFERENCES Equipe(id)"
                    //+ " FOREIGN KEY (idterrain) REFERENCES Terrain(id)"
                    + ") "
            );

            // Table de liaison Composition (Equipe <-> Joueur)
            st.executeUpdate("create table Composition ( "
                    + " idequipe integer not null,"
                    + " idjoueur integer not null,"
                    + " FOREIGN KEY (idequipe) REFERENCES Equipe(id),"
                    + " FOREIGN KEY (idjoueur) REFERENCES Joueur(id),"
                    + " PRIMARY KEY (idequipe, idjoueur)"
                    + ") "
            );
            
            // Table de liaison Points (Joueur <-> Tournoi pour le classement)
            st.executeUpdate("create table Points ( "
                    + " idjoueur integer not null,"
                    + " idtournoi integer not null,"
                    + " points integer not null DEFAULT 0,"
                    + " FOREIGN KEY (idtournoi) REFERENCES Tournoi(id),"
                    + " FOREIGN KEY (idjoueur) REFERENCES Joueur(id),"
                    + " PRIMARY KEY (idjoueur, idtournoi)"
                    + ") "
            );

            // Table de liaison Inscription (Joueur <-> Tournoi)
            st.executeUpdate("create table Inscription("
                    + " idjoueur integer not null,"
                    + " idtournoi integer not null,"
                    + " FOREIGN KEY (idjoueur) REFERENCES Joueur(id),"
                    + " FOREIGN KEY (idtournoi) REFERENCES Tournoi(id),"
                    + " PRIMARY KEY (idjoueur, idtournoi)"
                    + ") "
            );
            
            // Table de liaison ParticipationRonde (Joueur <-> Ronde pour la priorité)
            st.executeUpdate("create table ParticipationRonde ( "
                    + " idjoueur integer not null,"
                    + " idronde integer not null,"
                    + " FOREIGN KEY (idjoueur) REFERENCES Joueur(id),"
                    + " FOREIGN KEY (idronde) REFERENCES Ronde(id),"
                    + " PRIMARY KEY (idjoueur, idronde)"
                    + ") "
            );

            // Si tout s'est bien passé, on valide la transaction
            con.commit();
        } catch (SQLException ex) {
            // En cas d'erreur, on annule tout ce qui a été fait dans ce bloc
            con.rollback();
            throw ex;
        } finally {
            // On rétablit le mode auto-commit par défaut
            con.setAutoCommit(true);
        }
    }

    /**
     * Supprime toutes les tables du schéma dans l'ordre inverse de leur création.
     * Utilise "IF EXISTS" pour éviter les erreurs si une table manque.
     * @param con La connexion active.
     * @throws SQLException En cas d'erreur SQL critique.
     */
    public static void deleteSchema(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            // 1. Suppression des tables de liaison et tables filles (qui ont des clés étrangères)
            st.executeUpdate("drop table if exists ParticipationRonde");
            st.executeUpdate("drop table if exists Inscription");
            st.executeUpdate("drop table if exists Points");
            st.executeUpdate("drop table if exists Composition");
            st.executeUpdate("drop table if exists Matchs");
            
            // 2. Suppression des tables intermédiaires
            st.executeUpdate("drop table if exists Equipe");
            st.executeUpdate("drop table if exists Ronde");
            
            // 3. Suppression des tables mères
            st.executeUpdate("drop table if exists Terrain");
            st.executeUpdate("drop table if exists Tournoi");
            st.executeUpdate("drop table if exists Utilisateur");
            st.executeUpdate("drop table if exists Joueur");
        }
    }

    /**
     * Réinitialise complètement la base de données (Suppression puis Création).
     */
    public static void razBdd(Connection con) throws SQLException {
        deleteSchema(con);
        creeSchema(con);
    }

    /**
     * Méthode principale pour tester la réinitialisation du schéma.
     */
    public static void main(String[] args) {
        try (Connection con = ConnectionSimpleSGBD.defaultCon()) {
            razBdd(con);
            System.out.println("=== Base de données réinitialisée avec succès ! ===");
        } catch (SQLException ex) {
            System.err.println("=== Erreur lors de la réinitialisation de la base de données ===");
            ex.printStackTrace();
            throw new Error(ex);
        }
    }
}