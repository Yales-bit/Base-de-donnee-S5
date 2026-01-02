package fr.insa.toto.webui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
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
import fr.insa.toto.model.Ronde;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fenêtre pop-up pour la modification rapide des équipes avec gestion des exemptés (3 colonnes).
 */
public class DialogModifierMatch extends Dialog {

    private enum Zone { EQUIPE_A, EXEMPT, EQUIPE_B }

    private Match match;
    private int tournoiId;

    // Conteneurs graphiques (les zones défilables)
    private VerticalLayout scrollAreaA;
    private VerticalLayout scrollAreaExempt;
    private VerticalLayout scrollAreaB;

    // --- RÉFÉRENCE QUI VOUS MANQUAIT PEUT-ÊTRE ---
    private H4 titleExempt;

    // Listes Java pour suivre où sont les widgets
    private List<JoueurWidget> widgetsA = new ArrayList<>();
    private List<JoueurWidget> widgetsExempt = new ArrayList<>();
    private List<JoueurWidget> widgetsB = new ArrayList<>();

    private Button btnSauvegarder = new Button("Valider les équipes", VaadinIcon.CHECK.create());
    private Span validationMessage = new Span();

    public DialogModifierMatch(Match match) {
        this.match = match;
        // On suppose que les getters ne sont pas null.
        this.tournoiId = match.getRonde().getIdtournoi();

        setHeaderTitle("Modifier la composition (Swap 3 zones)");
        setWidth("1100px");
        setHeight("700px");

        // 1. Interface principale (3 colonnes)
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();
        mainLayout.setSpacing(true);
        mainLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        // Création des 3 colonnes structurées
        VerticalLayout colA = createColumnStructure("Equipe A : " + match.getEquipe1().getNom(), LumoUtility.Background.CONTRAST_5);
        scrollAreaA = extractScrollArea(colA);

        // Colonne centrale
        VerticalLayout colExempt = createColumnStructure("Exemptés", LumoUtility.Background.CONTRAST_10);
        scrollAreaExempt = extractScrollArea(colExempt);
        // --- INITIALISATION DE LA RÉFÉRENCE ---
        this.titleExempt = (H4) colExempt.getChildren().findFirst().get();

        // Colonne droite
        VerticalLayout colB = createColumnStructure("Equipe B : " + match.getEquipe2().getNom(), LumoUtility.Background.CONTRAST_5);
        scrollAreaB = extractScrollArea(colB);

        mainLayout.add(colA, colExempt, colB);
        // Répartition de l'espace : 40% - 20% - 40%
        mainLayout.setFlexGrow(2, colA);
        mainLayout.setFlexGrow(1, colExempt);
        mainLayout.setFlexGrow(2, colB);

        add(mainLayout);

        // 2. Chargement et répartition des joueurs
        initialiserWidgets();

        // 3. Pied de page
        configureFooter();
        updateValidationState();
    }

    /**
     * Crée la structure visuelle d'une colonne (Titre + Zone de scroll).
     */
    private VerticalLayout createColumnStructure(String title, String bgColorUtility) {
        VerticalLayout col = new VerticalLayout();
        col.setPadding(false);
        col.setSpacing(false);
        col.setHeightFull();
        // Le premier enfant est le titre H4
        col.add(new H4(title));

        VerticalLayout scrollArea = new VerticalLayout();
        scrollArea.setPadding(true);
        scrollArea.setSpacing(true);
        scrollArea.getElement().getStyle().set("overflow-y", "auto");
        scrollArea.addClassName(bgColorUtility);
        scrollArea.getElement().getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        // Le deuxième enfant est la zone de scroll
        col.add(scrollArea);
        col.setFlexGrow(1, scrollArea);
        return col;
    }

    /**
     * Helper pour récupérer proprement la zone de scroll d'une colonne structurée.
     */
    private VerticalLayout extractScrollArea(VerticalLayout structureCol) {
        // On suppose que la zone de scroll est le 2ème élément (index 1)
        return (VerticalLayout) structureCol.getChildren().skip(1).findFirst().orElse(null);
    }


private void initialiserWidgets() {
        try {
            // 1. On charge les joueurs du match courant pour l'affichage des colonnes A et B
            List<Joueur> joueursA = Equipe.getJoueursDeLEquipe(match.getEquipe1().getId());
            List<Joueur> joueursB = Equipe.getJoueursDeLEquipe(match.getEquipe2().getId());

            // 2. On charge TOUS les joueurs inscrits au tournoi (la base de référence)
            List<Joueur> tousInscrits = Joueur.getJoueursInscritsComplets(this.tournoiId);

            // 3. --- LE POINT CRUCIAL CORRIGÉ ---
            // On récupère les IDs de TOUS ceux qui jouent dans cette ronde (dans ce match OU dans les autres matchs simultanés).
            int idRondeActuelle = match.getRonde().getId();
            // Appel à la nouvelle méthode backend créée à l'étape 1
            List<Integer> idsParticipantsRonde = Ronde.getIdsJoueursParticipants(idRondeActuelle);

            // 4. Filtrage pour trouver les VRAIS exemptés
            List<Joueur> joueursExempts = new ArrayList<>();
            for (Joueur j : tousInscrits) {
                // Un joueur est exempt s'il est inscrit au tournoi MAIS que son ID
                // n'est pas dans la liste des participants officiels de la ronde.
                if (!idsParticipantsRonde.contains(j.getId())) {
                    joueursExempts.add(j);
                }
            }

            // 5. Remplissage des colonnes graphiques
            for (Joueur j : joueursA) addWidgetToZone(new JoueurWidget(j), Zone.EQUIPE_A);
            for (Joueur j : joueursB) addWidgetToZone(new JoueurWidget(j), Zone.EQUIPE_B);
            for (Joueur j : joueursExempts) addWidgetToZone(new JoueurWidget(j), Zone.EXEMPT);

            updateExemptTitle();

        } catch (SQLException e) {
            Notification.show("Erreur de chargement : " + e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            // e.printStackTrace(); // Utile pour le debug
            close();
        }
    }

    // --- LOGIQUE CENTRALISÉE DU DÉPLACEMENT ---

    private void moveWidget(JoueurWidget widget, Zone destination) {
        Zone source = widget.getCurrentZone();

        // 1. Retrait de la source
        switch (source) {
            case EQUIPE_A -> { widgetsA.remove(widget); scrollAreaA.remove(widget); }
            case EQUIPE_B -> { widgetsB.remove(widget); scrollAreaB.remove(widget); }
            case EXEMPT ->   { widgetsExempt.remove(widget); scrollAreaExempt.remove(widget); }
        }

        // 2. Ajout à la destination
        addWidgetToZone(widget, destination);

        // 3. Mise à jour des états (validation + titre central)
        updateValidationState();
        updateExemptTitle();
    }

    private void addWidgetToZone(JoueurWidget widget, Zone zone) {
        switch (zone) {
            case EQUIPE_A -> { widgetsA.add(widget); scrollAreaA.add(widget); }
            case EQUIPE_B -> { widgetsB.add(widget); scrollAreaB.add(widget); }
            case EXEMPT ->   { widgetsExempt.add(widget); scrollAreaExempt.add(widget); }
        }
        widget.updateLayoutForZone(zone);
    }


   private void updateExemptTitle() {
        // Si la référence graphique n'existe pas, on ne fait rien
        if (titleExempt == null) return;
        int count = widgetsExempt.size();
        titleExempt.setText("Exemptés (" + count + ")");
    }

    private void updateValidationState() {
        int countA = widgetsA.size();
        int countB = widgetsB.size();
        boolean equilibre = (countA == countB) && (countA > 0);

        btnSauvegarder.setEnabled(equilibre);

        validationMessage.removeClassName(LumoUtility.TextColor.SUCCESS);
        validationMessage.addClassName(LumoUtility.TextColor.ERROR);

        if (!equilibre) {
            validationMessage.setText("Déséquilibre : A(" + countA + ") vs B(" + countB + ").");
        } else {
            validationMessage.removeClassName(LumoUtility.TextColor.ERROR);
            validationMessage.addClassName(LumoUtility.TextColor.SUCCESS);
            validationMessage.setText("Équipes équilibrées (" + countA + " vs " + countB + "). Prêt à valider.");
        }
    }

    private void configureFooter() {
        btnSauvegarder.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnSauvegarder.addClickListener(e -> sauvegarder());

        validationMessage.getElement().getStyle().set("flex-grow", "1");
        HorizontalLayout footerLayout = new HorizontalLayout(validationMessage, new Button("Annuler", e -> this.close()), btnSauvegarder);
        footerLayout.setWidthFull();
        footerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        getFooter().add(footerLayout);
    }

    private void sauvegarder() {
        try {
            List<Joueur> finaleA = widgetsA.stream().map(JoueurWidget::getJoueur).collect(Collectors.toList());
            List<Joueur> finaleB = widgetsB.stream().map(JoueurWidget::getJoueur).collect(Collectors.toList());

            // C'est ici que la magie opère : mise à jour des équipes ET des participants à la ronde
            Match.updateEquipesDuMatch(match, finaleA, finaleB);

            Notification.show("Équipes et participants à la ronde modifiés avec succès !").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            this.close();
            com.vaadin.flow.component.UI.getCurrent().getPage().reload();
        } catch (SQLException e) {
            Notification.show("Erreur sauvegarde critique : " + e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }


    // ==========================================================================
    // CLASSE INTERNE : Le Widget Joueur Intelligent
    // ==========================================================================
    private class JoueurWidget extends HorizontalLayout {
        private Joueur joueur;
        private Zone currentZone;
        private Span nameSpan;


        public JoueurWidget(Joueur joueur) {
            this.joueur = joueur;
            setWidthFull();
            setAlignItems(Alignment.CENTER);
            setPadding(true);
            setSpacing(true);
            // Style "carte"
            getElement().getStyle().set("background-color", "var(--lumo-base-color)")
                         .set("border-radius", "var(--lumo-border-radius-s)")
                         .set("box-shadow", "var(--lumo-box-shadow-xs)");

            nameSpan = new Span(joueur.getPrenom() + " " + joueur.getNom().toUpperCase());
            nameSpan.getElement().getStyle().set("font-weight", "500").set("font-size", "0.9em");
            setFlexGrow(1, nameSpan); // Le nom prend l'espace dispo par défaut
        }

        public void updateLayoutForZone(Zone zone) {
            this.currentZone = zone;
            removeAll(); // On vide le layout visuellement

            // On prépare des boutons tout neufs
            Button btnGoLeft = new Button(VaadinIcon.ARROW_LEFT.create());
            btnGoLeft.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);

            Button btnGoRight = new Button(VaadinIcon.ARROW_RIGHT.create());
            btnGoRight.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);

            switch (zone) {
                case EQUIPE_A -> {
                    // Zone Gauche : Nom + Bouton Droite (vers Exempt)
                    setJustifyContentMode(JustifyContentMode.BETWEEN);
                    btnGoRight.setTooltipText("Mettre en exempté");
                    btnGoRight.addClickListener(e -> moveWidget(this, Zone.EXEMPT));
                    add(nameSpan, btnGoRight);
                }
                case EQUIPE_B -> {
                    // Zone Droite : Bouton Gauche (vers Exempt) + Nom
                    setJustifyContentMode(JustifyContentMode.START);
                    btnGoLeft.setTooltipText("Mettre en exempté");
                    btnGoLeft.addClickListener(e -> moveWidget(this, Zone.EXEMPT));
                    add(btnGoLeft, nameSpan);
                }
                case EXEMPT -> {
                    // Zone Milieu : Bouton Gauche (vers A) + Nom + Bouton Droite (vers B)
                    setJustifyContentMode(JustifyContentMode.BETWEEN);

                    btnGoLeft.setTooltipText("Envoyer dans l'équipe A");
                    btnGoLeft.addClickListener(e -> moveWidget(this, Zone.EQUIPE_A));

                    btnGoRight.setTooltipText("Envoyer dans l'équipe B");
                    btnGoRight.addClickListener(e -> moveWidget(this, Zone.EQUIPE_B));

                    add(btnGoLeft, nameSpan, btnGoRight);
                }
            }
        }

        public Joueur getJoueur() { return joueur; }
        public Zone getCurrentZone() { return currentZone; }
    }
}