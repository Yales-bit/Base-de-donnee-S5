package fr.insa.toto.webui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.toto.model.Tournoi;
import fr.insa.toto.webui.Session.Sessioninfo;

import java.sql.SQLException;
import java.util.List;

@Route(value = "tournois", layout = InterfacePrincipale.class)
@PageTitle("Liste des Tournois")
public class VueListeTournois extends VerticalLayout {

    // On initialise la grille sans classe pour éviter les colonnes automatiques par défaut
    // ou on nettoiera les colonnes juste après
    private Grid<Tournoi> grid = new Grid<>(Tournoi.class);
    private Button btnNouveau = new Button("Nouveau Tournoi", VaadinIcon.PLUS.create());

    public VueListeTournois() {
        setAlignItems(Alignment.CENTER);
        
        // 1. Titre
        add(new H2("Gestion des Tournois"));

        // 2. Configuration du bouton "Nouveau"
        btnNouveau.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate(VueCreation.class));
        });
        
        if(Sessioninfo.adminConnected()){
            HorizontalLayout toolbar = new HorizontalLayout(btnNouveau);
            toolbar.setWidthFull();
            toolbar.setJustifyContentMode(JustifyContentMode.END);
            add(toolbar);
   
        }
      
        // 3. Configuration de la Grille
        // IMPORTANT : On supprime toutes les colonnes par défaut pour les définir manuellement
        grid.removeAllColumns(); 
        
        // On ajoute les colonnes une par une
        grid.addColumn(Tournoi::getId).setHeader("ID").setWidth("50px").setFlexGrow(0);
        grid.addColumn(Tournoi::getNom).setHeader("Nom du Tournoi");
        grid.addColumn(Tournoi::getNbrRondes).setHeader("Rondes");
        grid.addColumn(Tournoi::getNbrTerrains).setHeader("Terrains");
        
        // Colonne calculée pour le statut
        grid.addColumn(t -> t.isFini() ? "Terminé" : (t.isOuvert() ? "En cours" : "Préparation"))
            .setHeader("Statut");

        // --- COLONNE BOUTON (Celle qui te manquait) ---
        grid.addComponentColumn(tournoi -> {
            Button btnDetails = new Button(VaadinIcon.EYE.create());
            btnDetails.addClickListener(e -> {
                // Navigation vers la page de détails avec l'ID
                btnDetails.getUI().ifPresent(ui -> 
                    ui.navigate(VueDetailsTournoi.class, tournoi.getId())
                );
            });
            return btnDetails;
        }).setHeader("Voir");

        // 4. Chargement des données
        updateList();
        add(grid);

       
    }

    private void updateList() {
        try {
            List<Tournoi> tournois = Tournoi.getAllTournois();
            grid.setItems(tournois);
        } catch (SQLException e) {
            Notification.show("Erreur lors du chargement : " + e.getMessage());
        }
    }
}