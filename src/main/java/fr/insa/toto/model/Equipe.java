package fr.insa.toto.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.insa.beuvron.utils.database.ClasseMiroir;
import fr.insa.beuvron.utils.database.ConnectionPool;

public class Equipe extends ClasseMiroir {
    private String nom;
    private int score;
    private int idronde;
    

    // //Constructeur utilisé quand on ne connait pas encore l'id de l'équipe (elle vient d'être créée)
    public Equipe(String nom, int score, int idronde) {
        super(); // associe -1 comme id
        this.nom = nom;
        this.score = score;
        this.idronde = idronde;
    }
        public Equipe(String nom, int idronde) {
        super(); // associe -1 comme id
        this.nom = nom;
        this.score = 0;
        this.idronde = idronde;
    }

    // Constructeur utilisé quand on connait l'id de l'équipe
    public Equipe(int idequipe, String nom, int score, int idronde) {
        super(idequipe);
        this.nom = nom;
        this.score = score;
        this.idronde = idronde;
    }
    public Equipe(int idequipe, String nom, int idronde) {
        super(idequipe);
        this.nom = nom;
        this.score = 0;
        this.idronde = idronde;
    }
public void ajouterJoueurs(Connection con, List<Joueur> joueurs) throws Exception {
       if (this.getId() == -1) {
             throw new Exception("Erreur technique : L'équipe doit être sauvegardée en base avant d'y ajouter des joueurs.");
       }

       String sql = "INSERT INTO Composition (idequipe, idjoueur) VALUES (?, ?)";
       
       // CORRECTION : On utilise la connexion passée en paramètre !
       // On enlève le try-with-resources sur la connection
       try (PreparedStatement pst = con.prepareStatement(sql)) {
           
           for (Joueur j : joueurs) {
               pst.setInt(1, this.getId());
               pst.setInt(2, j.getId());
               pst.addBatch();
           }
           pst.executeBatch(); 
       }
       // Pas de catch/finally ici, on laisse l'appelant gérer la transaction
}
    public List<Joueur> getJoueurs() throws Exception { //renvoit tous les joueurs d'une equipe
     List<Joueur> joueurs = new ArrayList<>();
        String sql = "SELECT j.* FROM Joueur j " +
                 "JOIN Composition c ON j.id = c.idjoueur " +
                 "WHERE c.idequipe = ?"; try (Connection con = ConnectionPool.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setInt(1, this.getId());
        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                // On suppose que les données en base sont valides (énums, dates...)
                Joueur j = new Joueur(
                    rs.getInt("id"), rs.getString("surnom"),
                    StatutSexe.valueOf(rs.getString("sexe")), rs.getInt("taille"),
                    rs.getString("prenom"), rs.getString("nom"),
                    rs.getInt("mois"), rs.getInt("jour"), rs.getInt("annee")
                );
                joueurs.add(j);
            }
        }
    }
    return joueurs;
}
public static Equipe getEquipeById(int id) throws SQLException {
    try (Connection con = ConnectionPool.getConnection()) {
        return getEquipeById(id, con);
    }
}


public static Equipe getEquipeById(int id, Connection con) throws SQLException {
    String sql = "SELECT * FROM Equipe WHERE id = ?";
    // Note : pas de try-with-resources sur 'con' ici !
    try (PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setInt(1, id);
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return new Equipe(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getInt("score"),
                    rs.getInt("idronde")
                );
            }
        }
    }
    return null;
}
public static List<Joueur> getJoueursDeLEquipe(int idEquipe) throws SQLException {
    List<Joueur> joueurs = new ArrayList<>();
    // Jointure entre Joueur et Composition pour trouver les membres de l'équipe
    String sql = """
        SELECT j.*
        FROM Joueur j
        INNER JOIN Composition c ON j.id = c.idjoueur
        WHERE c.idequipe = ?
    """;

    try (Connection con = ConnectionPool.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setInt(1, idEquipe);
        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                
                joueurs.add(new Joueur(
                    rs.getInt("id"),
                    rs.getString("surnom"),
                    StatutSexe.valueOf(rs.getString("sexe")),
                    rs.getInt("taille"),
                    rs.getString("prenom"),
                    rs.getString("nom"),
                    rs.getInt("mois"),
                    rs.getInt("jour"),
                    rs.getInt("annee")
                ));
            }
        }
    }
    return joueurs;
}
@Override
protected Statement saveSansId(Connection con) throws SQLException {
    // CORRECTION : Ajout de Statement.RETURN_GENERATED_KEYS
    PreparedStatement pst = con.prepareStatement(
        "INSERT INTO Equipe (nom, score, idronde) VALUES (?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS // <--- ICI
    );
    pst.setString(1, this.nom);
    pst.setInt(2, this.score);
    pst.setInt(3, this.idronde);
    pst.executeUpdate();
    return pst;
}
public void remplacerJoueurs(Connection con, List<Joueur> nouveauxJoueurs) throws SQLException {
    if (this.getId() <= 0) {
        throw new IllegalStateException("Impossible de modifier les joueurs d'une équipe non sauvegardée (ID invalide).");
    }

    // 1. SUPPRESSION des anciens liens dans la table Composition
    // On ne supprime pas les joueurs, juste leur lien avec cette équipe.
    String deleteSql = "DELETE FROM Composition WHERE idequipe = ?";
    try (PreparedStatement pstDel = con.prepareStatement(deleteSql)) {
        pstDel.setInt(1, this.getId());
        pstDel.executeUpdate();
    }

    // 2. INSERTION des nouveaux liens
    if (nouveauxJoueurs != null && !nouveauxJoueurs.isEmpty()) {
        String insertSql = "INSERT INTO Composition (idequipe, idjoueur) VALUES (?, ?)";
        try (PreparedStatement pstIns = con.prepareStatement(insertSql)) {
            for (Joueur j : nouveauxJoueurs) {
                pstIns.setInt(1, this.getId());
                pstIns.setInt(2, j.getId());
                // On utilise le batch pour la performance
                pstIns.addBatch(); 
            }
            // On exécute toutes les insertions d'un coup
            pstIns.executeBatch(); 
        }
    }
}

    // Getters and Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    
    public int getidronde() {
        return idronde;
    }

    public void setidronde(int idronde) {
        this.idronde = idronde;
    }
}   
