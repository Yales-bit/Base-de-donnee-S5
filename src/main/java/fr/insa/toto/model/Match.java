package fr.insa.toto.model;

public class Match {
    public Match() {
        // Constructeur par défaut
    }
    private Long id;
    private Ronde ronde;
    private Equipe equipe1;
    private Equipe equipe2;
    private Terrain terrain;
    private StatutMatch statut;

    public Match(Ronde ronde, Equipe equipe1, Equipe equipe2, Terrain terrain) {
        this.ronde = ronde;
        this.equipe1 = equipe1;
        this.equipe2 = equipe2;
        this.terrain = terrain;
        this.statut = StatutMatch.EN_ATTENTE; // Par défaut, un nouveau match est "en cours"
    }
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
    public boolean estTermine() {
        return this.statut == StatutMatch.TERMINE;
    }
}
