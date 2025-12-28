package fr.insa.toto.webui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.NotificationVariant;
import fr.insa.toto.model.ServiceInscription;
import com.vaadin.flow.component.combobox.*;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import fr.insa.toto.model.Ronde;
import fr.insa.toto.model.Match;
import fr.insa.toto.model.Joueur;
import java.util.List;
import java.util.stream.Collectors;

import fr.insa.toto.model.Joueur;
import fr.insa.toto.model.Tournoi;
import fr.insa.toto.webui.Session.Sessioninfo;
import java.sql.SQLException;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import fr.insa.toto.model.StatutMatch;
import java.util.ArrayList;
import fr.insa.toto.model.StatutRonde;

@Route(value = "tournoi", layout = InterfacePrincipale.class) // L'URL sera .../tournoi/ID
@PageTitle("Détails du Tournoi")

public class VueDetailsTournoi extends VerticalLayout implements HasUrlParameter<Integer> {

    private H2 titreTournoi = new H2();
    private Span statusBadge = new Span();
    private Button btnInscrire = new Button("S'inscrire au tournoi", VaadinIcon.USER_CHECK.create());
    private Button btnDemarrer = new Button("Démarrer le tournoi ", VaadinIcon.PLAY.create());
    // On garde une référence au tournoi actuel pour les actions des boutons
    private Tournoi tournoiActuel;
    // Informations détaillées
    private Span infoRondes = new Span();
    private Span infoTerrains = new Span();
    private Span infoEquipes = new Span();
    private Span infoDuree = new Span();
    private H4 titreParticipants = new H4("Participants inscrits");
    private Accordion accordionRondes = new Accordion();
    private Button btnRetour = new Button("Retour liste", VaadinIcon.ARROW_LEFT.create());

    private Grid<Joueur> gridParticipants = new Grid<>(Joueur.class);

    public VueDetailsTournoi() {
        setAlignItems(Alignment.CENTER);

       btnRetour.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(VueListeTournois.class)));
        
       FlexLayout infosLayout = new FlexLayout();
        // On centre les éléments sur la ligne
        infosLayout.setAlignItems(Alignment.CENTER);
        infosLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        // C'EST CETTE LIGNE QUI FONCTIONNE MAINTENANT : permet le retour à la ligne
        infosLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        infosLayout.setWidthFull();

        // On crée les 4 cartes
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
        headerLayout.setAlignItems(Alignment.CENTER); // Centre verticalement
        headerLayout.setWidthFull(); // Prend toute la largeur
        add(headerLayout);


        // 2. Création du contenu du premier onglet ("Informations")
        VerticalLayout contenuOngletInfos = new VerticalLayout();
        contenuOngletInfos.setPadding(false); // Enlève les marges inutiles à l'intérieur de l'onglet
        contenuOngletInfos.setAlignItems(Alignment.CENTER); 
        btnInscrire.setVisible(false);
        // Style "Primaire" (bleu par défaut) pour l'inscription
        btnInscrire.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        btnDemarrer.setVisible(false);
        // Style "Succès" (vert) et "Large" pour que le bouton de démarrage soit bien visible
        btnDemarrer.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_LARGE);
        HorizontalLayout actionsLayout = new HorizontalLayout(btnInscrire, btnDemarrer);
        actionsLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        
        contenuOngletInfos.add(actionsLayout);
        contenuOngletInfos.add(new H4("Paramètres du tournoi"));
        contenuOngletInfos.add(infosLayout);
        contenuOngletInfos.add(this.titreParticipants);
        contenuOngletInfos.add(gridParticipants);


        // 3. Création du contenu du deuxième onglet ("Matchs & Résultats").
        VerticalLayout contenuOngletMatchs = new VerticalLayout();
        contenuOngletMatchs.setPadding(false);
        this.accordionRondes.setWidthFull();
        contenuOngletMatchs.add(this.accordionRondes);


        // 4. Création et configuration du TabSheet (la boîte à onglets)
        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidthFull(); 
        tabSheet.add("Informations", contenuOngletInfos);
        tabSheet.add("Matchs & Résultats", contenuOngletMatchs);
        add(tabSheet);

        btnDemarrer.addClickListener(e -> {
    if (tournoiActuel == null) return;
    try {
        tournoiActuel.lancerTournoi();

        // 2. Notification de succès
        Notification.show("Le tournoi est démarré ! Les matchs sont générés.")
            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        // On recharge simplement la vue actuelle
        UI.getCurrent().getPage().reload();

    } catch (Exception ex) {
        Notification.show("Erreur lors du lancement: " + ex.getMessage())
            .addThemeVariants(NotificationVariant.LUMO_ERROR);
        ex.printStackTrace();
    }
});
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
        
        // Gestion du statut (Ouvert/Fini)
        String statut = "En préparation";          //Valeur a changer en recuperant le statut du tournoi
        String couleur = "grey";
        if (t.isFini()) {
            statut = "Terminé";
            couleur = "grey";
        } else if (t.isOuvert()) {
            statut = "En préparation";
            couleur = "green";
        }
        else if (!t.isOuvert()) {
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

        // Remplissage des infos
        infoRondes.setText("" + t.getNbrRondes());
        infoTerrains.setText("" + t.getNbrTerrains());
        infoEquipes.setText("" + t.getNbrJoueursParEquipe());
        // Pour la durée, on peut garder "min" car c'est court
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
            // On vérifie par sécurité qu'on a bien un tournoi chargé
            if (tournoiActuel != null) {
                // On ouvre la boîte de dialogue d'inscription
                showInscriptionDialog();
            }
        });
        actualiserAffichageRondes(t);
    }


private VerticalLayout createStatCard(VaadinIcon icon, String label, Span valueSpan) {
    // 1. Configuration de l'icône
    com.vaadin.flow.component.icon.Icon i = icon.create();
    i.setSize("24px");
    // Utilise la couleur primaire du thème (souvent bleu)
    i.getElement().getStyle().set("color", "var(--lumo-primary-color)");

    // 2. Configuration de la valeur (le gros chiffre)
    valueSpan.getElement().getStyle().set("font-size", "1.5em");
    valueSpan.getElement().getStyle().set("font-weight", "bold");

    // 3. Configuration du libellé (le petit texte)
    Span labelSpan = new Span(label);
    labelSpan.getElement().getStyle().set("font-size", "0.8em");
    labelSpan.getElement().getStyle().set("color", "var(--lumo-secondary-text-color)");

    // 4. Assemblage dans une mini layout vertical
    VerticalLayout card = new VerticalLayout(i, valueSpan, labelSpan);
    card.setAlignItems(Alignment.CENTER); // Tout centrer
    card.setSpacing(false);
    card.setPadding(false);
    // Ajoute un petit fond et des bordures arrondies
    card.getElement().getStyle().set("background-color", "var(--lumo-contrast-5pct)");
    card.getElement().getStyle().set("border-radius", "10px");
    card.getElement().getStyle().set("padding", "15px");
    // Définit une largeur fixe pour que toutes les cartes soient égales
    card.setWidth("120px");
    card.getElement().getStyle().set("margin", "10px"); // Marge autour de la carte

    return card;
}

private void showInscriptionDialog() {
    Dialog dialog = new Dialog();
    dialog.setHeaderTitle("Inscription au tournoi : " + tournoiActuel.getNom());

    // -Pour les joueurs déjà existants
    H4 titreExistant = new H4("Option 1 : Je suis déjà enregistré");
    ComboBox<Joueur> comboJoueurs = new ComboBox<>("Sélectionnez votre profil");
    comboJoueurs.setWidthFull();
    // On affiche le surnom, le prénom et le nom dans la liste pour aider à choisir
    comboJoueurs.setItemLabelGenerator(j -> j.getSurnom() + " (" + j.getPrenom() + " " + j.getNom() + ")");
    
    // Chargement des données depuis la BDD
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

    // On regroupe la section 1 dans un layout visuel (une "carte")
    VerticalLayout sectionExistant = new VerticalLayout(titreExistant, comboJoueurs, btnValiderInscription);
    styleAsCard(sectionExistant);


    //Pour les nouveaux joueurs
    H4 titreNouveau = new H4("Option 2 : Je suis nouveau ici");
    Span explication = new Span("Vous devez d'abord créer votre fiche joueur dans notre base de données.");
    
    Button btnCreerCompte = new Button("Créer ma fiche joueur maintenant", VaadinIcon.ARROW_RIGHT.create());
    btnCreerCompte.setIconAfterText(true); // Met la flèche à droite
    btnCreerCompte.setWidthFull();
    
    btnCreerCompte.addClickListener(e -> {
        dialog.close(); 
        // On navigue vers la vue d'inscription
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
            // Liste pour garder une référence vers les cartes actives (pour la sauvegarde)
            List<MatchCard> activeCards = new ArrayList<>();

            if (matchs.isEmpty()) {
                 contentLayout.add(new Span("Aucun match généré pour cette ronde."));
            } else {
                for (Match match : matchs) {
                    MatchCard card = new MatchCard(match);
                    contentLayout.add(card);
                    // Si c'est une carte de saisie (admin + pas terminé), on la garde en mémoire
                    if(Sessioninfo.adminConnected() && match.getStatut() != StatutMatch.TERMINE) {
                        activeCards.add(card);
                    }
                }
            }
            if (ronde.getStatut() == StatutRonde.EN_COURS) {
            try {
    // On récupère les joueurs qui n'ont pas joué à CETTE ronde
    List<Joueur> joueursExempts = Ronde.getJoueursPrioritaires(t.getId(), ronde.getNumero()+1);

    if (!joueursExempts.isEmpty()) {
        // CAS 1 : IL Y A DES EXEMPTS -> On affiche la boîte grise (Code existant)
        VerticalLayout exemptsBox = new VerticalLayout();
        exemptsBox.addClassName(LumoUtility.Background.CONTRAST_5);
        exemptsBox.addClassName(LumoUtility.BorderRadius.MEDIUM);
        exemptsBox.addClassName(LumoUtility.Margin.Top.MEDIUM);
        exemptsBox.setPadding(true);
        exemptsBox.setSpacing(false);
        exemptsBox.setWidthFull();
        exemptsBox.setMaxWidth("600px");

        H5 titreExempts = new H5("Joueurs exemptés de cette ronde (prioritaires au prochain tour)");
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
        // CAS 2 : AJOUT ICI -> PAS D'EXEMPTS (Tout le monde joue)
        // On affiche un simple petit texte informatif
        Span noExemptsSpan = new Span("ℹ️ Tous les joueurs disponibles participent à cette ronde.");

        // Un peu de style pour que ça fasse propre et discret (gris, petite police)
        noExemptsSpan.addClassName(LumoUtility.TextColor.SECONDARY);
        noExemptsSpan.addClassName(LumoUtility.FontSize.SMALL);
        noExemptsSpan.addClassName(LumoUtility.Margin.Top.SMALL);

        // On l'ajoute au layout principal, centré
        contentLayout.add(noExemptsSpan);
        contentLayout.setHorizontalComponentAlignment(Alignment.CENTER, noExemptsSpan);
    }

} catch (SQLException e) {
    System.err.println("Erreur chargement exempts pour ronde " + ronde.getNumero() + " : " + e.getMessage());
}
            }

            // --- GESTION DES BOUTONS D'ACTION POUR LA RONDE EN COURS ---
            if (Sessioninfo.adminConnected() && ronde.getStatut() == StatutRonde.EN_COURS) {
                
                HorizontalLayout buttonsLayout = new HorizontalLayout();
                buttonsLayout.setWidthFull();
                buttonsLayout.setJustifyContentMode(JustifyContentMode.END);

                // Bouton 1 : Sauvegarder (Brouillon)
                Button btnSaveDraft = new Button("Sauvegarder les scores", VaadinIcon.DISC.create());
                btnSaveDraft.addClickListener(e -> {
                    try {
                        int saveCount = 0;
                        for (MatchCard card : activeCards) {
                            // Appelle la nouvelle méthode de sauvegarde temporaire
                            Match.sauvegarderScoresTemporaires(card.getMatch().getId(), card.getScore1Saisi(), card.getScore2Saisi());
                            saveCount++;
                        }
                        Notification.show(saveCount + " scores sauvegardés (brouillon).")
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        // Pas besoin de recharger toute la page, les champs sont déjà à jour visuellement
                    } catch (Exception ex) {
                         Notification.show("Erreur sauvegarde : " + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
                });

                // Bouton 2 : Clôturer la ronde (Actif seulement si tout semble saisi)
                Button btnCloturer = new Button("CLÔTURER LA RONDE & DISTRIBUER LES POINTS", VaadinIcon.CHECK_CIRCLE.create());
                btnCloturer.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR); // Rouge pour signaler une action importante
                
                // Vérification basique : est-ce que tous les champs ont une valeur ?
                boolean allScoresEntered = activeCards.stream().allMatch(MatchCard::scoresSontSaisis);
                btnCloturer.setEnabled(allScoresEntered);
                if(!allScoresEntered) {
                    btnCloturer.setTooltipText("Saisissez tous les scores pour activer la clôture.");
                }

                btnCloturer.addClickListener(e -> {
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
                            Tournoi tCheck = Tournoi.getTournoiById(tournoiActuel.getId());
                            if(tCheck.isFini()) {
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
}