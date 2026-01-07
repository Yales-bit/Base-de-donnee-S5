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
import fr.insa.toto.model.dto.LigneClassement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private boolean ouvert = true;
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
    // Constructeur sans id et sans specification du nombre d'équipes : à utiliser à compter du 09/12/25
    public Tournoi(int nbrjoueursparequipe, int dureematch, int nbrrondes, String nom, int nbreterrains, boolean ouvert, boolean fini) {
        super();
        this.nbrjoueursparequipe = nbrjoueursparequipe;
        this.dureematch = dureematch;
        this.nbrrondes = nbrrondes;
        this.nom = nom;
        this.nbreterrains = nbreterrains;
        this.ouvert = ouvert;
        this.fini = fini;
        this.nbrequipemin = 2; 
        this.nbrequipemax = -1;
    }
    //Constructeur avec id sans spécification du nombre d'équipes : à utiliser à compter du 09/12/25. INVERSION DE L'ORDRE DE NOM ET NBRETERRAINS PAR 
    public Tournoi(int id, int nbrjoueursparequipe, int dureematch, int nbrrondes,  int nbreterrains, String nom, boolean ouvert, boolean fini) {
        super(id);
        this.nbrjoueursparequipe = nbrjoueursparequipe;
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
        /*if (T.getNbEquipes()<2){
            throw new Exception("Le tournoi doit avoir au moins 2 equipes");
        }*/ // Obsolète depuis que c'est le nombre de terrains qui fixe cette valeur.
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
                //int nbEquipes = rs.getInt("nbrequipes");
                int duree = rs.getInt("dureematch");
                int nbRondes = rs.getInt("nbrrondes");
                int nbTerrains = rs.getInt("nbreterrains");
                //int min = rs.getInt("nbrequipemax");
                //int max = rs.getInt("nbrequipemin");
                boolean ouvert = rs.getBoolean("ouvert");
                boolean fini = rs.getBoolean("fini");

                // On reconstruit l'objet
                list.add(new Tournoi(id, nbJoueurs, duree, nbRondes, nbTerrains, nom, ouvert, fini)); // max et min supprimé, à voir...
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
                //int nbEquipes = rs.getInt("nbrequipes");
                int duree = rs.getInt("dureematch");
                int nbRondes = rs.getInt("nbrrondes");
                int nbTerrains = rs.getInt("nbreterrains");
                //int min = rs.getInt("nbrequipemax");
                //int max = rs.getInt("nbrequipemin");
                boolean ouvert = rs.getBoolean("ouvert");
                boolean fini = rs.getBoolean("fini");

                return new Tournoi(id, nbJoueurs, duree, nbRondes,  nbTerrains, nom, ouvert, fini);
            }
        }
        return null; // Pas trouvé
    }


@Override
protected Statement saveSansId(Connection con) throws SQLException {
    PreparedStatement pst = con.prepareStatement("insert into Tournoi (nbrjoueursparequipe, dureematch, nbrequipemax, nbrequipemin, nbrrondes, nom, nbreterrains, ouvert, fini) \n"
            + "values(?,?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
    
    pst.setInt(1, this.nbrjoueursparequipe);
    //pst.setInt(2, this.nbrequipes);
    pst.setInt(2, this.dureematch);
    pst.setInt(3, this.nbrequipemax);
    pst.setInt(4, this.nbrequipemin);
    pst.setInt(5, this.nbrrondes);
    pst.setString(6, this.nom);
    pst.setInt(7, this.nbreterrains);
    pst.setBoolean(8, this.ouvert);
    pst.setBoolean(9, this.fini);
    pst.executeUpdate();
    return pst;
}



public void inscrireJoueurs(List<Joueur> joueurs) throws Exception {
    //vérifie si le tournoi est ouvert
    if (!this.isOuvert()) {
        // On bloque tout de suite avec une erreur claire
        throw new Exception("Action impossible : Les inscriptions pour le tournoi '" + this.getNom() + "' sont fermées.");
    }

    // le tournoi existe-t-il en base ?
    if (this.getId() == -1) {
        throw new Exception("Erreur technique : Impossible d'inscrire des joueurs à un tournoi non sauvegardé.");
    }
    
    if (joueurs == null || joueurs.isEmpty()) {
         return; // Rien à faire si la liste est vide
    }

    // 3. Si c'est ouvert, on procède à l'inscription normalement
    try (Connection con = ConnectionPool.getConnection()) {
        String sql = "INSERT INTO Inscription (idtournoi, idjoueur) VALUES (?, ?)";

        
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            for (Joueur j : joueurs) {
                pst.setInt(1, this.getId()); 
                pst.setInt(2, j.getId()); 
                pst.addBatch(); 
            }
            pst.executeBatch(); 
        }
    }
}
    
    public List<Joueur> getJoueursInscrits() throws SQLException {
        List<Joueur> list = new ArrayList<>();
        String sql = "SELECT j.* FROM Joueur j JOIN Inscription i ON j.id = i.idjoueur WHERE i.idtournoi = ?";
        
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, this.getId());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                
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
   
    String sql = "SELECT COUNT(idjoueur) FROM Inscription WHERE idtournoi = ?";
    
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


private void genererMatchsPourRonde(Ronde ronde) throws Exception {
        List<Joueur> tousInscrits = this.getJoueursInscrits();
        
        // Si c'est la ronde 1, numeroRonde-1 = 0, ce qui est géré par getJoueursPrioritaires
        List<Joueur> prioritaires = Ronde.getJoueursPrioritaires(this.getId(), ronde.getNumero());
        
        List<Joueur> normaux = new ArrayList<>(tousInscrits);

        // On retire de la liste 'normaux' ceux qui sont prioritaires
        for (Joueur prioritaire : prioritaires) {
            normaux.removeIf(joueurNormal -> joueurNormal.getId() == prioritaire.getId());
        }

        // Mélange aléatoire des deux groupes séparément
        Collections.shuffle(prioritaires);
        Collections.shuffle(normaux);
        
        List<Joueur> poolDeTirage = new ArrayList<>();
        // Les prioritaires EN PREMIER
        poolDeTirage.addAll(prioritaires);
        poolDeTirage.addAll(normaux);

        int totalJoueursDispo = poolDeTirage.size();
        int nbJoueursParEquipe = this.getNbrJoueursParEquipe();
        int nbJoueursParMatch = nbJoueursParEquipe * 2;
        
        // Vérification minimale : peut-on faire au moins UN match ?
        if (totalJoueursDispo < nbJoueursParMatch) {
            //  pas assez de monde. On ne peut rien générer.
            throw new Exception("Pas assez de joueurs disponibles (" + totalJoueursDispo + ") pour générer ne serait-ce qu'un match (" + nbJoueursParMatch + " requis).");
        }
        
        int maxMatchsParJoueurs = totalJoueursDispo / nbJoueursParMatch;
        int nbrTerrainsDispo = this.getNbrTerrains();
        int nombreMatchsAGenerer = Math.min(maxMatchsParJoueurs, nbrTerrainsDispo);
        
        System.out.println("Info Ronde " + ronde.getNumero() + " : " + totalJoueursDispo + " joueurs, " 
                + nbrTerrainsDispo + " terrains -> Génération de " + nombreMatchsAGenerer + " matchs.");

        // Liste pour mémoriser ceux qui VONT jouer dans cette ronde
        List<Joueur> participantsDeCetteRonde = new ArrayList<>();

        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            con.setAutoCommit(false); // Transaction

            // On boucle exactement le nombre de fois calculé
            for (int i = 0; i < nombreMatchsAGenerer; i++) {
                List<Joueur> equipeA = new ArrayList<>();
                List<Joueur> equipeB = new ArrayList<>();

                // On "pioche" les joueurs en tête de liste et on les retire du pool
                for (int k = 0; k < nbJoueursParEquipe; k++) equipeA.add(poolDeTirage.remove(0));
                for (int k = 0; k < nbJoueursParEquipe; k++) equipeB.add(poolDeTirage.remove(0));
                participantsDeCetteRonde.addAll(equipeA);
                participantsDeCetteRonde.addAll(equipeB);

                // Création Equipes et Match en base
                String nomEq1 = "T" + this.getId() + "-R" + ronde.getNumero() + "-M" + (i + 1) + "-A";
                Equipe eq1 = new Equipe(nomEq1, ronde.getId());
                eq1.saveInDB(con);
                eq1.ajouterJoueurs(con, equipeA);

                String nomEq2 = "T" + this.getId() + "-R" + ronde.getNumero() + "-M" + (i + 1) + "-B";
                Equipe eq2 = new Equipe(nomEq2, ronde.getId());
                eq2.saveInDB(con);
                eq2.ajouterJoueurs(con, equipeB);

                Match m = new Match(ronde, eq1, eq2);
                m.saveInDB(con);
            }

            // Elle supprime les anciens (si besoin) et insère les nouveaux dans la même transaction.
            Ronde.updateParticipantsDeLaRonde(ronde.getId(), participantsDeCetteRonde, con);

            con.commit(); // Validation de tout le bloc
            
            int nbExempts = poolDeTirage.size(); // Ceux qui restent dans la liste initiale
            System.out.println(nombreMatchsAGenerer + " matchs générés pour ronde " + ronde.getNumero() + ". " 
                    + participantsDeCetteRonde.size() + " joueurs participants. " + nbExempts + " exemptés.");

        } catch (Exception e) {
            if (con != null) try { con.rollback(); } catch (SQLException ex) {}
            throw new Exception("Erreur génération matchs ronde " + ronde.getNumero(), e);
        } finally {
            if (con != null) { con.setAutoCommit(true); con.close(); }
        }
    }


public void lancerTournoi() throws Exception {
    if (!this.estNombreJoueursSuffisant()) {
        throw new Exception(
            "Impossible de démarrer le tournoi. Nombre de joueurs insuffisant : " +
            this.compterJoueursInscrits() + " inscrits. Minimum requis pour former 2 équipes : " + 
            (2 * this.getNbrJoueursParEquipe()) + " (" + this.getNbrJoueursParEquipe() + " joueurs par équipe)."
        );
    }//fermeture
    this.setOuvert(false);
    try (Connection con = ConnectionPool.getConnection()) {
        this.updateStatutTournoi(con);
    }
    //creation de toutes les rondes (vides)
    int totalRondes = this.getNbrRondes();

    for (int i =1; i <= totalRondes; i++) {
        StatutRonde statutInit = (i==1) ? StatutRonde.EN_COURS : StatutRonde.EN_ATTENTE;
        Ronde r = new Ronde(i, this.getId(), statutInit);

        try (Connection con = ConnectionPool.getConnection()) {
            r.saveInDB(con);
        }
        //on lance seulement la ronde 1
        if (i == 1) {
            this.genererMatchsPourRonde(r);
        }
    }
    System.out.println("Tournoi demarré !"+totalRondes+" rondes créees.");
}

public void passerRondeSuivante() throws Exception {
    System.out.println("Tentative de passage à la ronde suivante pour le tournoi " + this.getId());

    // quelle est la prochaine ronde en attente ?
    Ronde prochaineRonde = Ronde.getProchaineRondeEnAttente(this.getId());

    if (prochaineRonde == null) {
        //Plus aucune ronde en attente. Le tournoi est donc TERMINÉ.
        System.out.println("Aucune ronde en attente trouvée. Fin du tournoi " + this.getNom());

        this.setFini(true);
        this.setOuvert(false); // On ferme le tournoi par sécurité

        try (Connection con = ConnectionPool.getConnection()) {
            // On sauvegarde le nouveau statut "fini" en base
            this.updateStatutTournoi(con);
        }
        return;
    }

    // Une ronde en attente a été trouvée. ON LA DÉMARRE.
    System.out.println("Prochaine ronde trouvée : Ronde n°" + prochaineRonde.getNumero() + " (ID: " + prochaineRonde.getId() + "). Démarrage...");
    prochaineRonde.updateStatutRonde(StatutRonde.EN_COURS);
    this.genererMatchsPourRonde(prochaineRonde);

    System.out.println("Ronde n°" + prochaineRonde.getNumero() + " démarrée avec succès.");
}

public static void supprimerTournoi(int idTournoi) throws Exception {
    Connection con = null;
    try {
        con = ConnectionPool.getConnection();
        con.setAutoCommit(false); // Démarrage transaction

        System.out.println("Début de la suppression en cascade du tournoi " + idTournoi);

   
        deleteData(con, "DELETE FROM Points WHERE idtournoi = ?", idTournoi);
        deleteData(con, "DELETE FROM Inscription WHERE idtournoi = ?", idTournoi);


 
        deleteData(con, "DELETE FROM Matchs WHERE idronde IN (SELECT id FROM Ronde WHERE idtournoi = ?)", idTournoi);


        
        String sqlDeleteCompo = "DELETE c FROM Composition c INNER JOIN Equipe e ON c.idequipe = e.id INNER JOIN Ronde r ON e.idronde = r.id WHERE r.idtournoi = ?";
        deleteData(con, sqlDeleteCompo, idTournoi);


 
        String sqlDeleteEquipes = "DELETE e FROM Equipe e INNER JOIN Ronde r ON e.idronde = r.id WHERE r.idtournoi = ?";
        deleteData(con, sqlDeleteEquipes, idTournoi);



        String sqlDeleteParticipation = "DELETE FROM ParticipationRonde WHERE idronde IN (SELECT id FROM Ronde WHERE idtournoi = ?)";
        deleteData(con, sqlDeleteParticipation, idTournoi);


    
        deleteData(con, "DELETE FROM Ronde WHERE idtournoi = ?", idTournoi);



        int deleted = deleteData(con, "DELETE FROM Tournoi WHERE id = ?", idTournoi);

        if (deleted == 0) {
            throw new Exception("Tournoi introuvable (ID: " + idTournoi + "), aucune suppression effectuée.");
        }

        con.commit(); // Validation de la transaction
        System.out.println("Suppression du tournoi " + idTournoi + " terminée avec succès.");

    } catch (Exception e) {
        if (con != null) try { con.rollback(); System.err.println("Rollback effectué."); } catch (SQLException ex) {}
        throw new Exception("Erreur critique lors de la suppression du tournoi : " + e.getMessage(), e);
    } finally {
        if (con != null) try { con.setAutoCommit(true); con.close(); } catch (SQLException e) {}
    }
}

private static int deleteData(Connection con, String sql, int paramId) throws SQLException {
    try (PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setInt(1, paramId);
        return pst.executeUpdate();
    }
}

public void updateStatutTournoi(Connection con) throws SQLException {
    if (this.getId() == -1) {
         throw new SQLException("Impossible de mettre à jour le statut : le tournoi n'existe pas en base (ID = -1).");
    }

    String sql = "UPDATE Tournoi SET ouvert = ?, fini = ? WHERE id = ?";
    
    try (PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setBoolean(1, this.ouvert);
        pst.setBoolean(2, this.fini);
        pst.setInt(3, this.getId());
        pst.executeUpdate();
    }
}
public List<LigneClassement> getClassement() throws SQLException {
    List<LigneClassement> classement = new ArrayList<>();

    String sql = """
        SELECT j.*, p.points
        FROM Points p
        INNER JOIN Joueur j ON p.idjoueur = j.id
        WHERE p.idtournoi = ?
        ORDER BY p.points DESC, j.nom ASC, j.prenom ASC
    """;

    try (Connection con = ConnectionPool.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setInt(1, this.getId());

        try (ResultSet rs = pst.executeQuery()) {
            int rang = 1; // Compteur simple pour le rang
            
            while (rs.next()) {
                // 1. Reconstituer le Joueur (les colonnes j.* sont là)
                Joueur j = new Joueur(
                    rs.getInt("id"),
                    rs.getString("surnom"),
                    StatutSexe.valueOf(rs.getString("sexe")),
                    rs.getInt("taille"),
                    rs.getString("prenom"),
                    rs.getString("nom"),
                    rs.getInt("mois"),
                    rs.getInt("jour"),
                    rs.getInt("annee")
                );
                
                // 2. Récupérer les points directement depuis la colonne 'points'
                int points = rs.getInt("points");

                // 3. Créer la ligne et l'ajouter
                classement.add(new LigneClassement(rang, j, points));
                rang++;
            }
        }
    }
    return classement;
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
        // Règle métier : Si c'est ouvert, ce n'est pas fini.
        if (ouvert) {
            this.fini = false;
        }
}

    public boolean isFini() {
        return fini;
    }

    public void setFini(boolean fini) {
        this.fini = fini;
        // Règle métier : Si c'est fini, ce n'est plus ouvert.
        if (fini) {
            this.ouvert = false;
        }
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
