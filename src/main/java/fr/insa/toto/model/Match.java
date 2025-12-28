package fr.insa.toto.model;

import fr.insa.beuvron.utils.database.ClasseMiroir;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import fr.insa.beuvron.utils.database.ConnectionPool;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Match extends ClasseMiroir {

    private Ronde ronde;
    private Equipe equipe1;
    private Equipe equipe2;
    private Terrain terrain;
    private StatutMatch statut;

    public Match(Ronde ronde, Equipe equipe1, Equipe equipe2, Terrain terrain) { // potentiellement obsolète, on ne cree plus de match avec terrain
        this.ronde = ronde;
        this.equipe1 = equipe1;
        this.equipe2 = equipe2;
        this.terrain = terrain;
        this.statut = StatutMatch.EN_ATTENTE; // Par défaut, un nouveau match est "en cours"
    }
    public Match(Ronde ronde, Equipe equipe1, Equipe equipe2) {
        this.ronde = ronde;
        this.equipe1 = equipe1;
        this.equipe2 = equipe2;
        this.statut = StatutMatch.EN_ATTENTE; // Par défaut, un nouveau match est "en cours"
    }
    public Match(int id, Ronde ronde, Equipe equipe1, Equipe equipe2, StatutMatch statut) {
        super(id);
        this.ronde = ronde;
        this.equipe1 = equipe1;
        this.equipe2 = equipe2;
        this.statut = statut;
    }

@Override
protected Statement saveSansId(Connection con) throws SQLException {
    // CORRECTION : Ajout de Statement.RETURN_GENERATED_KEYS
    PreparedStatement pst = con.prepareStatement(
        "INSERT INTO Matchs (idronde, idequipe1, idequipe2, statut) VALUES (?, ?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS // <--- ET ICI
    );
    pst.setInt(1, this.ronde.getId());
    pst.setInt(2, this.equipe1.getId());
    pst.setInt(3, this.equipe2.getId());
    // Utilise name() pour l'enum
    pst.setString(4, this.statut.name());
    pst.executeUpdate();
    return pst;
}
public static Match getMatchById(int matchId, Connection con) throws SQLException {
    String sql = "SELECT * FROM Matchs WHERE id = ?";
    
    Connection connectionToUse = con;
    boolean mustCloseConnection = false;
    if (connectionToUse == null) {
        connectionToUse = ConnectionPool.getConnection();
        mustCloseConnection = true;
    }

    try (PreparedStatement pst = connectionToUse.prepareStatement(sql)) {
        pst.setInt(1, matchId);
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                int idRonde = rs.getInt("idronde");
                int idEq1 = rs.getInt("idequipe1");
                int idEq2 = rs.getInt("idequipe2");

                // --- CORRECTION ICI ---
                // On utilise les versions qui acceptent la connexion 'connectionToUse'
                Ronde ronde = Ronde.getRonde(idRonde, connectionToUse);
                Equipe eq1 = Equipe.getEquipeById(idEq1, connectionToUse);
                Equipe eq2 = Equipe.getEquipeById(idEq2, connectionToUse);

                if (ronde == null || eq1 == null || eq2 == null) {
                    throw new SQLException("Incohérence en base : données liées au match introuvables.");
                }

                return new Match(
                    rs.getInt("id"),
                    ronde,
                    eq1,
                    eq2,
                    StatutMatch.valueOf(rs.getString("statut"))
                );
            }
        }
    } finally {
        if (mustCloseConnection && connectionToUse != null) {
            connectionToUse.close();
        }
    }
    return null;
}
/*public static void validerResultatMatch(int matchId, int scoreEquipe1, int scoreEquipe2) throws Exception {
    Connection con = null;
    try {
        con = ConnectionPool.getConnection();
        // DÉBUT DE TRANSACTION : Important !
        con.setAutoCommit(false); //s'il y a une erreur on ne veut pas que seule une partie des changements soient appliqués, c'est du tout ou rien et par defaut setautocommit est true

        //récuperer le match 
        int idRonde = -1;
        StatutMatch statutActuel = null;
        int idEquipe1 = -1;
        int idEquipe2 = -1;
        
        try(PreparedStatement pstSel = con.prepareStatement("SELECT idronde, statut, idequipe1, idequipe2 FROM Matchs WHERE id = ?")) {
             pstSel.setInt(1, matchId);
             try(ResultSet rs = pstSel.executeQuery()){
                 if(rs.next()){
                     idRonde = rs.getInt("idronde");
                     statutActuel = StatutMatch.valueOf(rs.getString("statut"));
                     idEquipe1 = rs.getInt("idequipe1");
                     idEquipe2 = rs.getInt("idequipe2");
                 } else { throw new Exception("Match introuvable ID: " + matchId); }
             }
        }
        if (statutActuel == StatutMatch.TERMINE) {
            throw new Exception("Ce match est déjà terminé et validé.");
        }
        // mettre à jour les cores des equipes
        String updateEquipeSql = "UPDATE Equipe SET score = ? WHERE id = ?";
        try(PreparedStatement pstEq = con.prepareStatement(updateEquipeSql)) {
            // Equipe 1
            pstEq.setInt(1, scoreEquipe1);
            pstEq.setInt(2, idEquipe1);
            pstEq.executeUpdate();
            // Equipe 2
            pstEq.setInt(1, scoreEquipe2);
            pstEq.setInt(2, idEquipe2);
            pstEq.executeUpdate();
        }
        // terminer le match
        String updateMatchSql = "UPDATE Matchs SET statut = ? WHERE id = ?";
        try(PreparedStatement pstMatch = con.prepareStatement(updateMatchSql)) {
            pstMatch.setString(1, StatutMatch.TERMINE.name());
            pstMatch.setInt(2, matchId);
            pstMatch.executeUpdate();
        }

        //calcul des points 
        int idTournoi = -1; //initialisation
        try(PreparedStatement pstRonde = con.prepareStatement("SELECT idtournoi FROM Ronde WHERE id = ?")){
            pstRonde.setInt(1, idRonde);
            try(ResultSet rsR = pstRonde.executeQuery()){
                if(rsR.next()) idTournoi = rsR.getInt("idtournoi");
            }
        }

        Equipe eq1Complet = new Equipe(idEquipe1, null, 0); 
        Equipe eq2Complet = new Equipe(idEquipe2, null, 0);
        distribuerPointsAuxJoueurs(con, eq1Complet.getJoueurs(), scoreEquipe1, idTournoi);
        distribuerPointsAuxJoueurs(con, eq2Complet.getJoueurs(), scoreEquipe2, idTournoi);

        // FIN DE TRANSACTION : Si on arrive ici, tout est bon.
        con.commit();
        System.out.println("Match " + matchId + " validé avec succès. Points distribués.");
    }
    catch (Exception e) {
        // EN CAS D'ERREUR : On annule TOUT ce qui a été fait dans la BDD
        if (con != null) {
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
        throw new Exception("Erreur lors de la validation du match. Annulation de la transaction.", e);
    } finally {
        if (con != null) {
            con.setAutoCommit(true); // Rétablir le mode par défaut
            con.close();
        }
    }
}
*/

public static void sauvegarderScoresTemporaires(int matchId, int scoreEquipe1, int scoreEquipe2) throws Exception {
    String sql = """
        UPDATE Equipe e
        JOIN Matchs m ON (e.id = m.idequipe1 OR e.id = m.idequipe2)
        SET e.score = CASE 
            WHEN e.id = m.idequipe1 THEN ?
            WHEN e.id = m.idequipe2 THEN ?
        END
        WHERE m.id = ?;
    """;
    try (Connection con = ConnectionPool.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {
         
        pst.setInt(1, scoreEquipe1);
        pst.setInt(2, scoreEquipe2);
        pst.setInt(3, matchId);
        
        int updatedRows = pst.executeUpdate();
        if (updatedRows == 0) {
             throw new Exception("Impossible de mettre à jour les scores pour le match " + matchId);
        }
    } catch (SQLException e) {
        throw new Exception("Erreur SQL lors de la sauvegarde des scores : " + e.getMessage(), e);
    }
}
public static void distribuerPointsAuxJoueurs(Connection con, List<Joueur> joueurs, int pointsAGagner, int idTournoi) throws SQLException {
    if (pointsAGagner == 0 || joueurs.isEmpty()) return;

    String sqlUpsertPoints = """
        INSERT INTO Points (idjoueur, idtournoi, points) VALUES (?, ?, ?)
        ON DUPLICATE KEY UPDATE points = points + VALUES(points)
    """;
    
    try (PreparedStatement pst = con.prepareStatement(sqlUpsertPoints)) {
        for (Joueur j : joueurs) {
            pst.setInt(1, j.getId());
            pst.setInt(2, idTournoi);
            pst.setInt(3, pointsAGagner); 
            pst.addBatch();
        }
        pst.executeBatch();
    }
}
public static List<Match> getMatchsDeLaRonde(int idRonde) throws SQLException {
    try (Connection con = ConnectionPool.getConnection()) {
        return getMatchsDeLaRonde(idRonde, con);
    }
}

public static List<Match> getMatchsDeLaRonde(int idRonde, Connection con) throws SQLException {
    List<Match> matchs = new ArrayList<>();
    String sql = "SELECT id FROM Matchs WHERE idronde = ?";

    // Note : pas de try-with-resources sur 'con' ici !
    try (PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setInt(1, idRonde);
        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                int idMatch = rs.getInt("id");
                // On passe la connexion existante 'con'
                Match m = Match.getMatchById(idMatch, con);
                if (m != null) {
                    matchs.add(m);
                }
            }
        }
    }
    return matchs;
}
public void updateStatut(Connection con) throws SQLException {
     String sql = "UPDATE Matchs SET statut = ? WHERE id = ?";
     try (PreparedStatement pst = con.prepareStatement(sql)) {
         pst.setString(1, this.statut.name());
         pst.setInt(2, this.getId());
         pst.executeUpdate();
     }
}

    // Getters et Setters

    public Ronde getRonde() { return ronde; }
    public void setRonde(Ronde ronde) { this.ronde = ronde; }

    public Equipe getEquipe1() { return equipe1; }
    public void setEquipe1(Equipe equipe1) { this.equipe1 = equipe1; }

    public Equipe getEquipe2() { return equipe2; }
    public void setEquipe2(Equipe equipe2) { this.equipe2 = equipe2; }

    public StatutMatch getStatut() { return statut; }
    public void setStatut(StatutMatch statut) { this.statut = statut; }

    public Terrain getTerrain() { return terrain; }
    public void setTerrain(Terrain terrain) { this.terrain = terrain; }

    // Méthodes utilitaires
    public boolean isTermine() {
        return this.statut == StatutMatch.TERMINE;
    }
}
