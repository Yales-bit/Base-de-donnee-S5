package fr.insa.toto.model;

import java.util.ArrayList;
import java.util.List;

public class Ronde {

    private Long id; // Identifiant unique de la ronde, sera potentiellement confondu avec le numéro mais jsp encore
    private int numero; // Numéro de la ronde dans le tournoi (ex: 1, 2, 3)
    private StatutRonde statut; // Statut de la ronde (voir la classe StatutRonde)
    private Tournoi tournoi; // La ronde appartient à un tournoi
    private List<Match> matchs; // 

    public Ronde() {
        this.matchs = new ArrayList<>();
        this.statut = StatutRonde.EN_ATTENTE; // Statut par défaut
    }

    public Ronde(int numero, Tournoi tournoi) {
        this.numero = numero;
        this.tournoi = tournoi;
        this.matchs = new ArrayList<>();
        this.statut = StatutRonde.EN_ATTENTE;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Tournoi getTournoi() {
        return tournoi;
    }

    public void setTournoi(Tournoi tournoi) {
        this.tournoi = tournoi;
    }

    public List<Match> getMatchs() {
        return matchs;
    }

    public void setMatchs(List<Match> matchs) {
        this.matchs = matchs;
    }

    // Méthode utilitaire pour ajouter un match
    public void addMatch(Match match) {
        if (this.matchs == null) {
            this.matchs = new ArrayList<>();
        }
        this.matchs.add(match);
        // Assurez-vous que le match référence bien cette ronde
        if (match.getRonde() != this) {
            match.setRonde(this);
        }
    }

    @Override
    public String toString() {
        return "Ronde{" +
               "id=" + id +
               ", numero=" + numero +
               ", statut=" + statut +
               ", tournoiId=" + (tournoi != null ? tournoi.getId() : "null") +
               ", nbMatchs=" + (matchs != null ? matchs.size() : 0) +
               '}';
    }
}