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
        this.statut = StatutRonde.EN_ATTENTE;
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
            PreparedStatement pst = con.prepareStatement("SELECT + FROM Ronde WHERE idtournoi = ? AND numero = ?");
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
        String sql = "SELECT * FROM Ronde WHERE id = ?";
        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
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
        return null;
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

    public void updateStatutRonde (StatutRonde nouveauStatut) throws SQLException {
      this.statut = nouveauStatut;
      try (Connection con = ConnectionPool.getConnection()) {
        PreparedStatement pst = con.prepareStatement("UPDATE Ronde SET statut = ? WHERE id = ?");
        pst.setString(1, nouveauStatut.toString());
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
        pst.setString(2, StatutMatch.TERMINE.toString());
        
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
    
    // Si c'est la ronde 1, il n'y a pas de ronde précédente, donc pas de prioritaires.
    if (numeroRondeActuelle <= 1) {
        return prioritaires;
    }
    
    int numeroRondePrecedente = numeroRondeActuelle - 1;
    String sql ="SELECT j.* FROM Joueur j " +
                "JOIN INSCRIPTION i ON i.idjoueur = j.id " +
                "LEFT JOIN ("+
                "SELECT pr.idjoueur FROM ParticipationRonde pr " +
                "JOIN Ronde r ON r.id = pr.idronde"+
                "WHERE r.idtournoi = ? AND r.numero = ?"+
                ") AS participants_prec ON j.id = participants_prec.idjoueur " +
                 "WHERE i.idtournoi = ? AND participants_prec.idjoueur IS NULL"; // selectionne les participants de la ronde precedente
    try (Connection con = ConnectionPool.getConnection();
        PreparedStatement pst = con.prepareStatement(sql)){
            pst.setInt(1, idTournoi);
            pst.setInt(2, numeroRondeActuelle);
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
public static List<Ronde> getRondesDuTournoi(int idTournoi) throws SQLException {
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