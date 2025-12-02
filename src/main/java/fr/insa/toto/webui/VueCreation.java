package fr.insa.toto.webui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.toto.model.Joueur;
import fr.insa.toto.model.Tournoi;

import java.util.Set;

@Route(value = "nouveau-tournoi")
@PageTitle("Création Tournoi")
public class VueCreation extends VerticalLayout {

    private TextField tfNom = new TextField("Nom du tournoi");
    private IntegerField tfNbRondes = new IntegerField("Nombre de rondes");
    private IntegerField tfNbrEquipes = new IntegerField("Nombre d'équipes (estimé)");
    private IntegerField tfNbrJoueurs = new IntegerField("Joueurs par équipe");
    private IntegerField tfNbrTerrains = new IntegerField("Terrains disponibles");
    private IntegerField tfDuree = new IntegerField("Durée matchs (min)");

    // --- NOUVEAU : Grille de sélection des joueurs ---
    private Grid<Joueur> gridSelectionJoueurs = new Grid<>(Joueur.class);

    private Button bValider = new Button("Créer le tournoi");
    private Button bAnnuler = new Button("Annuler");

    public VueCreation() {
        setAlignItems(Alignment.CENTER);
        add(new H2("Nouveau Tournoi"));

        // Config des champs (inchangé)
        tfNom.setWidth("300px");
        tfNbRondes.setMin(1); tfNbRondes.setValue(3);
        tfNbrJoueurs.setMin(1); tfNbrJoueurs.setValue(2);
        tfNbrTerrains.setMin(1);

        // --- CONFIGURATION GRILLE JOUEURS ---
        configureGridJoueurs();

        // Style Boutons
        bValider.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        bAnnuler.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // --- ACTION VALIDER ---
        bValider.addClickListener(event -> {
            try {
                // 1. Création du Tournoi
                String nom = tfNom.getValue();
                int nbRondes = tfNbRondes.getValue() != null ? tfNbRondes.getValue() : 0;
                int nbrEquipes = tfNbrEquipes.getValue() != null ? tfNbrEquipes.getValue() : 0;
                int nbrJoueurs = tfNbrJoueurs.getValue() != null ? tfNbrJoueurs.getValue() : 0;
                int nbrTerrains = tfNbrTerrains.getValue() != null ? tfNbrTerrains.getValue() : 0;
                int duree = tfDuree.getValue() != null ? tfDuree.getValue() : 0;

                Tournoi t = new Tournoi(nbrJoueurs, nbrEquipes, duree, nbRondes, nom, nbrTerrains, false, false);
                Tournoi.creerTournoi(t); // Le tournoi est sauvé, il a maintenant un ID

                // 2. Inscription des Joueurs sélectionnés
                Set<Joueur> selection = gridSelectionJoueurs.getSelectedItems();
                if (!selection.isEmpty()) {
                    // On convertit le Set en List et on inscrit
                    t.inscrireJoueurs(selection.stream().toList());
                }

                Notification.show("Tournoi créé avec " + selection.size() + " joueurs inscrits !", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                getUI().ifPresent(ui -> ui.navigate(VueListeTournois.class));

            } catch (Exception e) {
                e.printStackTrace();
                Notification.show("Erreur : " + e.getMessage(), 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        bAnnuler.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(VueListeTournois.class)));

        HorizontalLayout boutons = new HorizontalLayout(bValider, bAnnuler);

        // Mise en page
        VerticalLayout form = new VerticalLayout(
            tfNom, 
            new HorizontalLayout(tfNbRondes, tfNbrTerrains),
            new HorizontalLayout(tfNbrEquipes, tfNbrJoueurs, tfDuree),
            new H4("Sélectionnez les participants :"),
            gridSelectionJoueurs, // Ajout de la grille dans le formulaire
            boutons
        );
        form.setAlignItems(Alignment.CENTER);
        form.setWidth("600px"); // On limite la largeur pour faire propre
        
        add(form);
    }

    private void configureGridJoueurs() {
        gridSelectionJoueurs.setHeight("250px");
        gridSelectionJoueurs.setSelectionMode(Grid.SelectionMode.MULTI); // C'est ça qui met les cases à cocher !
        gridSelectionJoueurs.setColumns("surnom", "sexe", "taille");
        
        // Chargement des données
        try {
            gridSelectionJoueurs.setItems(Joueur.getAllJoueurs());
        } catch (Exception e) {
            Notification.show("Impossible de charger les joueurs : " + e.getMessage());
        }
    }
}
