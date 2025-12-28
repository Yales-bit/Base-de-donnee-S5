package fr.insa.toto.webui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import fr.insa.toto.model.Joueur;
import fr.insa.toto.model.dto.LigneHistoriqueDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Route(value = "profil", layout = InterfacePrincipale.class)
@PageTitle("Profil Joueur")
public class VueProfilJoueur extends VerticalLayout implements HasUrlParameter<Integer> {

    // --- Composants d'en-tête ---
    private Button btnRetour;
    private H1 surnomHeader = new H1();

    // --- Composants de la section "Informations" (Carte Profil) ---
    private VerticalLayout profileCardLayout;
    private FlexLayout quickStatsLayout; // FlexLayout pour le wrap
    private VerticalLayout identityDetailsLayout;

    // --- Composants de la section "Historique" ---
    private VerticalLayout historySectionLayout;
    private H3 titreHistorique = new H3("Historique des Matchs");
    private Grid<LigneHistoriqueDTO> gridHistorique = new Grid<>(LigneHistoriqueDTO.class, false);


    public VueProfilJoueur() {
        // Configuration globale de la vue
        setWidthFull();
        setAlignItems(Alignment.CENTER);
        setPadding(true);
        setSpacing(true);
        addClassName(LumoUtility.Background.CONTRAST_5);

        // 1. Initialisation de l'En-tête (Header)
        initHeaderSection();

        // 2. Initialisation de la structure de la Carte Profil
        initProfileCardStructure();

        // 3. Initialisation de la section Historique
        initHistorySection();

        // Assemblage final de la vue
        VerticalLayout mainContent = new VerticalLayout(surnomHeader, profileCardLayout, historySectionLayout);
        mainContent.setWidthFull();
        mainContent.setMaxWidth("900px");
        mainContent.setPadding(false);
        mainContent.setSpacing(true);
        mainContent.setAlignItems(Alignment.STRETCH);

        HorizontalLayout topBar = new HorizontalLayout(btnRetour);
        topBar.setWidthFull();
        topBar.setMaxWidth("900px");

        add(topBar, mainContent);
    }

    private void initHeaderSection() {
        btnRetour = new Button("Retour", VaadinIcon.ARROW_LEFT.create());
        btnRetour.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        // Navigue vers la liste des tournois (à adapter si besoin)
        btnRetour.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(VueListeTournois.class)));

        surnomHeader.addClassName(LumoUtility.Margin.Top.NONE);
        surnomHeader.addClassName(LumoUtility.Margin.Bottom.SMALL);
        surnomHeader.getStyle().set("font-size", "2.5rem");
        surnomHeader.setText("Chargement du profil...");
    }

    private void initProfileCardStructure() {
        profileCardLayout = new VerticalLayout();
        profileCardLayout.setWidthFull();
        profileCardLayout.setPadding(true);
        profileCardLayout.setSpacing(true);
        profileCardLayout.addClassName(LumoUtility.Background.BASE);
        profileCardLayout.addClassName(LumoUtility.BorderRadius.LARGE);
        profileCardLayout.addClassName(LumoUtility.BoxShadow.SMALL);

        H3 infoTitle = new H3("Informations Personnelles");
        infoTitle.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.TextColor.SECONDARY);

        // FlexLayout pour les stats rapides
        quickStatsLayout = new FlexLayout();
        quickStatsLayout.setWidthFull();
        quickStatsLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        quickStatsLayout.getStyle().set("gap", "var(--lumo-space-m)"); 

        identityDetailsLayout = new VerticalLayout();
        identityDetailsLayout.setPadding(false);
        identityDetailsLayout.setSpacing(true);
        identityDetailsLayout.addClassName(LumoUtility.Margin.Top.MEDIUM);

        profileCardLayout.add(infoTitle, quickStatsLayout, identityDetailsLayout);
    }

    private void initHistorySection() {
        historySectionLayout = new VerticalLayout();
        historySectionLayout.setWidthFull();
        historySectionLayout.setPadding(false);
        historySectionLayout.setSpacing(true);
        historySectionLayout.addClassName(LumoUtility.Margin.Top.LARGE);

        titreHistorique.addClassNames(LumoUtility.Margin.Bottom.SMALL, LumoUtility.TextColor.SECONDARY);

        configureGridHistorique();
        gridHistorique.setVisible(false); 

        historySectionLayout.add(titreHistorique, gridHistorique);
    }

    private void configureGridHistorique() {
        gridHistorique.addColumn(LigneHistoriqueDTO::getNomTournoi)
                .setHeader("Tournoi").setSortable(true).setAutoWidth(true);
        
        gridHistorique.addColumn(LigneHistoriqueDTO::getNumeroRonde)
                .setHeader("Ronde").setSortable(true).setTextAlign(ColumnTextAlign.CENTER).setWidth("80px").setFlexGrow(0);
        
        
        gridHistorique.addColumn(LigneHistoriqueDTO::getScore)
                .setHeader("Score").setTextAlign(ColumnTextAlign.CENTER).setWidth("100px").setFlexGrow(0);

        gridHistorique.addComponentColumn(ligne -> {
            Span resultat = new Span(ligne.isVictoire() ? "Victoire" : "Défaite");
            resultat.addClassName(ligne.isVictoire() ? LumoUtility.TextColor.SUCCESS : LumoUtility.TextColor.ERROR);
            resultat.addClassName(LumoUtility.FontWeight.BOLD);
            resultat.getElement().getStyle().set("padding", "4px 8px");
            resultat.getElement().getStyle().set("border-radius", "4px");
            resultat.getElement().getStyle().set("background-color",
                    ligne.isVictoire() ? "var(--lumo-success-color-10pct)" : "var(--lumo-error-color-10pct)");
            return resultat;
        }).setHeader("Résultat").setTextAlign(ColumnTextAlign.CENTER).setWidth("120px").setFlexGrow(0);
        gridHistorique.addColumn(LigneHistoriqueDTO::getAdversaires)
                .setHeader("Adversaire(s)").setAutoWidth(true);
        gridHistorique.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        gridHistorique.setAllRowsVisible(true);
    }


    @Override
    public void setParameter(BeforeEvent event, Integer joueurId) {
        try {
            Joueur joueur = Joueur.getJoueurById(joueurId);
            if (joueur != null) {
                quickStatsLayout.removeAll();
                identityDetailsLayout.removeAll();
                
                afficherJoueur(joueur);
                chargerHistorique(joueurId);
            } else {
                Notification.show("Joueur introuvable", 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                getUI().ifPresent(ui -> ui.navigate(VueListeTournois.class)); 
            }
        } catch (SQLException e) {
            Notification.show("Erreur BDD : " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    /**
     * Remplit la structure visuelle avec les données du joueur.
     */
    private void afficherJoueur(Joueur j) {
        surnomHeader.setText(j.getSurnom());

        String ageText = "Inconnu";
        try {
            LocalDate dateNaissance = LocalDate.of(j.getAnnee(), j.getMois(), j.getJour());
            int age = Period.between(dateNaissance, LocalDate.now()).getYears();
            ageText = age + " ans";
        } catch (Exception e) { /* Date invalide */ }

        String dateNaissanceText = String.format("%02d/%02d/%04d", j.getJour(), j.getMois(), j.getAnnee());
        
        // Utilisation de toString() pour le texte.
        String sexeText = (j.getSexe() != null) ? j.getSexe().toString() : "Non précisé";
        String tailleText = j.getTaille() > 0 ? j.getTaille() + " cm" : "Non renseignée";

        // --- CORRECTION ICÔNE DYNAMIQUE ---
        // On choisit la bonne icône selon la valeur de l'enum (MALE ou FEMALE)
        VaadinIcon iconSexe = VaadinIcon.USER; // Valeur par défaut (neutre)
        if (j.getSexe() != null) {
            // On utilise .name() en majuscule pour comparer le nom technique de l'enum
            String sexeName = j.getSexe().name().toUpperCase();
            if ("FEMININ".equals(sexeName)) {
                iconSexe = VaadinIcon.FEMALE;
            } else if ("MASCULIN".equals(sexeName)) {
                iconSexe = VaadinIcon.MALE;
            }
        }

        // 3. Remplissage des "Stats Rapides"
        quickStatsLayout.add(
                createQuickStatBox(VaadinIcon.CALENDAR_USER, "Âge", ageText),
                // Utilisation de ARROW_UP pour la taille
                createQuickStatBox(VaadinIcon.ARROW_UP, "Taille", tailleText),
                // Utilisation de l'icône dynamique calculée au-dessus
                createQuickStatBox(iconSexe, "Sexe", sexeText)
        );

        // 4. Remplissage des "Détails d'identité"
        identityDetailsLayout.add(
                createDetailRow(VaadinIcon.USER_CARD, "Nom complet", j.getPrenom() + " " + j.getNom().toUpperCase()),
                createDetailRow(VaadinIcon.DATE_INPUT, "Date de naissance", dateNaissanceText)
        );
    }

    private VerticalLayout createQuickStatBox(VaadinIcon icon, String label, String value) {
        Icon i = icon.create();
        i.setSize("32px");
        i.addClassName(LumoUtility.TextColor.PRIMARY);

        Span valueSpan = new Span(value);
        valueSpan.addClassName(LumoUtility.FontWeight.BOLD);
        valueSpan.addClassName(LumoUtility.FontSize.LARGE);

        Span labelSpan = new Span(label);
        labelSpan.addClassName(LumoUtility.FontSize.SMALL);
        labelSpan.addClassName(LumoUtility.TextColor.SECONDARY);

        VerticalLayout box = new VerticalLayout(i, valueSpan, labelSpan);
        box.setAlignItems(Alignment.CENTER);
        box.setPadding(true);
        box.setSpacing(false);
        box.addClassName(LumoUtility.Background.CONTRAST_5);
        box.addClassName(LumoUtility.BorderRadius.MEDIUM);
        box.setMinWidth("120px");
        box.setFlexGrow(1);
        return box;
    }

    private HorizontalLayout createDetailRow(VaadinIcon icon, String label, String value) {
        Icon i = icon.create();
        i.setSize("20px");
        i.addClassName(LumoUtility.TextColor.SECONDARY);
        i.addClassName(LumoUtility.Margin.Right.SMALL);

        Span labelSpan = new Span(label + " :");
        labelSpan.addClassName(LumoUtility.TextColor.SECONDARY);
        labelSpan.addClassName(LumoUtility.Margin.Right.SMALL);
        labelSpan.setWidth("150px"); 
        labelSpan.setClassName(LumoUtility.Flex.SHRINK_NONE);

        Span valueSpan = new Span(value);
        valueSpan.addClassName(LumoUtility.FontWeight.MEDIUM);

        HorizontalLayout row = new HorizontalLayout(i, labelSpan, valueSpan);
        row.setAlignItems(Alignment.CENTER);
        row.setWidthFull();
        return row;
    }

    private void chargerHistorique(int joueurId) {
        try {
            List<LigneHistoriqueDTO> historique = Joueur.getHistoriqueMatchs(joueurId);

            if (historique != null && !historique.isEmpty()) {
                gridHistorique.setItems(historique);
                gridHistorique.setVisible(true);
                titreHistorique.setText("Historique des Matchs (" + historique.size() + ")");
            } else {
                titreHistorique.setText("Aucun match joué pour le moment.");
                gridHistorique.setVisible(false);
            }
            titreHistorique.setVisible(true);

        } catch (SQLException e) {
            Notification.show("Erreur chargement historique : " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}