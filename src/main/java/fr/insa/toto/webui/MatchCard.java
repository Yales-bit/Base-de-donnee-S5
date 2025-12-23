package fr.insa.toto.webui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import fr.insa.toto.model.Match;
import fr.insa.toto.model.StatutMatch;
import fr.insa.toto.webui.Session.Sessioninfo;

/**
 * Composant graphique représentant un seul match.
 * Gère l'affichage du score OU la saisie du résultat selon le rôle de l'utilisateur.
 */
public class MatchCard extends VerticalLayout {

    private Match match;

    public MatchCard(Match m) {
        this.match = m;
        // Configuration de base de la "carte"
        setSpacing(false);
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        // Style CSS pour faire une jolie boîte
        getElement().getStyle().set("border", "1px solid var(--lumo-contrast-20pct)");
        getElement().getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        getElement().getStyle().set("background-color", "var(--lumo-base-color)");
        getElement().getStyle().set("box-shadow", "var(--lumo-box-shadow-xs)");
        // Largeur adaptative
        setWidth("100%");
        setMaxWidth("500px"); // Pas trop large sur les grands écrans
        getElement().getStyle().set("margin", "10px auto"); // Centré horizontalement avec un peu d'espace

        // 1. Le Titre : Qui contre Qui ?
        // On sécurise au cas où une équipe serait null (ne devrait pas arriver en théorie)
        String nomEquipe1 = (match.getEquipe1() != null) ? match.getEquipe1().getNom() : "Equipe 1";
        String nomEquipe2 = (match.getEquipe2() != null) ? match.getEquipe2().getNom() : "Equipe 2";
        H5 titreMatch = new H5(nomEquipe1 + " vs " + nomEquipe2);
        titreMatch.getElement().getStyle().set("margin-top", "0");
        titreMatch.getElement().getStyle().set("margin-bottom", "10px");
        add(titreMatch);

        // 2. LA LOGIQUE CONDITIONNELLE
        boolean isMatchTermine = match.getStatut() == StatutMatch.TERMINE;
        boolean isAdminEtMatchALancer = Sessioninfo.adminConnected() && !isMatchTermine;

        if (isAdminEtMatchALancer) {
            creerInterfaceSaisieAdmin();
        } else {
            creerInterfaceLectureSeule(isMatchTermine);
        }
    }

    private void creerInterfaceLectureSeule(boolean isTermine) {
        String texteScore;
        String couleur;
        if (isTermine) {
             // Si le match est fini, on récupère les scores DANS LES ÉQUIPES
             int s1 = (match.getEquipe1() != null) ? match.getEquipe1().getScore() : 0;
             int s2 = (match.getEquipe2() != null) ? match.getEquipe2().getScore() : 0;
             texteScore = s1 + " - " + s2;
             couleur = "var(--lumo-primary-text-color)"; // Couleur normale
        }
        else {
            // Si le match n'est pas fini
            if (match.getStatut() == StatutMatch.EN_COURS) {
                texteScore = "En cours";
        } else {
        texteScore = "À jouer";
    }
    couleur = "var(--lumo-tertiary-text-color)";
}

        Span scoreSpan = new Span(texteScore);
        scoreSpan.getElement().getStyle().set("font-size", "1.5em");
        scoreSpan.getElement().getStyle().set("font-weight", "bold");
        scoreSpan.getElement().getStyle().set("color", couleur);

        add(scoreSpan);
    }
    private void creerInterfaceSaisieAdmin() {
        IntegerField score1Field = new IntegerField();
        score1Field.setPlaceholder("Score Eq. 1");
        score1Field.setWidth("100px");
        score1Field.setMin(0); // Pas de score négatif

        Span separateur = new Span("-");
        separateur.getElement().getStyle().set("font-weight", "bold");
        separateur.getElement().getStyle().set("font-size", "1.2em");

        IntegerField score2Field = new IntegerField();
        score2Field.setPlaceholder("Score Eq. 2");
        score2Field.setWidth("100px");
        score2Field.setMin(0);

        Button btnValider = new Button(VaadinIcon.CHECK.create());
        btnValider.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        btnValider.setTooltipText("Valider le résultat");

        // --- Action du bouton Valider ---
        btnValider.addClickListener(e -> {
            Integer s1 = score1Field.getValue();
            Integer s2 = score2Field.getValue();

            if (s1 == null || s2 == null) {
                Notification.show("Veuillez entrer les deux scores pour valider.")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                // APPEL AU BACKEND (Méthode statique de Match)
                // On passe l'ID du match actuel et les scores saisis
                Match.validerResultatMatch(this.match.getId(), s1, s2);

                Notification.show("Résultat validé ! Points distribués.")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                // RECHARGER LA PAGE COMPLÈTE
                // C'est crucial pour voir le match passer en "Terminé" et voir les boutons de fin de ronde.
                UI.getCurrent().getPage().reload();

            } catch (Exception ex) {
                 Notification.show("Erreur lors de la validation : " + ex.getMessage(), 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                 ex.printStackTrace(); // Utile pour le débogage
            }
        });

        HorizontalLayout saisieLayout = new HorizontalLayout(score1Field, separateur, score2Field, btnValider);
        saisieLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        // On ajoute un petit titre "Saisir le score" pour l'admin
        Span titreSaisie = new Span("Saisir le résultat :");
        titreSaisie.getElement().getStyle().set("font-size", "0.9em");
        titreSaisie.getElement().getStyle().set("color", "var(--lumo-secondary-text-color)");

        add(titreSaisie, saisieLayout);
    }
}