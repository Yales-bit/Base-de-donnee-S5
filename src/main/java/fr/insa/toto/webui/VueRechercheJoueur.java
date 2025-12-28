package fr.insa.toto.webui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import fr.insa.toto.model.Joueur;
import fr.insa.toto.model.StatutSexe;
import java.util.Comparator;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Route(value = "recherche", layout = InterfacePrincipale.class)
@PageTitle("Explorateur de Joueurs")
public class VueRechercheJoueur extends VerticalLayout {

    // Composants de filtrage
    private TextField searchField = new TextField("Recherche textuelle");
    private ComboBox<StatutSexe> sexeFilter = new ComboBox<>("Genre");
    private IntegerField minAgeField = new IntegerField("Age minimum");
    private IntegerField maxAgeField = new IntegerField("Age maximum");
    private IntegerField minTailleField = new IntegerField("Taille min (cm)");
    private IntegerField maxTailleField = new IntegerField("Taille max (cm)");
    private Button btnResetFilters = new Button("Réinitialiser les filtres", VaadinIcon.REFRESH.create());

    private Grid<Joueur> grid = new Grid<>(Joueur.class);
    private ListDataProvider<Joueur> dataProvider;

    public VueRechercheJoueur() {
        setSizeFull(); // La vue prend tout l'espace
        setPadding(true);
        setSpacing(true);

        // 1. Titre
        add(new H3("Explorateur de Joueurs"));

        
        configureFilters();
        configureGrid();
        chargerTousLesJoueurs();
    }

    private void configureFilters() {
        // Configuration des champs
        searchField.setPlaceholder("Nom, prénom, surnom...");
        searchField.setClearButtonVisible(true);
        // EAGER = le filtre s'applique dès qu'on tape une lettre
        searchField.setValueChangeMode(ValueChangeMode.EAGER);

        sexeFilter.setItems(StatutSexe.values());
        sexeFilter.setClearButtonVisible(true);
        sexeFilter.setPlaceholder("Tous");

        minAgeField.setMin(0); maxAgeField.setMin(0);
        minTailleField.setMin(0); maxTailleField.setMin(0);
        minTailleField.setStepButtonsVisible(true);
        maxTailleField.setStepButtonsVisible(true);

        btnResetFilters.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // Layout pour organiser les filtres proprement (Responsive)
        FormLayout filtersLayout = new FormLayout();
        filtersLayout.add(searchField, sexeFilter, minAgeField, maxAgeField, minTailleField, maxTailleField);
        
        filtersLayout.setColspan(searchField, 2);
        filtersLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1), // 1 colonne sur mobile
                new FormLayout.ResponsiveStep("600px", 2), // 2 colonnes sur tablette
                new FormLayout.ResponsiveStep("900px", 4)  // 4 colonnes sur desktop
        );

        // Ajout des filtres et du bouton reset dans un layout horizontal
        VerticalLayout filterPanel = new VerticalLayout(filtersLayout, btnResetFilters);
        filterPanel.setSpacing(false);
        filterPanel.setPadding(true);
        // Petit style "carte" pour le panneau de filtres
        filterPanel.addClassName(LumoUtility.Background.CONTRAST_5);
        filterPanel.addClassName(LumoUtility.BorderRadius.MEDIUM);
        filterPanel.setAlignItems(Alignment.END); // Bouton reset à droite

        add(filterPanel);

        // --- LOGIQUE DE FILTRAGE ---
        // On ajoute des écouteurs sur TOUS les champs. Dès qu'un champ change, on rafraîchit le filtre.
        searchField.addValueChangeListener(e -> onFilterChange());
        sexeFilter.addValueChangeListener(e -> onFilterChange());
        minAgeField.addValueChangeListener(e -> onFilterChange());
        maxAgeField.addValueChangeListener(e -> onFilterChange());
        minTailleField.addValueChangeListener(e -> onFilterChange());
        maxTailleField.addValueChangeListener(e -> onFilterChange());

        // Bouton Reset
        btnResetFilters.addClickListener(e -> {
            searchField.clear();
            sexeFilter.clear();
            minAgeField.clear();
            maxAgeField.clear();
            minTailleField.clear();
            maxTailleField.clear();
        });
    }

  
    private void onFilterChange() {
        if (dataProvider == null) return;

        dataProvider.setFilter(joueur -> {
            // --- 1. Filtre Textuel (Surnom OU Prénom OU Nom) ---
            String searchTerm = searchField.getValue().trim().toLowerCase();
            boolean matchesText = searchTerm.isEmpty() ||
                                  (joueur.getSurnom() != null && joueur.getSurnom().toLowerCase().contains(searchTerm)) ||
                                  (joueur.getPrenom() != null && joueur.getPrenom().toLowerCase().contains(searchTerm)) ||
                                  (joueur.getNom() != null && joueur.getNom().toLowerCase().contains(searchTerm));

            if (!matchesText) return false; // Si le texte ne correspond pas, on rejette tout de suite

            // --- 2. Filtre Genre ---
            StatutSexe selectedSexe = sexeFilter.getValue();
            boolean matchesSexe = selectedSexe == null || joueur.getSexe() == selectedSexe;

            if (!matchesSexe) return false;

            // --- 3. Filtre Taille ---
            Integer minTaille = minTailleField.getValue();
            Integer maxTaille = maxTailleField.getValue();
            if (minTaille != null && joueur.getTaille() < minTaille) return false;
            if (maxTaille != null && joueur.getTaille() > maxTaille) return false;

            // --- 4. Filtre Âge (Calcul dynamique) ---
            Integer minAge = minAgeField.getValue();
            Integer maxAge = maxAgeField.getValue();
            if (minAge != null || maxAge != null) {
                int ageJoueur = calculerAge(joueur);
                if (minAge != null && ageJoueur < minAge) return false;
                if (maxAge != null && ageJoueur > maxAge) return false;
            }

            // Si on arrive ici, le joueur respecte TOUS les critères actifs
            return true;
        });
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.removeAllColumns();

        grid.addColumn(Joueur::getSurnom)
            .setHeader("Surnom")
            .setSortable(true)
            .setAutoWidth(true);

        // 2. NOM COMPLET (Colonne calculée -> Besoin d'un comparateur)
        grid.addColumn(j -> j.getPrenom() + " " + j.getNom().toUpperCase())
            .setHeader("Nom complet")
            // ON DÉFINIT COMMENT COMPARER : On compare la chaîne complète en minuscules pour le tri
            .setComparator(Comparator.comparing(j -> (j.getPrenom() + " " + j.getNom()).toLowerCase()))
            .setSortable(true)
            .setAutoWidth(true);

        // 3. GENRE (Enum -> Tri par défaut OK, se base sur l'ordre de définition dans l'Enum)
        grid.addColumn(Joueur::getSexe)
            .setHeader("Genre")
            .setSortable(true)
            .setWidth("100px").setFlexGrow(0);

        // 4. ÂGE (Valeur calculée -> Besoin d'un comparateur numérique)
        grid.addColumn(this::calculerAge)
            .setHeader("Âge")
            // ON DÉFINIT COMMENT COMPARER : On compare le résultat entier de la méthode calculerAge
            .setComparator(Comparator.comparingInt(this::calculerAge))
            .setSortable(true)
            .setWidth("80px").setFlexGrow(0);

        // 5. TAILLE (Affichage String, mais tri numérique -> COMPARATEUR OBLIGATOIRE)
        // Si on ne met pas de comparateur, il triera alphabétiquement ("90 cm" > "100 cm")
        grid.addColumn(j -> j.getTaille() + " cm")
            .setHeader("Taille")
            
            .setComparator(Comparator.comparingInt(Joueur::getTaille))
            .setSortable(true)
            .setWidth("100px").setFlexGrow(0);


        // Colonne Bouton (Pas de tri ici)
        grid.addComponentColumn(joueur -> {
            Button b = new Button(VaadinIcon.EYE.create());
            b.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            b.setTooltipText("Voir le profil complet");
            b.addClickListener(e -> {
                b.getUI().ifPresent(ui -> ui.navigate(VueProfilJoueur.class, joueur.getId()));
            });
            HorizontalLayout layout = new HorizontalLayout(b);
            layout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            return layout;
        }).setWidth("80px").setFlexGrow(0);

        add(grid);
        // On donne à la grille tout l'espace vertical disponible
        setFlexGrow(1, grid);
    }

    private void chargerTousLesJoueurs() {
        try {
            List<Joueur> tousLesJoueurs = Joueur.getAllJoueurs();
            dataProvider = new ListDataProvider<>(tousLesJoueurs);

            // On l'assigne à la grille. C'est lui qui gérera les filtres maintenant.
            grid.setDataProvider(dataProvider);
            
            Notification.show(tousLesJoueurs.size() + " joueurs chargés.", 3000, Notification.Position.BOTTOM_END);

        } catch (SQLException e) {
            Notification.show("Erreur lors du chargement des joueurs : " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }

// Méthode utilitaire pour calculer l'âge précisément
    private int calculerAge(Joueur j) {
        try {
            LocalDate dateNaissance = LocalDate.of(j.getAnnee(), j.getMois(), j.getJour());
            return Period.between(dateNaissance, LocalDate.now()).getYears();
        } catch (Exception e) {
            // Si la date est invalide en BDD (ex: 30 février), on retourne 0 pour ne pas planter
            return 0;
        }
    }
}