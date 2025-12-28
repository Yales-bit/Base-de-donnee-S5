package fr.insa.toto.webui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
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

    private Grid<Tournoi> grid = new Grid<>(Tournoi.class);
    private Button btnNouveau = new Button("Nouveau Tournoi", VaadinIcon.PLUS.create());

    public VueListeTournois() {
        setAlignItems(Alignment.CENTER);
 
        add(new H2("Gestion des Tournois"));


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
        grid.removeAllColumns(); 
        
        grid.addColumn(Tournoi::getId).setHeader("ID").setWidth("50px").setFlexGrow(0);
        grid.addColumn(Tournoi::getNom).setHeader("Nom du Tournoi");
        grid.addColumn(Tournoi::getNbrRondes).setHeader("Rondes");
        grid.addColumn(Tournoi::getNbrTerrains).setHeader("Terrains");
        
        // Colonne calculée pour le statut (Correction de la logique : si pas fini et pas ouvert = en cours)
        grid.addColumn(t -> t.isFini() ? "Terminé" : (!t.isOuvert() ? "En cours" : "Préparation"))
            .setHeader("Statut");

        // --- MODIFICATION DE LA COLONNE ACTIONS ---
        grid.addComponentColumn(tournoi -> {
            // On crée un layout horizontal pour mettre les boutons côte à côte
            HorizontalLayout actionsLayout = new HorizontalLayout();
            
            // Bouton "Voir" (pour tout le monde)
            Button btnDetails = new Button(VaadinIcon.EYE.create());
            btnDetails.addThemeVariants(ButtonVariant.LUMO_TERTIARY); // Style plus léger
            btnDetails.setTooltipText("Voir les détails");
            btnDetails.addClickListener(e -> {
                btnDetails.getUI().ifPresent(ui -> 
                    ui.navigate(VueDetailsTournoi.class, tournoi.getId())
                );
            });
            actionsLayout.add(btnDetails);

            // Bouton "Supprimer" (UNIQUEMENT POUR L'ADMIN)
            if (Sessioninfo.adminConnected()) {
                Button btnDelete = new Button(VaadinIcon.TRASH.create());
                btnDelete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR); // Rouge et léger
                btnDelete.setTooltipText("Supprimer le tournoi");
                btnDelete.addClickListener(e -> {

                    afficherConfirmationSuppression(tournoi);
                });
                actionsLayout.add(btnDelete);
            }

            return actionsLayout;
        }).setHeader("Actions"); 


        updateList();
        add(grid);
    }

    private void afficherConfirmationSuppression(Tournoi tournoiToDelete) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Supprimer le tournoi ?");
        
        dialog.add(new Text("Êtes-vous sûr de vouloir supprimer définitivement le tournoi \"" 
                + tournoiToDelete.getNom() + "\" ? Toutes les données associées (matchs, résultats) seront perdues."));

        Button btnCancel = new Button("Annuler", e -> dialog.close());
        Button btnConfirm = new Button("Supprimer", e -> {
            try {
                
                Tournoi.supprimerTournoi(tournoiToDelete.getId());
                
                Notification.show("Tournoi supprimé avec succès.", 3000, Notification.Position.BOTTOM_START)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                
                // On recharge la grille pour faire disparaître la ligne supprimée
                updateList();
                dialog.close();
                
            } catch (Exception ex) {
                Notification.show("Erreur lors de la suppression : " + ex.getMessage(), 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
            }
        });
        // Style rouge pour le bouton de confirmation dangereuse
        btnConfirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        dialog.getFooter().add(btnCancel, btnConfirm);
        dialog.open();
    }

    private void updateList() {
        try {
            List<Tournoi> tournois = Tournoi.getAllTournois();
            grid.setItems(tournois);
        } catch (SQLException e) {
            Notification.show("Erreur lors du chargement : " + e.getMessage())
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}