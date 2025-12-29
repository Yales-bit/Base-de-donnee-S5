package fr.insa.toto.webui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.theme.lumo.LumoUtility;
import fr.insa.toto.model.Equipe;
import fr.insa.toto.model.Joueur;
import fr.insa.toto.model.Match;
import fr.insa.toto.model.StatutMatch;
import fr.insa.toto.webui.Session.Sessioninfo;

import java.sql.SQLException;
import java.util.List;

public class MatchCard extends VerticalLayout {

    private Match match;
    private IntegerField score1Field;
    private IntegerField score2Field;

    public MatchCard(Match m) {
        this.match = m;
        // --- Styles de la carte ---
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
        
        // --- MODIF : Rendre la carte cliquable ---
        getElement().getStyle().set("cursor", "pointer");
        // Ajout d'un effet visuel au survol de la carte entière
        getElement().executeJs("this.onmouseover = function() { this.style.backgroundColor = 'var(--lumo-contrast-5pct)'; }");
        getElement().executeJs("this.onmouseout = function() { this.style.backgroundColor = 'var(--lumo-base-color)'; }");
        
        addClickListener(e -> afficherDetailsMatch());
        // -----------------------------------------

        // --- Titre ---
        String nomEquipe1 = (match.getEquipe1() != null) ? match.getEquipe1().getNom() : "Equipe 1";
        String nomEquipe2 = (match.getEquipe2() != null) ? match.getEquipe2().getNom() : "Equipe 2";
        H5 titreMatch = new H5(nomEquipe1 + " vs " + nomEquipe2);
        titreMatch.getElement().getStyle().set("margin-top", "0");
        titreMatch.getElement().getStyle().set("margin-bottom", "10px");
        add(titreMatch);

        // --- Logique d'affichage des scores ---
        boolean isMatchTermine = match.getStatut() == StatutMatch.TERMINE;
        if (isMatchTermine || !Sessioninfo.adminConnected()) {
            creerInterfaceLectureSeule(isMatchTermine);
        } else {
            creerInterfaceSaisieAdmin();
        }
    }

    /**
     * Ouvre une boîte de dialogue avec la composition des équipes.
     */
    private void afficherDetailsMatch() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Détails du match");
        dialog.setWidth("600px");
        

        HorizontalLayout compositionsLayout = new HorizontalLayout();
        compositionsLayout.setWidthFull();
        compositionsLayout.setSpacing(true);

        // Colonne Equipe 1
        VerticalLayout equipe1Layout = createEquipeDetailsLayout(match.getEquipe1(), dialog);
        // Colonne Equipe 2
        VerticalLayout equipe2Layout = createEquipeDetailsLayout(match.getEquipe2(), dialog);

        compositionsLayout.add(equipe1Layout, equipe2Layout);
        // Ajout d'une ligne de séparation verticale entre les deux équipes
        equipe1Layout.getElement().getStyle().set("border-right", "1px solid var(--lumo-contrast-20pct)");

        dialog.add(compositionsLayout);
        Button closeButton = new Button("Fermer", e -> dialog.close());
        dialog.getFooter().add(closeButton);

        if (Sessioninfo.adminConnected() && match.getStatut() != StatutMatch.TERMINE) {
        Button btnModifier = new Button("Modifier les équipes", VaadinIcon.EDIT.create());
        btnModifier.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnModifier.getElement().getStyle().set("margin-left", "auto"); 

        btnModifier.addClickListener(e -> {
            dialog.close(); //
            DialogModifierMatch dialogModif = new DialogModifierMatch(this.match);
            dialogModif.open();
        });
        dialog.getFooter().add(btnModifier);
    }
        //dialog.getFooter().add(new com.vaadin.flow.component.button.Button("Fermer", e -> dialog.close()));
        dialog.open();
    }

    private VerticalLayout createEquipeDetailsLayout(Equipe equipe, Dialog parentDialog) {
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.setPadding(false);

        H3 nomEquipe = new H3(equipe.getNom());
        nomEquipe.addClassName(LumoUtility.TextColor.PRIMARY);
        layout.add(nomEquipe);

        try {
            List<Joueur> joueurs = Equipe.getJoueursDeLEquipe(equipe.getId());
            
            if (joueurs.isEmpty()) {
                 layout.add(new Span("(Aucun joueur)"));
            } else {
                for (Joueur j : joueurs) {
                    // Création du lien vers le profil du joueur
                    Span lienJoueur = new Span(j.getSurnom());
                    lienJoueur.addClassName(LumoUtility.FontWeight.MEDIUM);
                    lienJoueur.getElement().getStyle().set("cursor", "pointer");
                    lienJoueur.getElement().getStyle().set("color", "var(--lumo-primary-text-color)");
                    
                    // Effet hover 
                    lienJoueur.getElement().executeJs("this.onmouseover = function() { this.style.color = 'var(--lumo-primary-color)'; }");
                    lienJoueur.getElement().executeJs("this.onmouseout = function() { this.style.color = 'var(--lumo-primary-text-color)'; }");

                    
                    
                    lienJoueur.addClickListener(e -> {
                        parentDialog.close();
                        UI.getCurrent().navigate("profil/" + j.getId());
                    });

                    layout.add(lienJoueur);
                }
            }
        } catch (SQLException e) {
            layout.add(new Span("Erreur chargement joueurs"));
            Notification.show("Erreur DB : " + e.getMessage());
        }
        return layout;
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

   
    // on utilise du JavaScript
    // sur l'élément HTML pour stopper la propagation du clic.
    score1Field.getElement().executeJs("this.addEventListener('click', function(e) { e.stopPropagation(); });");

    if(match.getEquipe1().getScore() > 0) score1Field.setValue(match.getEquipe1().getScore());

    Span separateur = new Span("-");
    separateur.getElement().getStyle().set("font-weight", "bold");
    separateur.getElement().getStyle().set("font-size", "1.2em");

    score2Field = new IntegerField();
    score2Field.setPlaceholder("Score 2");
    score2Field.setWidth("90px");
    score2Field.setMin(0);

    
    score2Field.getElement().executeJs("this.addEventListener('click', function(e) { e.stopPropagation(); });");

    if(match.getEquipe2().getScore() > 0) score2Field.setValue(match.getEquipe2().getScore());

    HorizontalLayout saisieLayout = new HorizontalLayout(score1Field, separateur, score2Field);
    saisieLayout.setAlignItems(FlexComponent.Alignment.CENTER);
    add(saisieLayout);
}


    public Match getMatch() { return match; }
    
    public int getScore1Saisi() {
        return (score1Field != null && score1Field.getValue() != null) ? score1Field.getValue() : 0;
    }
    public int getScore2Saisi() {
        return (score2Field != null && score2Field.getValue() != null) ? score2Field.getValue() : 0;
    }
    
    public boolean scoresSontSaisis() {
        return score1Field != null && score1Field.getValue() != null && score2Field != null && score2Field.getValue() != null;
    }
}