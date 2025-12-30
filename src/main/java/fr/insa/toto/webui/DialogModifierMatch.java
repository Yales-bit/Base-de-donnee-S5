package fr.insa.toto.webui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import fr.insa.toto.model.Equipe;
import fr.insa.toto.model.Joueur;
import fr.insa.toto.model.Match;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fenêtre pop-up dédiée à la modification (swap) rapide des joueurs.
 * Version optimisée par déplacement de composants graphiques.
 */
public class DialogModifierMatch extends Dialog {

    private Match match;

    // Conteneurs graphiques (les colonnes visuelles)
    private VerticalLayout containerA = new VerticalLayout();
    private VerticalLayout containerB = new VerticalLayout();

    // Listes pour garder une référence Java vers nos widgets (pour savoir qui est où)
    private List<JoueurWidget> widgetsA = new ArrayList<>();
    private List<JoueurWidget> widgetsB = new ArrayList<>();

    private Button btnSauvegarder = new Button("Valider les nouvelles équipes", VaadinIcon.CHECK.create());
    private Span validationMessage = new Span();

    public DialogModifierMatch(Match match) {
        this.match = match;
        setHeaderTitle("Modifier la composition (Swap rapide)");
        setWidth("900px");
        setHeight("650px");

        // 1. Configuration de l'interface principale
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();
        mainLayout.setSpacing(true);
        mainLayout.setAlignItems(FlexComponent.Alignment.START);

        // Configuration des colonnes visuelles
        configureContainer(containerA, match.getEquipe1().getNom());
        configureContainer(containerB, match.getEquipe2().getNom());

        mainLayout.add(containerA, containerB);
        mainLayout.setFlexGrow(1, containerA);
        mainLayout.setFlexGrow(1, containerB);
        add(mainLayout);

        // 2. Chargement des données initiales et création des widgets
        initialiserWidgets();

        // 3. Configuration du pied de page
        configureFooter();

        // 4. Première validation
        updateValidationState();
    }

    private void configureContainer(VerticalLayout container, String nomEquipe) {
        container.setPadding(false);
        container.setSpacing(false); // On gérera l'espacement dans le widget
        container.setHeightFull();
        container.add(new H4("Equipe : " + nomEquipe));
        // Zone défilable pour les joueurs
        VerticalLayout scrollArea = new VerticalLayout();
        scrollArea.setPadding(true);
        scrollArea.setSpacing(true);
        scrollArea.getElement().getStyle().set("overflow-y", "auto");
        scrollArea.getElement().getStyle().set("flex-grow", "1"); // Prend tout l'espace restant
        scrollArea.getElement().getStyle().set("background-color", "var(--lumo-contrast-5pct)");
        scrollArea.getElement().getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        container.add(scrollArea);
        // C'est dans cette zone qu'on ajoutera les widgets, pas directement dans le container principal
        // On triche un peu pour que containerA pointe vers la zone de scroll
        if (container == containerA) containerA = scrollArea;
        else containerB = scrollArea;
    }


    private void initialiserWidgets() {
        try {
            List<Joueur> joueursA = Equipe.getJoueursDeLEquipe(match.getEquipe1().getId());
            List<Joueur> joueursB = Equipe.getJoueursDeLEquipe(match.getEquipe2().getId());

            for (Joueur j : joueursA) {
                JoueurWidget widget = new JoueurWidget(j, true); // true = est à gauche
                widgetsA.add(widget);
                containerA.add(widget);
            }
            for (Joueur j : joueursB) {
                JoueurWidget widget = new JoueurWidget(j, false); // false = est à droite
                widgetsB.add(widget);
                containerB.add(widget);
            }

        } catch (SQLException e) {
            Notification.show("Erreur de chargement : " + e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            close();
        }
    }

    /**
     * MÉTHODE CENTRALE DU SWAP.
     * Déplace le widget d'une liste Java à l'autre ET d'un conteneur graphique à l'autre.
     */
    private void swapWidget(JoueurWidget widget) {
        if (widgetsA.contains(widget)) {
            // Il est dans A, on le passe dans B
            widgetsA.remove(widget);
            containerA.remove(widget); // Vaadin le détache visuellement de gauche

            widget.setDirectionDroite(false); // La flèche pointera vers la gauche
            widgetsB.add(widget);
            containerB.add(widget); // Vaadin l'attache visuellement à droite
        } else {
            // Il est dans B, on le passe dans A
            widgetsB.remove(widget);
            containerB.remove(widget); // Détache de droite

            widget.setDirectionDroite(true); // La flèche pointera vers la droite
            widgetsA.add(widget);
            containerA.add(widget); // Attache à gauche
        }
        // C'est instantané car on ne recharge aucune donnée, on déplace juste des boîtes.
        updateValidationState();
    }


    private void updateValidationState() {
        int countA = widgetsA.size();
        int countB = widgetsB.size();
        boolean equilibre = (countA == countB) && (countA > 0);

        btnSauvegarder.setEnabled(equilibre);

        validationMessage.removeClassName(LumoUtility.TextColor.SUCCESS);
        validationMessage.addClassName(LumoUtility.TextColor.ERROR);

        if (!equilibre) {
            if (countA == 0 && countB == 0) {
                validationMessage.setText("Erreur : Les équipes sont vides.");
            } else {
                validationMessage.setText("Déséquilibre : " + countA + " vs " + countB + ".");
            }
        } else {
            validationMessage.removeClassName(LumoUtility.TextColor.ERROR);
            validationMessage.addClassName(LumoUtility.TextColor.SUCCESS);
            validationMessage.setText("Équipes équilibrées (" + countA + " par équipe).");
        }
    }

    private void configureFooter() {
        btnSauvegarder.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnSauvegarder.addClickListener(e -> sauvegarder());

        validationMessage.getElement().getStyle().set("font-size", "0.9em");
        validationMessage.getElement().getStyle().set("flex-grow", "1");

        HorizontalLayout footerLayout = new HorizontalLayout();
        footerLayout.setWidthFull();
        footerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        footerLayout.add(validationMessage, new Button("Annuler", e -> this.close()), btnSauvegarder);
        getFooter().add(footerLayout);
    }


    private void sauvegarder() {
        try {
            // On reconstruit les listes de joueurs à partir de la position finale des widgets
            List<Joueur> finaleA = widgetsA.stream().map(JoueurWidget::getJoueur).collect(Collectors.toList());
            List<Joueur> finaleB = widgetsB.stream().map(JoueurWidget::getJoueur).collect(Collectors.toList());

            // C'EST SEULEMENT ICI QU'ON TOUCHE À LA BDD
            Match.updateEquipesDuMatch(match, finaleA, finaleB);

            Notification.show("Équipes modifiées avec succès !").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            this.close();
            com.vaadin.flow.component.UI.getCurrent().getPage().reload();

        } catch (SQLException e) {
            Notification.show("Erreur sauvegarde : " + e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }


    // ==========================================================================
    // CLASSE INTERNE : Le "Widget" qui représente un joueur graphiquement
    // ==========================================================================
    private class JoueurWidget extends HorizontalLayout {
        private Joueur joueur;
        private Button moveBtn;
        private Span nameSpan;

        public JoueurWidget(Joueur joueur, boolean estAGaucheInitialement) {
            this.joueur = joueur;
            setWidthFull();
            setAlignItems(Alignment.CENTER);
            setPadding(true);
            setSpacing(true);
            getElement().getStyle().set("background-color", "var(--lumo-base-color)");
            getElement().getStyle().set("border-radius", "var(--lumo-border-radius-s)");
            getElement().getStyle().set("box-shadow", "var(--lumo-box-shadow-xs)");

            nameSpan = new Span(joueur.getPrenom() + " " + joueur.getNom().toUpperCase());
            nameSpan.getElement().getStyle().set("font-weight", "500");
            setFlexGrow(1, nameSpan); // Le nom prend l'espace disponible

            moveBtn = new Button();
            moveBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            // AU CLIC : On appelle la méthode de la classe parente pour se déplacer soi-même
            moveBtn.addClickListener(e -> swapWidget(this));

            setDirectionDroite(estAGaucheInitialement);
        }

        /**
         * Change l'apparence du widget selon qu'il est à gauche ou à droite.
         */
        public void setDirectionDroite(boolean versLaDroite) {
            removeAll();
            Icon icon = (versLaDroite ? VaadinIcon.ARROW_RIGHT : VaadinIcon.ARROW_LEFT).create();
            moveBtn.setIcon(icon);
            
            if (versLaDroite) {
                // Equipe A : Nom à gauche, bouton à droite
                add(nameSpan, moveBtn);
                setJustifyContentMode(JustifyContentMode.BETWEEN);
            } else {
                // Equipe B : Bouton à gauche, nom à droite
                add(moveBtn, nameSpan);
                setJustifyContentMode(JustifyContentMode.START);
            }
        }

        public Joueur getJoueur() { return joueur; }
    }
}