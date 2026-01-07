package fr.insa.toto.webui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import fr.insa.toto.model.*;
import fr.insa.toto.model.dto.LigneClassement;
import fr.insa.toto.webui.Session.Sessioninfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "tournoi", layout = InterfacePrincipale.class)
@PageTitle("Détails du Tournoi")
public class VueDetailsTournoi extends VerticalLayout implements HasUrlParameter<Integer> {

    private H2 titreTournoi = new H2();
    private Span statusBadge = new Span();
    private Button btnInscrire = new Button("S'inscrire au tournoi", VaadinIcon.USER_CHECK.create());
    private Button btnDemarrer = new Button("Démarrer le tournoi ", VaadinIcon.PLAY.create());
    private Tournoi tournoiActuel;
    private Span infoRondes = new Span();
    private Span infoTerrains = new Span();
    private Span infoEquipes = new Span();
    private Span infoDuree = new Span();
    private H4 titreParticipants = new H4("Participants inscrits");
    private Accordion accordionRondes = new Accordion();
    private Button btnRetour = new Button("Retour liste", VaadinIcon.ARROW_LEFT.create());
    private Grid<LigneClassement> gridClassement = new Grid<>();
    private VerticalLayout contenuOngletClassement = new VerticalLayout();
    private Grid<Joueur> gridParticipants = new Grid<>(Joueur.class);
    //private List<LigneClassement> classementCompletCache = new ArrayList<>();
  //  private com.vaadin.flow.component.html.Input sliderTopN = new com.vaadin.flow.component.html.Input();
    //private Span sliderLabelValue = new Span(); 
    private Button btnVoirPodium = new Button("VOIR LE PODIUM FINAL 🏆");

    public VueDetailsTournoi() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        btnRetour.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(VueListeTournois.class)));

        FlexLayout infosLayout = new FlexLayout();
        infosLayout.setAlignItems(Alignment.CENTER);
        infosLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        infosLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        infosLayout.setWidthFull();

        infosLayout.add(
                createStatCard(VaadinIcon.LIST_OL, "Rondes", infoRondes),
                createStatCard(VaadinIcon.GRID_BIG, "Terrains", infoTerrains),
                createStatCard(VaadinIcon.USERS, "Par équipe", infoEquipes),
                createStatCard(VaadinIcon.TIMER, "Durée match", infoDuree)
        );

        add(btnRetour, titreTournoi, statusBadge, infosLayout);
        gridParticipants.setColumns("surnom", "sexe", "taille");
        gridParticipants.setWidthFull();
        gridParticipants.setMaxWidth("800px");
        gridParticipants.getElement().getStyle().set("margin-left", "auto");
        gridParticipants.getElement().getStyle().set("margin-right", "auto");

        HorizontalLayout headerLayout = new HorizontalLayout(btnRetour, titreTournoi, statusBadge);
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.setWidthFull();
        add(headerLayout);


        VerticalLayout contenuOngletInfos = new VerticalLayout();
        contenuOngletInfos.setPadding(false);
        contenuOngletInfos.setAlignItems(Alignment.CENTER);
        btnInscrire.setVisible(false);
        btnInscrire.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        btnDemarrer.setVisible(false);
        btnDemarrer.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_LARGE);
        HorizontalLayout actionsLayout = new HorizontalLayout(btnInscrire, btnDemarrer);
        actionsLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        contenuOngletInfos.add(actionsLayout);
        contenuOngletInfos.add(new H4("Paramètres du tournoi"));
        contenuOngletInfos.add(infosLayout);
        contenuOngletInfos.add(this.titreParticipants);
        contenuOngletInfos.add(gridParticipants);


        VerticalLayout contenuOngletMatchs = new VerticalLayout();
        contenuOngletMatchs.setPadding(false);
        this.accordionRondes.setWidthFull();
        contenuOngletMatchs.add(this.accordionRondes);


        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.add("Informations", contenuOngletInfos);
        tabSheet.add("Matchs & Résultats", contenuOngletMatchs);
        add(tabSheet);
        setFlexGrow(1, tabSheet);

        btnDemarrer.addClickListener(e -> {
            if (tournoiActuel == null) return;
            try {
                tournoiActuel.lancerTournoi();
                Notification.show("Le tournoi est démarré ! Les matchs sont générés.")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                UI.getCurrent().getPage().reload();
            } catch (Exception ex) {
                Notification.show("Erreur lors du lancement: " + ex.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
            }
        });
        configureGridClassement(); // On appelle la méthode de config (à créer plus bas)
        tabSheet.add("Classement Général", contenuOngletClassement);
    }

    @Override
    public void setParameter(BeforeEvent event, Integer tournoiId) {
        try {
            Tournoi t = Tournoi.getTournoiById(tournoiId);
            if (t != null) {
                afficherTournoi(t);
            } else {
                titreTournoi.setText("Tournoi introuvable");
            }
        } catch (SQLException e) {
            titreTournoi.setText("Erreur : " + e.getMessage());
        }
    }

    private void afficherTournoi(Tournoi t) {
        this.tournoiActuel = t;
        titreTournoi.setText(t.getNom().toUpperCase());
        titreTournoi.getStyle().set("font-weight", "800");
        titreTournoi.getStyle().set("color", "var(--lumo-primary-text-color)");
        titreTournoi.getStyle().set("font-size", "3em");

        String statut = "En préparation";
        String couleur = "grey";
        if (t.isFini()) {
            statut = "Terminé";
            couleur = "grey";
        } else if (t.isOuvert()) {
            statut = "En préparation";
            couleur = "green";
        } else if (!t.isOuvert()) {
            statut = "En cours";
            couleur = "blue";
        }

        statusBadge.setText(statut);
        statusBadge.getElement().getStyle().set("background-color", couleur);
        statusBadge.getElement().getStyle().set("color", "white");
        statusBadge.getElement().getStyle().set("padding", "5px 10px");
        statusBadge.getElement().getStyle().set("border-radius", "10px");

        btnInscrire.setVisible(false);
        btnDemarrer.setVisible(false);
        if (t.isOuvert()) {
            btnInscrire.setVisible(true);
        }

        if (Sessioninfo.adminConnected()) {
            if (!t.isFini() && t.isOuvert()) {
                btnDemarrer.setVisible(true);
            }
        }

        infoRondes.setText("" + t.getNbrRondes());
        infoTerrains.setText("" + t.getNbrTerrains());
        infoEquipes.setText("" + t.getNbrJoueursParEquipe());
        infoDuree.setText(t.getDureeMatch() + " min");
        try {
            List<Joueur> inscrits = t.getJoueursInscrits();
            gridParticipants.setItems(inscrits);
            int count = inscrits.size();
            String label = (count <= 1) ? "Participant inscrit" : "Participants inscrits";
            this.titreParticipants.setText(label + " (" + count + ")");
        } catch (SQLException e) {
            Notification.show("Erreur chargement participants : " + e.getMessage());
        }


        btnInscrire.addClickListener(e -> {
            if (tournoiActuel != null) {
                showInscriptionDialog();
            }
        });
        actualiserAffichageRondes(t);
        actualiserClassement();

        btnVoirPodium.setVisible(false); // Caché par défaut
        btnVoirPodium.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        // Une couleur dorée/jaune pour le côté festif
        btnVoirPodium.getElement().getStyle().set("background-color", "#FFD700"); 
        btnVoirPodium.getElement().getStyle().set("color", "black");
        btnVoirPodium.getElement().getStyle().set("font-weight", "bold");
        btnVoirPodium.getElement().getStyle().set("margin-top", "20px");
        if (t.isFini()) {
            btnVoirPodium.setVisible(true);
            btnVoirPodium.addClickListener(e -> {
                 // Navigation vers la nouvelle vue avec l'ID du tournoi
                 UI.getCurrent().navigate(VuePodium.class, t.getId());
            });
            add(btnVoirPodium);
        }
    }


    private VerticalLayout createStatCard(VaadinIcon icon, String label, Span valueSpan) {
        com.vaadin.flow.component.icon.Icon i = icon.create();
        i.setSize("24px");
        i.getElement().getStyle().set("color", "var(--lumo-primary-color)");

        valueSpan.getElement().getStyle().set("font-size", "1.5em");
        valueSpan.getElement().getStyle().set("font-weight", "bold");

        Span labelSpan = new Span(label);
        labelSpan.getElement().getStyle().set("font-size", "0.8em");
        labelSpan.getElement().getStyle().set("color", "var(--lumo-secondary-text-color)");

        VerticalLayout card = new VerticalLayout(i, valueSpan, labelSpan);
        card.setAlignItems(Alignment.CENTER);
        card.setSpacing(false);
        card.setPadding(false);
        card.getElement().getStyle().set("background-color", "var(--lumo-contrast-5pct)");
        card.getElement().getStyle().set("border-radius", "10px");
        card.getElement().getStyle().set("padding", "15px");
        card.setWidth("120px");
        card.getElement().getStyle().set("margin", "10px");

        return card;
    }

    private void showInscriptionDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Inscription au tournoi : " + tournoiActuel.getNom());

        H4 titreExistant = new H4("Option 1 : Je suis déjà enregistré");
        ComboBox<Joueur> comboJoueurs = new ComboBox<>("Sélectionnez votre profil");
        comboJoueurs.setWidthFull();
        comboJoueurs.setItemLabelGenerator(j -> j.getSurnom() + " (" + j.getPrenom() + " " + j.getNom() + ")");

        try {
            comboJoueurs.setItems(Joueur.getAllJoueurs());
        } catch (Exception e) {
            Notification.show("Erreur chargement joueurs : " + e.getMessage());
        }
        Button btnValiderInscription = new Button("M'inscrire avec ce profil");
        btnValiderInscription.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnValiderInscription.setWidthFull();

        btnValiderInscription.addClickListener(event -> {
            Joueur joueurSelectionne = comboJoueurs.getValue();
            if (joueurSelectionne == null) {
                Notification.show("Veuillez sélectionner un profil dans la liste.");
                return;
            }

            try {
                ServiceInscription.tenterInscription(tournoiActuel, java.util.List.of(joueurSelectionne));

                Notification.show(joueurSelectionne.getSurnom() + " inscrit avec succès !")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                dialog.close();
                actualiserGrilleParticipants();

            } catch (Exception e) {
                Notification.show("Erreur lors de l'inscription : " + e.getMessage(), 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        VerticalLayout sectionExistant = new VerticalLayout(titreExistant, comboJoueurs, btnValiderInscription);
        styleAsCard(sectionExistant);


        H4 titreNouveau = new H4("Option 2 : Je suis nouveau ici");
        Span explication = new Span("Vous devez d'abord créer votre fiche joueur dans notre base de données.");

        Button btnCreerCompte = new Button("Créer ma fiche joueur maintenant", VaadinIcon.ARROW_RIGHT.create());
        btnCreerCompte.setIconAfterText(true);
        btnCreerCompte.setWidthFull();

        btnCreerCompte.addClickListener(e -> {
            dialog.close();
            UI.getCurrent().navigate(VueInscription.class);
            Notification.show("Une fois votre fiche créée, revenez ici pour vous inscrire au tournoi.");
        });

        VerticalLayout sectionNouveau = new VerticalLayout(titreNouveau, explication, btnCreerCompte);
        styleAsCard(sectionNouveau);


        VerticalLayout dialogContent = new VerticalLayout(sectionExistant, sectionNouveau);
        dialogContent.setSpacing(true);
        dialogContent.setPadding(false);

        dialog.add(dialogContent);

        Button closeButton = new Button("Annuler", e -> dialog.close());
        dialog.getFooter().add(closeButton);

        dialog.open();
    }

    private void styleAsCard(VerticalLayout layout) {
        layout.getElement().getStyle().set("background-color", "var(--lumo-contrast-5pct)");
        layout.getElement().getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        layout.getElement().getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");
        layout.setPadding(true);
    }


    private void actualiserGrilleParticipants() {
        if (tournoiActuel == null) return;
        try {
            List<Joueur> inscrits = tournoiActuel.getJoueursInscrits();
            gridParticipants.setItems(inscrits);
        } catch (SQLException e) {
            Notification.show("Erreur rechargement participants : " + e.getMessage());
        }
    }


    private void actualiserAffichageRondes(Tournoi t) {
        accordionRondes.getElement().removeAllChildren();

        try {
            List<Ronde> rondes = Ronde.getRondesDuTournoi(t.getId());

            if (rondes.isEmpty()) {
                accordionRondes.add("En attente", new VerticalLayout(new Span("Le tournoi n'a pas encore démarré.")));
                return;
            }

            for (Ronde ronde : rondes) {
                VerticalLayout contentLayout = new VerticalLayout();
                contentLayout.setPadding(false);
                contentLayout.setSpacing(true);

                List<Match> matchs = Match.getMatchsDeLaRonde(ronde.getId());
                List<MatchCard> activeCards = new ArrayList<>();

                if (matchs.isEmpty()) {
                    contentLayout.add(new Span("Aucun match généré pour cette ronde."));
                } else {
                    for (Match match : matchs) {
                        MatchCard card = new MatchCard(match);
                        contentLayout.add(card);
                        if (Sessioninfo.adminConnected() && match.getStatut() != StatutMatch.TERMINE) {
                            activeCards.add(card);
                        }
                    }
                }

                if (ronde.getStatut() == StatutRonde.EN_COURS || ronde.getStatut() == StatutRonde.TERMINEE) {
                try {
                    // On récupère les joueurs qui n'ont pas joué à CETTE ronde (d'où le +1)
                    List<Joueur> joueursExempts = Ronde.getJoueursPrioritaires(t.getId(), ronde.getNumero() + 1);

                    if (!joueursExempts.isEmpty()) {
                        VerticalLayout exemptsBox = new VerticalLayout();
                        exemptsBox.addClassName(LumoUtility.Background.CONTRAST_5);
                        exemptsBox.addClassName(LumoUtility.BorderRadius.MEDIUM);
                        exemptsBox.addClassName(LumoUtility.Margin.Top.MEDIUM);
                        exemptsBox.setPadding(true);
                        exemptsBox.setSpacing(false);
                        exemptsBox.setWidthFull();
                        exemptsBox.setMaxWidth("600px");

                        H5 titreExempts = new H5("Joueurs exemptés de cette ronde");
                        titreExempts.addClassName(LumoUtility.Margin.Top.NONE);
                        titreExempts.addClassName(LumoUtility.Margin.Bottom.SMALL);
                        titreExempts.addClassName(LumoUtility.TextColor.SECONDARY);

                        String listeNomsStr = joueursExempts.stream()
                                .map(j -> j.getPrenom() + " " + j.getNom().toUpperCase())
                                .collect(Collectors.joining(", "));

                        Span nomsSpan = new Span(listeNomsStr);
                        nomsSpan.addClassName(LumoUtility.FontWeight.MEDIUM);
                        nomsSpan.addClassName(LumoUtility.TextColor.PRIMARY);

                        exemptsBox.add(titreExempts, nomsSpan);
                        contentLayout.add(exemptsBox);
                        contentLayout.setHorizontalComponentAlignment(Alignment.CENTER, exemptsBox);

                    } else {
                        // Petit message si tout le monde joue, visible tout le temps aussi
                        Span noExemptsSpan = new Span("ℹ️ Tous les joueurs disponibles ont participé à cette ronde.");
                        noExemptsSpan.addClassName(LumoUtility.TextColor.SECONDARY);
                        noExemptsSpan.addClassName(LumoUtility.FontSize.SMALL);
                        noExemptsSpan.addClassName(LumoUtility.Margin.Top.SMALL);

                        contentLayout.add(noExemptsSpan);
                        contentLayout.setHorizontalComponentAlignment(Alignment.CENTER, noExemptsSpan);
                    }

                } catch (SQLException e) {
                    System.err.println("Erreur chargement exempts pour ronde " + ronde.getNumero() + " : " + e.getMessage());
                }
            }
                // -------------------------------------------------------------------------

                if (Sessioninfo.adminConnected() && ronde.getStatut() == StatutRonde.EN_COURS) {

                    HorizontalLayout buttonsLayout = new HorizontalLayout();
                    buttonsLayout.setWidthFull();
                    buttonsLayout.setJustifyContentMode(JustifyContentMode.END);

                    Button btnSaveDraft = new Button("Sauvegarder les scores", VaadinIcon.DISC.create());
                    btnSaveDraft.addClickListener(e -> {
                        try {
                            int saveCount = 0;
                            for (MatchCard card : activeCards) {
                                Match.sauvegarderScoresTemporaires(card.getMatch().getId(), card.getScore1Saisi(), card.getScore2Saisi());
                                saveCount++;
                            }
                            Notification.show(saveCount + " scores sauvegardés (brouillon).")
                                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        } catch (Exception ex) {
                            Notification.show("Erreur sauvegarde : " + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
                        }
                    });
                    
                    Button btnCloturer = new Button("CLÔTURER LA RONDE & DISTRIBUER LES POINTS", VaadinIcon.CHECK_CIRCLE.create());
                    btnCloturer.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

                    // --- MODIF 1 : Le bouton n'est plus grisé ---
                    // On retire : btnCloturer.setEnabled(...); et le tooltip.

                    btnCloturer.addClickListener(e -> {
                        // --- MODIF 1 : Vérification au clic ---
                        boolean allScoresEntered = activeCards.stream().allMatch(MatchCard::scoresSontSaisis);
                        if (!allScoresEntered) {
                            Notification.show("Impossible de clôturer : des scores sont manquants !", 3000, Notification.Position.MIDDLE)
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                            return; // On arrête tout ici
                        }

                        // Si tout est bon, on ouvre la boîte de dialogue
                        Dialog confirmDialog = new Dialog();
                        confirmDialog.setHeaderTitle("Confirmer la clôture de la Ronde " + ronde.getNumero());
                        confirmDialog.add(new VerticalLayout(
                                new Span("Attention : Cette action est irréversible."),
                                new Span("Les scores seront figés et les points distribués aux joueurs."),
                                new Span("Êtes-vous sûr ?")
                        ));

                        Button confirmBtn = new Button("Oui, clôturer et passer à la suite", event -> {
                            try {
                                confirmDialog.close();

                                ronde.cloturerRondeEtDistribuerPoints();
                                Notification.show("Ronde " + ronde.getNumero() + " clôturée. Points distribués.")
                                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                                tournoiActuel.passerRondeSuivante();
                                actualiserClassement();
                                Tournoi tCheck = Tournoi.getTournoiById(tournoiActuel.getId());
                                if (tCheck.isFini()) {
                                    Notification.show("Le tournoi est maintenant TERMINÉ ! Félicitations aux vainqueurs.", 5000, Notification.Position.MIDDLE)
                                            .addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                                } else {
                                    Notification.show("La ronde suivante a démarré ! Les nouveaux matchs sont générés.", 3000, Notification.Position.TOP_CENTER)
                                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                }

                                UI.getCurrent().getPage().reload();

                            } catch (Exception ex) {
                                Notification.show("Erreur lors de l'opération : " + ex.getMessage(), 5000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                                ex.printStackTrace();
                            }
                        });
                        confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

                        confirmDialog.getFooter().add(new Button("Annuler", event -> confirmDialog.close()));
                        confirmDialog.getFooter().add(confirmBtn);
                        confirmDialog.open();
                    });

                    buttonsLayout.add(btnSaveDraft, btnCloturer);
                    contentLayout.add(buttonsLayout);
                }


                AccordionPanel panel = accordionRondes.add("Ronde " + ronde.getNumero() + " (" + ronde.getStatut() + ")", contentLayout);
                if (ronde.getStatut() == StatutRonde.EN_COURS) {
                    panel.setOpened(true);
                }
            }

        } catch (SQLException e) {
            Notification.show("Erreur chargement des matchs : " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void configureGridClassement() {
        contenuOngletClassement.setSizeFull();
        contenuOngletClassement.setPadding(false); 
        contenuOngletClassement.setSpacing(false);


        gridClassement.setSizeFull();
        gridClassement.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);

        gridClassement.addColumn(new ComponentRenderer<>(ligne -> {
            Span rangSpan = new Span("#" + ligne.getRang());
            rangSpan.addClassName(LumoUtility.FontWeight.BOLD);
            if (ligne.getRang() == 1) rangSpan.getElement().getStyle().set("color", "gold");
            else if (ligne.getRang() == 2) rangSpan.getElement().getStyle().set("color", "silver");
            else if (ligne.getRang() == 3) rangSpan.getElement().getStyle().set("color", "#cd7f32"); // Bronze
            return rangSpan;
        })).setHeader("Rang").setWidth("70px").setFlexGrow(0);

        gridClassement.addColumn(LigneClassement::getNomAffichage).setHeader("Joueur").setAutoWidth(true);

        gridClassement.addColumn(new ComponentRenderer<>(ligne -> {
            Span pointsSpan = new Span(ligne.getTotalPoints() + " pts");
            pointsSpan.addClassName(LumoUtility.FontWeight.EXTRABOLD);
            pointsSpan.addClassName(LumoUtility.FontSize.LARGE);
            pointsSpan.addClassName(LumoUtility.TextColor.PRIMARY);
            return pointsSpan;
        })).setHeader("Total Points").setWidth("140px").setFlexGrow(0).setTextAlign(ColumnTextAlign.END);

        contenuOngletClassement.add( gridClassement);
        // La grille prend tout l'espace restant
        contenuOngletClassement.setFlexGrow(1, gridClassement);
    }



    private void actualiserClassement() {
        if (tournoiActuel == null) return;
        try {
            // On récupère la liste complète et on l'affiche directement
            List<LigneClassement> classement = tournoiActuel.getClassement();
            gridClassement.setItems(classement);
            
            if (classement.isEmpty() && tournoiActuel.isOuvert() && !tournoiActuel.isFini()) {
                 Notification.show("Le classement s'affichera ici après la première ronde.", 3000, Notification.Position.BOTTOM_START);
            }
            
        } catch (SQLException e) {
            Notification.show("Erreur chargement classement : " + e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }
}