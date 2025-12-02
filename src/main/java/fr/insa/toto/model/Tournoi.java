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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.ObjectUtils.Null;

public class Tournoi extends ClasseMiroir {
    //private int id;
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
        this.nbrequipemax = -1; // Convention : 0 signifie "illimité"
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
        this.nbrequipemax = -1;
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
            String sqlErrorMessage = ex.getMessage();
            if (sqlErrorMessage != null && (sqlErrorMessage.contains("Duplicate entry") || sqlErrorMessage.contains("Violation d'index unique")) && sqlErrorMessage.toLowerCase().contains("nom")) { 
                throw new TournoiNomExisteDejaException("Un tournoi du même nom existe déjà, peut-être souhaitez-vous le modifier ?");
            }
            else {
                throw new Exception("Erreur technique lors de l'enregistrement du tournoi en base de données", ex);
            }
        }
    }

    public static List<Tournoi> getAllTournois() throws SQLException {
        List<Tournoi> list = new ArrayList<>();
        try (Connection con = ConnectionPool.getConnection()) {
            // Attention : on utilise le nom de table "tournoi" (singulier) défini précédemment
            PreparedStatement pst = con.prepareStatement("SELECT * FROM Tournoi");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                // On récupère les colonnes
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                int nbJoueurs = rs.getInt("nbrjoueursparequipe");
                int nbEquipes = rs.getInt("nbrequipes");
                int duree = rs.getInt("dureematch");
                int nbRondes = rs.getInt("nbrrondes");
                int nbTerrains = rs.getInt("nbreterrains");
                int min = rs.getInt("nbrequipemax");
                int max = rs.getInt("nbrequipemin");
                boolean ouvert = rs.getBoolean("ouvert");
                boolean fini = rs.getBoolean("fini");

                // On reconstruit l'objet
                list.add(new Tournoi(id, nbJoueurs, nbEquipes, duree, max, min, nbRondes, nom, nbTerrains, ouvert, fini));
            }
        }
        return list;
    }


    // Méthode pour récupérer un tournoi spécifique par son ID
    public static Tournoi getTournoiById(int id) throws SQLException {
        try (Connection con = ConnectionPool.getConnection()) {
            // ATTENTION : Nom de table avec Majuscule "Tournoi"
            PreparedStatement pst = con.prepareStatement("SELECT * FROM Tournoi WHERE id = ?");
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String nom = rs.getString("nom");
                int nbJoueurs = rs.getInt("nbrjoueursparequipe");
                int nbEquipes = rs.getInt("nbrequipes");
                int duree = rs.getInt("dureematch");
                int nbRondes = rs.getInt("nbrrondes");
                int nbTerrains = rs.getInt("nbreterrains");
                int min = rs.getInt("nbrequipemax");
                int max = rs.getInt("nbrequipemin");
                boolean ouvert = rs.getBoolean("ouvert");
                boolean fini = rs.getBoolean("fini");

                return new Tournoi(id, nbJoueurs, nbEquipes, duree, max, min, nbRondes, nom, nbTerrains, ouvert, fini);
            }
        }
        return null; // Pas trouvé
    }


@Override
protected Statement saveSansId(Connection con) throws SQLException {
    PreparedStatement pst = con.prepareStatement("insert into Tournoi (nbrjoueursparequipe, nbrequipes, dureematch, nbrequipemax, nbrequipemin, nbrrondes, nom, nbreterrains, ouvert, fini) \n"
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


// Inscrire une liste de joueurs à ce tournoi
    public void inscrireJoueurs(List<Joueur> joueurs) throws SQLException {
        try (Connection con = ConnectionPool.getConnection()) {
            String sql = "INSERT INTO Inscription (id_tournoi, id_joueur) VALUES (?, ?)";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                for (Joueur j : joueurs) {
                    pst.setInt(1, this.getId()); // ID du tournoi actuel
                    pst.setInt(2, j.getId());    // ID du joueur
                    pst.addBatch(); // On prépare l'ajout en lot
                }
                pst.executeBatch(); // On exécute tout d'un coup
            }
        }
    }

    // Récupérer les joueurs inscrits à ce tournoi
    public List<Joueur> getJoueursInscrits() throws SQLException {
        List<Joueur> list = new ArrayList<>();
        String sql = "SELECT j.* FROM Joueur j JOIN Inscription i ON j.id = i.id_joueur WHERE i.id_tournoi = ?";
        
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, this.getId());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                // Mapping simple (à adapter si tu as une méthode de mapping dédiée)
                int id = rs.getInt("id");
                String surnom = rs.getString("surnom");
                String sexeStr = rs.getString("sexe");
                int taille = rs.getInt("taille");
                String prenom = rs.getString("prenom");
                String nom = rs.getString("nom");
                int mois = rs.getInt("mois");
                int jour = rs.getInt("jour");
                int annee = rs.getInt("annee");
                StatutSexe sexe = null;
                try { if(sexeStr != null) sexe = StatutSexe.valueOf(sexeStr); } catch (Exception e) {}
                
                list.add(new Joueur(id, surnom, sexe, taille, prenom, nom, mois, jour, annee));
            }
        }
        return list;
    }    

/**
 * Compte le nombre de joueurs actuellement inscrits à ce tournoi (via la table Inscription).
 * @return Le nombre de joueurs.
 * @throws SQLException 
 */
public int compterJoueursInscrits() throws SQLException {
    // Attention : Nom de table avec Majuscule 'Inscription'
    String sql = "SELECT COUNT(id_joueur) FROM Inscription WHERE id_tournoi = ?";
    
    // Vérification de base pour s'assurer que l'objet est bien en BDD
    if (this.getId() == -1) {
        throw new IllegalStateException("Ce tournoi n'a pas encore été sauvegardé.");
    }
    
    try (Connection con = ConnectionPool.getConnection()) {
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, this.getId());
        
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1); // Le résultat du COUNT est dans la première colonne
            }
        }
    }
    return 0;
}

/**
 * Vérifie si le nombre de joueurs inscrits est suffisant pour former au moins un match.
 * @return True si le minimum est atteint, False sinon.
 * @throws SQLException 
 */
public boolean estNombreJoueursSuffisant() throws SQLException {
    int nbJoueursInscrits = this.compterJoueursInscrits();
    // Récupération du paramètre global défini lors de la création du tournoi
    int nbJoueursParEquipe = this.getNbrJoueursParEquipe(); 
    
    // Calcul du minimum requis : 2 équipes * (Joueurs par équipe)
    int minimumRequis = 2 * nbJoueursParEquipe; 
    
    return nbJoueursInscrits >= minimumRequis;
}


/**
 * Démarre la première ronde si toutes les conditions sont remplies.
 * @throws Exception si le nombre de joueurs est insuffisant.
 */
public Ronde demarrerPremiereRonde() throws Exception {
    
    // 1. Vérification de la condition demandée
    if (!this.estNombreJoueursSuffisant()) {
        int nbJoueursInscrits = this.compterJoueursInscrits();
        int nbJoueursParEquipe = this.getNbrJoueursParEquipe();
        int minimumRequis = 2 * nbJoueursParEquipe;
        
        // Lance une exception claire que la VueDetailsTournoi pourra afficher
        throw new Exception(
            "Impossible de démarrer la ronde. Nombre de joueurs insuffisant : " +
            nbJoueursInscrits + " inscrits. Minimum requis pour former 2 équipes : " + minimumRequis + 
            " (" + nbJoueursParEquipe + " joueurs par équipe)."
        );
    }
    
    // 2. Création de la Ronde 
    // Attention : Tu devras implémenter le saveInDB pour Ronde et sa logique de numéro !
    Ronde nouvelleRonde = new Ronde(1, this.getId(), StatutRonde.EN_COURS);
    
    // Étape technique future :
    // - Sauvegarder la nouvelleRonde en BDD (nouvelleRonde.saveInDB(...))
    // - Appeler la méthode de répartition des joueurs et de création des Matchs (Étape suivante)
    // - Mettre à jour le statut du Tournoi (this.setOuvert(true) puis this.updateInDB())

    return nouvelleRonde;
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
