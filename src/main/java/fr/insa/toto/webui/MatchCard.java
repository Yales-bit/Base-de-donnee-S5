package fr.insa.toto.webui;

import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import fr.insa.toto.model.Match;
import fr.insa.toto.model.StatutMatch;
import fr.insa.toto.webui.Session.Sessioninfo;

public class MatchCard extends VerticalLayout {

    private Match match;
    // On garde les champs en attributs de classe pour y accéder via des getters
    private IntegerField score1Field;
    private IntegerField score2Field;

    public MatchCard(Match m) {
        this.match = m;
        // --- Styles (inchangés) ---
        setSpacing(false);
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        getElement().getStyle().set("border", "1px solid var(--lumo-contrast-20pct)");
        getElement().getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        getElement().getStyle().set("background-color", "var(--lumo-base-color)");
        getElement().getStyle().set("box-shadow", "var(--lumo-box-shadow-xs)");
        setWidth("100%");
        setMaxWidth("500px");
        getElement().getStyle().set("margin", "10px auto");

        // --- Titre ---
        String nomEquipe1 = (match.getEquipe1() != null) ? match.getEquipe1().getNom() : "Equipe 1";
        String nomEquipe2 = (match.getEquipe2() != null) ? match.getEquipe2().getNom() : "Equipe 2";
        H5 titreMatch = new H5(nomEquipe1 + " vs " + nomEquipe2);
        titreMatch.getElement().getStyle().set("margin-top", "0");
        titreMatch.getElement().getStyle().set("margin-bottom", "10px");
        add(titreMatch);

        // --- Logique d'affichage ---
        // Si le match est terminé OU si on n'est pas admin -> Lecture seule
        boolean isMatchTermine = match.getStatut() == StatutMatch.TERMINE;
        if (isMatchTermine || !Sessioninfo.adminConnected()) {
            creerInterfaceLectureSeule(isMatchTermine);
        } else {
            // Admin et match en cours/attente -> Champs de saisie
            creerInterfaceSaisieAdmin();
        }
    }

    private void creerInterfaceLectureSeule(boolean isTermine) {
        String texteScore;
        String couleur;
        if (isTermine) {
             int s1 = (match.getEquipe1() != null) ? match.getEquipe1().getScore() : 0;
             int s2 = (match.getEquipe2() != null) ? match.getEquipe2().getScore() : 0;
             texteScore = s1 + " - " + s2;
             couleur = "var(--lumo-primary-text-color)";
        } else {
             texteScore = (match.getStatut() == StatutMatch.EN_COURS) ? "En cours" : "À jouer";
             couleur = "var(--lumo-tertiary-text-color)";
        }

        Span scoreSpan = new Span(texteScore);
        scoreSpan.getElement().getStyle().set("font-size", "1.5em");
        scoreSpan.getElement().getStyle().set("font-weight", "bold");
        scoreSpan.getElement().getStyle().set("color", couleur);
        add(scoreSpan);
    }

    private void creerInterfaceSaisieAdmin() {
        score1Field = new IntegerField();
        score1Field.setPlaceholder("Score 1");
        score1Field.setWidth("90px");
        score1Field.setMin(0);
        // On pré-remplit avec la valeur existante en BDD si elle n'est pas 0
        if(match.getEquipe1().getScore() > 0) score1Field.setValue(match.getEquipe1().getScore());

        Span separateur = new Span("-");
        separateur.getElement().getStyle().set("font-weight", "bold");
        separateur.getElement().getStyle().set("font-size", "1.2em");

        score2Field = new IntegerField();
        score2Field.setPlaceholder("Score 2");
        score2Field.setWidth("90px");
        score2Field.setMin(0);
        if(match.getEquipe2().getScore() > 0) score2Field.setValue(match.getEquipe2().getScore());

        // Plus de bouton Valider ici !

        HorizontalLayout saisieLayout = new HorizontalLayout(score1Field, separateur, score2Field);
        saisieLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        add(saisieLayout);
    }

    // --- Getters pour permettre à la vue parente de récupérer les données ---
    public Match getMatch() { return match; }
    
    // Retourne 0 si le champ est vide ou null, pour éviter les erreurs
    public int getScore1Saisi() {
        return (score1Field != null && score1Field.getValue() != null) ? score1Field.getValue() : 0;
    }
    public int getScore2Saisi() {
        return (score2Field != null && score2Field.getValue() != null) ? score2Field.getValue() : 0;
    }
    
    // Utile pour savoir si tous les scores ont été saisis
    public boolean scoresSontSaisis() {
        return score1Field != null && score1Field.getValue() != null && score2Field != null && score2Field.getValue() != null;
    }
}