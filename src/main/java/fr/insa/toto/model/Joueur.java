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
    private int scoretotal;
    private String surnom;
    private StatutSexe sexe;
    private int taille;

    //Constructeur utilisé quand on ne connait pas encore l'id du joueur (il vient d'être créé)
    public Joueur(String surnom, StatutSexe sexe, int taille) {
        super();
        this.surnom = surnom;
        this.sexe = sexe;
        this.taille = taille;
        this.scoretotal = 0;
    }

    //Constructeur utilisé quand on connait l'id du joueur (il vient d'avoir son id attribué)
    public Joueur(int id, String surnom, StatutSexe sexe, int taille) {
        super(id);
        this.surnom = surnom;
        this.sexe = sexe;
        this.taille = taille;
        this.scoretotal = 0;
    }

    //Constructeur avec score total
    public Joueur(int id, String surnom, StatutSexe sexe, int taille, int scoretotal) {
        super(id);
        this.surnom = surnom;
        this.sexe = sexe;
        this.taille = taille;
        this.scoretotal = scoretotal;
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

    public void ajouterPoints(int points) { this.scoretotal += points; }
    public void update(Connection con) throws SQLException, Exception {
        if (this.getId() == -1) {
            throw new Exception("Impossible de mettre à jour ce joueur : il n'a pas encore été sauvegardé en base de données (son ID est -1). Utilisez saveInDB() d'abord.");
        }
        String query = "UPDATE Joueur SET surnom = ?, taille = ?, sexe = ?, scoretotal = ? WHERE id = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, this.surnom);
            pst.setInt(2, this.taille);
            pst.setString(3, this.sexe.toString());
            pst.setInt(4, this.scoretotal);
            // Le dernier paramètre est l'ID pour le WHERE
            pst.setInt(5, this.getId());

            // Exécution
            int rowsAffected = pst.executeUpdate();
            // (Optionnel) Vérification de sécurité
            if (rowsAffected == 0) {
                throw new Exception("Erreur : L'ID " + this.getId() + " n'a pas été trouvé en base de données. Aucune mise à jour effectuée.");
            }
        }
    }
    public static List<Joueur> getClassementGeneral() throws Exception {
        List<Joueur> classement = new ArrayList<>();
        String query = "SELECT * FROM Joueur ORDER BY scoretotal DESC";
        try (Connection con = ConnectionPool.getConnection();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String surnom = rs.getString("surnom");
                StatutSexe sexe = StatutSexe.valueOf(rs.getString("sexe"));
                int taille = rs.getInt("taille");
                int scoretotal = rs.getInt("scoretotal");
                Joueur j = new Joueur(id, surnom, sexe, taille, scoretotal);
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
        PreparedStatement pst = con.prepareStatement("insert into Joueur (surnom, sexe, taille, scoretotal ) \n"
                + "values(?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
        pst.setString(1, this.surnom);
        pst.setString(2, this.sexe.toString());
        pst.setInt(3, this.taille);
        pst.setInt(4, this.scoretotal); 
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
    public int getScoretotal() {
        return scoretotal;
    }
    public void setScoretotal(int scoretotal) {
        this.scoretotal = scoretotal;
    }
  
 //test commit Hack   
}
