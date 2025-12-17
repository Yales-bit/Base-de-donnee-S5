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
import fr.insa.toto.model.StatutSexe;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;

import org.hibernate.annotations.processing.SQL;

import fr.insa.beuvron.utils.database.ClasseMiroir;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.beuvron.utils.database.ConnectionSimpleSGBD;


public class Joueur extends ClasseMiroir {
    private String prenom;
    private String nom;
    private int mois;
    private int jour;
    private int annee;
    private int scoretotal;
    private String surnom;
    private StatutSexe sexe;
    private int taille;
    private int pointsDansTournoi = 0;

    //Constructeur utilisé quand on ne connait pas encore l'id du joueur (il vient d'être créé) A SUPPRIMER
    public Joueur(String surnom, StatutSexe sexe, int taille) {
        super();
        this.surnom = surnom;
        this.sexe = sexe;
        this.taille = taille;
        this.scoretotal = 0;
        this.prenom = null;
        this.nom = null;
        this.mois = 0;
        this.jour = 0;
        this.annee = 0;
    }
    //Constructeur avec nom, prenom, date de naissance
    public Joueur(String surnom, StatutSexe sexe, int taille, String prenom, String nom, int mois, int jour, int annee) {
        super();
        this.surnom = surnom;
        this.sexe = sexe;
        this.taille = taille;
        this.prenom = prenom;
        this.nom = nom;
        this.mois = mois;
        this.jour = jour;
        this.annee = annee;
    }

    //Constructeur utilisé quand on connait l'id du joueur (il vient d'avoir son id attribué) A SUPPRIMER
    /*public Joueur(int id, String surnom, StatutSexe sexe, int taille) {
        super(id);
        this.surnom = surnom;
        this.sexe = sexe;
        this.taille = taille;
        this.scoretotal = 0;
        this.prenom = null;
        this.nom = null;
        this.mois = 0;
        this.jour = 0;
        this.annee = 0;
    }*/

    //Constructeur avec id, nom, prenom, date de naissance
    public Joueur(int id, String surnom, StatutSexe sexe, int taille, String prenom, String nom, int mois, int jour, int annee) {
        super(id);
        this.surnom = surnom;
        this.sexe = sexe;
        this.taille = taille;
        this.prenom = prenom;
        this.nom = nom;
        this.mois = mois;
        this.jour = jour;
        this.annee = annee;
    }

    //Constructeur avec score total A SUPPRIMER
    /*public Joueur(int id, String surnom, StatutSexe sexe, int taille, int scoretotal) {
        super(id);
        this.surnom = surnom;
        this.sexe = sexe;
        this.taille = taille;
        this.scoretotal = scoretotal;
    }*/

    //Constructeur avec tout sauf annee mois jour
    public Joueur(int id, String surnom, StatutSexe sexe, int taille, String prenom, String nom) {
        super(id);
        this.surnom = surnom;
        this.sexe = sexe;
        this.taille = taille;
        this.prenom = prenom;
        this.nom = nom;
    }

    public static void creerJoueur(Joueur J) throws SQLException {
        if (J.getSurnom() == null || J.getSurnom().isEmpty()) {
            throw new SQLException("Le surnom est obligatoire");
        }
        if (J.getTaille() <= 100) {
            throw new SQLException("La taille (en cm) doit etre supérieure à 100 cm");
        }
        if (J.getSexe().isEmpty()) {
            throw new SQLException("Le sexe est obligatoire");
        }
        if (J.getPrenom() == null || J.getPrenom().isEmpty()) {
            throw new SQLException("Le prenom est obligatoire");
        }
        if (J.getNom() == null || J.getNom().isEmpty()) {
            throw new SQLException("Le nom est obligatoire");
        }
        if (J.getMois() < 1 || J.getMois() > 12) {
            throw new SQLException("Le mois est obligatoire et doit etre compris entre 1 et 12");
        }
        if (J.getJour() < 1 || J.getJour() > 31) {
            throw new SQLException("Le jour est obligatoire et doit etre compris entre 1 et 31");
        }
        if (J.getAnnee() < 1900 || J.getAnnee() > 2100) {
            throw new SQLException("L'annee est obligatoire et doit etre compris entre 1900 et 2100");
        }

        try (Connection con = ConnectionPool.getConnection()) {
            J.saveInDB(con);
        }
        catch (Exception e) {
            String sqlErrorMessage = e.getMessage();
            if (sqlErrorMessage != null && (sqlErrorMessage.contains("Duplicate entry") || sqlErrorMessage.contains("Violation d'index unique")) && sqlErrorMessage.toLowerCase().contains("surnom")) {
                throw new SQLException("Le joueur "+J.getSurnom()+" existe deja"); //Par la suite ajouter une option permettant de modifier le joueur
            }
        }

        
    }

    // 2. Méthode pour rechercher des joueurs par une partie de leur surnom
    public static List<Joueur> rechercherParSurnom(String recherche) throws SQLException {
        List<Joueur> resultats = new ArrayList<>();
        try (Connection con = ConnectionPool.getConnection()) {
            // Le % permet de chercher "n'importe quoi" avant ou après le texte
            PreparedStatement pst = con.prepareStatement("SELECT * FROM Joueur WHERE surnom LIKE ?");
            pst.setString(1, "%" + recherche + "%");
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String surnom = rs.getString("surnom");
                String catStr = rs.getString("sexe");
                int taille = rs.getInt("taille");
                String prenom = rs.getString("prenom");
                String nom = rs.getString("nom");
                int mois = rs.getInt("mois");
                int jour = rs.getInt("jour");
                int annee = rs.getInt("annee");
                StatutSexe sexe = null;
                try {
                    if(catStr != null) sexe = StatutSexe.valueOf(catStr);
                } catch (IllegalArgumentException e) { sexe = StatutSexe.MASCULIN; }

                resultats.add(new Joueur(id, surnom, sexe, taille, prenom, nom, mois, jour, annee));
            }
        }
        return resultats;
    }
    public static List<Joueur> rechercherParPrenom(String recherche) throws SQLException {
        List<Joueur> resultats = new ArrayList<>();
        try (Connection con = ConnectionPool.getConnection()) {
            // Le % permet de chercher "n'importe quoi" avant ou après le texte
            PreparedStatement pst = con.prepareStatement("SELECT * FROM Joueur WHERE prenom LIKE ?");
            pst.setString(1, "%" + recherche + "%");
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String surnom = rs.getString("surnom");
                String catStr = rs.getString("sexe");
                int taille = rs.getInt("taille");
                String prenom = rs.getString("prenom");
                String nom = rs.getString("nom");
                int mois = rs.getInt("mois");
                int jour = rs.getInt("jour");
                int annee = rs.getInt("annee");

                StatutSexe sexe = null;
                try {
                    if(catStr != null) sexe = StatutSexe.valueOf(catStr);
                } catch (IllegalArgumentException e) { sexe = StatutSexe.MASCULIN; }

                resultats.add(new Joueur(id, surnom, sexe, taille, prenom, nom, mois, jour, annee));
            }
        }
        return resultats;
    }

    public static List<Joueur> rechercherParNom(String recherche) throws SQLException {
        List<Joueur> resultats = new ArrayList<>();
        try (Connection con = ConnectionPool.getConnection()) {
            // Le % permet de chercher "n'importe quoi" avant ou après le texte
            PreparedStatement pst = con.prepareStatement("SELECT * FROM Joueur WHERE nom LIKE ?");
            pst.setString(1, "%" + recherche + "%");
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String surnom = rs.getString("surnom");
                String prenom = rs.getString("prenom");
                String nom = rs.getString("nom");
                String catStr = rs.getString("sexe");
                int taille = rs.getInt("taille");
                int mois = rs.getInt("mois");
                int jour = rs.getInt("jour");
                int annee = rs.getInt("annee");
                StatutSexe sexe = null;
                try {
                    if(catStr != null) sexe = StatutSexe.valueOf(catStr);
                } catch (IllegalArgumentException e) { sexe = StatutSexe.MASCULIN; }

                resultats.add(new Joueur(id, surnom, sexe, taille, prenom, nom, mois, jour, annee));
            }
        }
        return resultats;
    }



    // 1. Méthode pour récupérer un joueur unique par son ID
    public static Joueur getJoueurById(int id) throws SQLException {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement("SELECT * FROM Joueur WHERE id = ?");
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                // On reconstitue l'objet depuis la BDD
                String surnom = rs.getString("surnom");
                String catStr = rs.getString("sexe");
                int taille = rs.getInt("taille");
                String prenom = rs.getString("prenom");
                String nom = rs.getString("nom"); 
                int mois = rs.getInt("mois");
                int jour = rs.getInt("jour");
                int annee = rs.getInt("annee");
                
                // Gestion sécurisée de l'enum (si null ou invalide)
                StatutSexe sexe = null;
                try {
                    if(catStr != null) sexe = StatutSexe.valueOf(catStr);
                } catch (IllegalArgumentException e) {
                    sexe = StatutSexe.MASCULIN; // Valeur par défaut si erreur
                }

                return new Joueur(id, surnom, sexe, taille, prenom, nom, mois, jour, annee);
            }
        }
        return null; // Pas trouvé
    }

   /* public void ajouterPoints(int points) { this.scoretotal += points; }
    public void update(Connection con) throws SQLException, Exception {
        if (this.getId() == -1) {
            throw new Exception("Impossible de mettre à jour ce joueur : il n'a pas encore été sauvegardé en base de données (son ID est -1). Utilisez saveInDB() d'abord.");
        }
        String query = "UPDATE Joueur SET surnom = ?, taille = ?, sexe = ?, prenom = ?, nom = ?, mois = ?, jour = ?, annee = ? WHERE id = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, this.surnom);
            pst.setInt(2, this.taille);
            pst.setString(3, this.sexe.toString());
            pst.setString(4, this.prenom);
            pst.setString(5, this.nom);
            pst.setInt(6, this.mois);
            pst.setInt(7, this.jour);
            pst.setInt(8, this.annee);
            // Le dernier paramètre est l'ID pour le WHERE
            pst.setInt(5, this.getId());

            // Exécution
            int rowsAffected = pst.executeUpdate();
            // (Optionnel) Vérification de sécurité
            if (rowsAffected == 0) {
                throw new Exception("Erreur : L'ID " + this.getId() + " n'a pas été trouvé en base de données. Aucune mise à jour effectuée.");
            }
        }
    }*/ //cette methode n'a plus lieu car la table joueur n'a plus de score, c'est gerer par la table points
    public static List<Joueur> getClassementGeneral() throws Exception {
        List<Joueur> classement = new ArrayList<>();
        String query = "SELECT * FROM Joueur ORDER BY scoretotal DESC"; // OBSOLETE, le score n'est plus dans la table joueur
        try (Connection con = ConnectionPool.getConnection();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String surnom = rs.getString("surnom");
                StatutSexe sexe = StatutSexe.valueOf(rs.getString("sexe"));
                int taille = rs.getInt("taille");
                String prenom = rs.getString("prenom");
                String nom = rs.getString("nom");
                int mois = rs.getInt("mois");
                int jour = rs.getInt("jour");
                int annee = rs.getInt("annee");
                Joueur j = new Joueur(id, surnom, sexe, taille, prenom, nom, mois, jour, annee);
                classement.add(j);
            }
            }
            catch (IllegalArgumentException e) {
            // Erreur spécifique si la valeur du sexe en BDD ne correspond pas à l'Enum
                throw new Exception("Erreur de données en base : statut de sexe inconnu.");
            } catch (SQLException e) {
                throw new Exception("Erreur technique lors de la récupération du classement.", e);
            }
        return classement;
    }

    



            @Override
    protected Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement pst = con.prepareStatement("insert into Joueur (surnom, sexe, taille, prenom, nom, mois, jour, annee) \n"
                + "values(?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
        pst.setString(1, this.surnom);
        pst.setString(2, this.sexe.toString());
        pst.setInt(3, this.taille);
        pst.setString(4, this.prenom);
        pst.setString(5, this.nom);
        pst.setInt(6, this.mois);
        pst.setInt(7, this.jour);
        pst.setInt(8, this.annee);
        pst.executeUpdate();
        return pst;

    }

    
 //  Récupérer le nombre de joueurs en base
    public static int getNombreJoueurs() throws SQLException {
         try (Connection con = ConnectionPool.getConnection()){

        PreparedStatement stmt = con.prepareStatement("SELECT COUNT(*) FROM Joueur");
        ResultSet rs = stmt.executeQuery();

        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }

        rs.close();
        stmt.close();
        con.close();

        return count;
    }}


    // Récupérer la liste de tous les joueurs existants
    public static List<Joueur> getAllJoueurs() throws SQLException {
        List<Joueur> list = new ArrayList<>();
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement("SELECT * FROM Joueur");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                // On réutilise le code de mapping existant ou on le simplifie ici
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

    public static List<Joueur> getClassementTournoi(int tournoiId) throws SQLException {
    List<Joueur> classement = new ArrayList<>();
    //on prend les infos du joueur ET ses points dans la table de liaison
    String query = "SELECT j.*, p.points " +
                   "FROM Joueur j " +
                   "JOIN Points p ON j.id = p.idjoueur " +
                   "WHERE p.idtournoi = ? " +
                   "ORDER BY p.points DESC, j.surnom ASC"; // Tri par points, puis par surnom si égalité

    try (Connection con = ConnectionPool.getConnection();
         PreparedStatement pst = con.prepareStatement(query)) {
        
        pst.setInt(1, tournoiId);
        
        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                // 1. Reconstituer le Joueur de base
                Joueur j = new Joueur(
                    rs.getInt("id"),
                    rs.getString("surnom"),
                    StatutSexe.valueOf(rs.getString("sexe")),
                    rs.getInt("taille"),
                    rs.getString("prenom"),
                    rs.getString("nom"),
                    rs.getInt("mois"), rs.getInt("jour"), rs.getInt("annee")
                );
                
                // 2. Lui ajouter ses points pour ce tournoi (récupérés de la table Points)
                j.setPointsDansTournoi(rs.getInt("points"));
                
                classement.add(j);
            }
        }
    }
    return classement;
}
    public int getPointsDansTournoi() { return pointsDansTournoi; }
    public void setPointsDansTournoi(int points) { this.pointsDansTournoi = points; }



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
    public int getScoretotal() { //a supprimer
        return scoretotal;
    }
    public void setScoretotal(int scoretotal) {
        this.scoretotal = scoretotal;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getMois() {
        return mois;
    }

    public void setMois(int mois) {
        this.mois = mois;
    }

    public int getJour() {
        return jour;
    }

    public void setJour(int jour) {
        this.jour = jour;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }


  
 //test commit Hack   
}
