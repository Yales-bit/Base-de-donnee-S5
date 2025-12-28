package fr.insa.toto.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.beuvron.utils.database.ClasseMiroir;

public class Ronde extends ClasseMiroir {

    private int numero; // Numéro de la ronde dans le tournoi (ex: 1, 2, 3)
    private StatutRonde statut; // Statut de la ronde (voir la classe StatutRonde)
    private int idtournoi; // La ronde appartient à un tournoi 

    public Ronde(int numero, int idtournoi, StatutRonde statut) {
        super();
        this.numero = numero;
        this.idtournoi = idtournoi;
        this.statut = statut;
    }

    public Ronde(int id, int numero, StatutRonde statut, int idtournoi) {
        super(id);
        this.numero = numero;
        this.statut = statut;
        this.idtournoi = idtournoi;
    }


@Override
protected Statement saveSansId(Connection con) throws SQLException {
    // CORRECTION ICI : Ajout de PreparedStatement.RETURN_GENERATED_KEYS
    PreparedStatement pst = con.prepareStatement(
        "INSERT INTO Ronde (numero, statut, idtournoi) VALUES (?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS // <--- C'EST ÇA QUI MANQUAIT
    );
    
    pst.setInt(1, this.numero);
    // Utilisation de name() pour l'enum, c'est plus sûr que toString()
    pst.setString(2, this.statut.name()); 
    pst.setInt(3, this.idtournoi);
    pst.executeUpdate();
    return pst;
}
    public static Ronde getRondeParNumero (int idTournoi, int numero) throws SQLException {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement("SELECT * FROM Ronde WHERE idtournoi = ? AND numero = ?");
            pst.setInt(1, idTournoi);
            pst.setInt(2, numero);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Ronde(rs.getInt("id"), rs.getInt("numero"), StatutRonde.valueOf(rs.getString("statut")), rs.getInt("idtournoi"));
            }
            return null;
        }
    }

    public static Ronde getRonde(int id) throws SQLException {
    try (Connection con = ConnectionPool.getConnection()) {
        return getRonde(id, con);
    }
}


    public static Ronde getDerniereRonde(int idTournoi) throws SQLException {
    // On sélectionne les rondes du tournoi, on trie par numéro décroissant (DESC)
    // et on ne prend que la première (LIMIT 1) -> c'est donc la plus récente.
    String query = "SELECT * FROM Ronde WHERE idtournoi = ? ORDER BY numero DESC LIMIT 1";
    
    try (Connection con = ConnectionPool.getConnection();
         PreparedStatement pst = con.prepareStatement(query)) {
        
        pst.setInt(1, idTournoi);
        
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                // On reconstruit l'objet Ronde à partir des données BDD
                return new Ronde(
                    rs.getInt("id"),
                    rs.getInt("numero"),
                    // Attention : s'assurer que la chaîne en BDD correspond exactement à l'enum
                    StatutRonde.valueOf(rs.getString("statut")), 
                    rs.getInt("idtournoi")
                );
            }
        }
    }
    return null; // Aucune ronde trouvée pour ce tournoi
}
public static Ronde getProchaineRondeEnAttente(int idTournoi) throws SQLException {
    // On cherche les rondes en attente, triées par numéro croissant, et on prend la première.
    String sql = "SELECT * FROM Ronde WHERE idtournoi = ? AND statut = ? ORDER BY numero ASC LIMIT 1";

    try (Connection con = ConnectionPool.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setInt(1, idTournoi);
        // On utilise .name() pour être sûr d'avoir la bonne chaîne "EN_ATTENTE"
        pst.setString(2, StatutRonde.EN_ATTENTE.name());

        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                // Mappage du résultat vers l'objet Ronde
                return new Ronde(
                    rs.getInt("id"),
                    rs.getInt("numero"),
                    StatutRonde.valueOf(rs.getString("statut")),
                    rs.getInt("idtournoi")
                );
            }
        }
    }
    return null; // Aucune ronde en attente trouvée
}

    public void updateStatutRonde (StatutRonde nouveauStatut) throws SQLException {
      this.statut = nouveauStatut;
      try (Connection con = ConnectionPool.getConnection()) {
        PreparedStatement pst = con.prepareStatement("UPDATE Ronde SET statut = ? WHERE id = ?");
        pst.setString(1, nouveauStatut.name());
        pst.setInt(2, this.getId());
        pst.executeUpdate();
      }  
    }

 
public boolean estTerminee() throws SQLException {
    if (this.getId() == -1) return false;

    // On compte le nombre de matchs de cette ronde qui NE sont PAS terminés.
    String sql = "SELECT COUNT(*) FROM Matchs WHERE idronde = ? AND statut != ?";
    
    try (Connection con = ConnectionPool.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {
        
        pst.setInt(1, this.getId());
        // Attention à bien utiliser la valeur String de l'enum
        pst.setString(2, StatutMatch.TERMINE.name());
        
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                int nbMatchsNonTermines = rs.getInt(1);
                // Si le compte est 0, cela veut dire que tous les matchs sont terminés.
                return nbMatchsNonTermines == 0;
            }
        }
    }
    return false; // Par sécurité 
}
    public void enregistrerParticipants(Connection con, List<Joueur> participants) throws SQLException { //pour savoir qui a joué cette ronde
        String sql = "INSERT INTO ParticipationRonde (idronde, idjoueur) VALUES (?, ?)";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            for (Joueur j : participants) {
                pst.setInt(1, this.getId());
                pst.setInt(2, j.getId());
                pst.addBatch();
            }
            pst.executeBatch();
        }
    }

   public static List<Joueur> getJoueursPrioritaires(int idTournoi, int numeroRondeActuelle) throws SQLException {
    List<Joueur> prioritaires = new ArrayList<>();

    // Si c'est la ronde 1 (ou moins), il n'y a pas de ronde précédente.
    if (numeroRondeActuelle <= 1) {
        return prioritaires;
    }

    int numeroRondePrecedente = numeroRondeActuelle - 1;

    // CORRECTION 1 : Utilisation de Text Block pour une requête SQL propre et sans erreur d'espace.
    String sql = """
        SELECT j.*
        FROM Joueur j
        JOIN Inscription i ON i.idjoueur = j.id
        LEFT JOIN (
            SELECT pr.idjoueur
            FROM ParticipationRonde pr
            JOIN Ronde r ON r.id = pr.idronde
            WHERE r.idtournoi = ? AND r.numero = ?
        ) AS participants_prec ON j.id = participants_prec.idjoueur
        WHERE i.idtournoi = ? AND participants_prec.idjoueur IS NULL;
    """;

    try (Connection con = ConnectionPool.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setInt(1, idTournoi);
        // CORRECTION 2 : On utilise bien la variable 'numeroRondePrecedente' calculée au début !
        pst.setInt(2, numeroRondePrecedente);
        pst.setInt(3, idTournoi);

        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                // Reconstitution du joueur
                prioritaires.add(new Joueur(
                    rs.getInt("id"), rs.getString("surnom"), StatutSexe.valueOf(rs.getString("sexe")),
                    rs.getInt("taille"), rs.getString("prenom"), rs.getString("nom"),
                    rs.getInt("mois"), rs.getInt("jour"), rs.getInt("annee")
                ));
            }
        }
    }
    return prioritaires;
}
/*public static List<Ronde> getRondesDuTournoi(int idTournoi) throws SQLException {
    List<Ronde> rondes = new ArrayList<>();
    String sql = "SELECT id FROM Ronde WHERE idtournoi = ? ORDER BY numero ASC";

    try (Connection con = ConnectionPool.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {
        
        pst.setInt(1, idTournoi);
        
        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                int idRonde = rs.getInt("id");
                Ronde r = Ronde.getRonde(idRonde);
                if (r != null) {
                    rondes.add(r);
                }
            }
        }
    }
    return rondes;
}*/
public static List<Ronde> getRondesDuTournoi(int idTournoi) throws SQLException {
    List<Ronde> rondes = new ArrayList<>();
    // CHANGEMENT SQL : On sélectionne TOUTES (*) les colonnes, pas juste l'ID
    String sql = "SELECT * FROM Ronde WHERE idtournoi = ? ORDER BY numero ASC";

    try (Connection con = ConnectionPool.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {
        
        pst.setInt(1, idTournoi);
        
        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                // --- CORRECTION MAJEURE ICI ---
                // Au lieu d'appeler Ronde.getRonde(id) qui ouvre une nouvelle connexion,
                // on reconstruit l'objet directement ici avec les données du ResultSet actuel.
                
                Ronde r = new Ronde(
                    rs.getInt("id"),
                    rs.getInt("numero"),
                    // Attention : assure-toi que la chaîne en BDD correspond exactement au nom de l'enum Java
                    StatutRonde.valueOf(rs.getString("statut")), 
                    rs.getInt("idtournoi")
                );
                
                rondes.add(r);
            }
        }
    }
    return rondes;
}

public void cloturerRondeEtDistribuerPoints() throws Exception {
    if (this.statut != StatutRonde.EN_COURS) {
        throw new Exception("Seule une ronde EN_COURS peut être clôturée.");
    }

    Connection con = null;
    try {
        con = ConnectionPool.getConnection();
        con.setAutoCommit(false); // TRANSACTION GÉANTE

        // 1. Récupérer tous les matchs de la ronde pour traiter les scores
        // On a besoin des objets complets (Equipes, Joueurs) pour attribuer les points
        List<Match> matchs = Match.getMatchsDeLaRonde(this.getId(),con); 

        for (Match m : matchs) {
            // Sécurité : si un match n'a pas de score (ex: 0-0 par défaut alors qu'ils n'ont pas joué)
            // C'est une règle métier à définir. Pour l'instant, on suppose que l'admin a fait son travail.

            // 2. Passer le match en TERMINE
            m.setStatut(StatutMatch.TERMINE);
            // On utilise une méthode d'update optimisée qui prend la connexion existante
            // (Il faut créer cette petite méthode dans Match.java, voir ci-dessous*)
            m.updateStatut(con);

            // 3. Distribuer les points pour ce match (utilise le UPSERT qu'on vient de corriger)
            Match.distribuerPointsAuxJoueurs(con, m.getEquipe1().getJoueurs(), m.getEquipe1().getScore(), this.idtournoi);
            Match.distribuerPointsAuxJoueurs(con, m.getEquipe2().getJoueurs(), m.getEquipe2().getScore(), this.idtournoi);
        }

        // 4. Passer la ronde en TERMINE
        this.statut = StatutRonde.TERMINEE;
        // Pareil, il faut une version transactionnelle de updateStatutRonde
        this.updateStatutRondeTransactionnel(con);

        con.commit(); // Tout est bon !
    } catch (Exception e) {
        if (con != null) try { con.rollback(); } catch (SQLException ex) {}
        throw new Exception("Erreur lors de la clôture de la ronde : " + e.getMessage(), e);
    } finally {
        if (con != null) { con.setAutoCommit(true); con.close(); }
    }
}
public static Ronde getRonde(int id, Connection con) throws SQLException {
    String sql = "SELECT * FROM Ronde WHERE id = ?";
    // Note : pas de try-with-resources sur 'con' ici !
    try (PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setInt(1, id);
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return new Ronde(
                    rs.getInt("id"),
                    rs.getInt("numero"),
                    StatutRonde.valueOf(rs.getString("statut")), 
                    rs.getInt("idtournoi")
                );
            }
        }
    }
    return null;
}




private void updateStatutRondeTransactionnel(Connection con) throws SQLException {
    String sql = "UPDATE Ronde SET statut = ? WHERE id = ?";
    try (PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setString(1, this.statut.name());
        pst.setInt(2, this.getId());
        pst.executeUpdate();
    }
}

    // Getters et Setters

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public StatutRonde getStatut() {
        return statut;
    }

    public void setStatut(StatutRonde statut) {
        this.statut = statut;
    }

    public int getIdtournoi() {
        return idtournoi;
    }

    public void setIdtournoi(int idtournoi) {
        this.idtournoi = idtournoi;
    }

 
    @Override
    public String toString() {
        return "Ronde{" +
               ", numero=" + numero +
               ", statut=" + statut +
               ", idtournoi=" + idtournoi +
               '}';
    }
}