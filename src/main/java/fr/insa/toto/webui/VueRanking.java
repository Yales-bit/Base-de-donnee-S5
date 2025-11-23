package fr.insa.toto.webui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
// AJOUT DE L'IMPORT POUR H1
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.List;


import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.StatutSexe;
import fr.insa.toto.model.Joueur;

 @Route("ranking")
 @PageTitle("Ranking !")


public class VueRanking extends VerticalLayout {
    private Grid<Joueur> grid;
    
    public VueRanking() {
        H1 titre = new H1("Ranking !");
        add(titre);
        this.setAlignItems(Alignment.CENTER);
        setHorizontalComponentAlignment(Alignment.CENTER, titre);
        configureGrid();
        loadData();
        setSizeFull();
        add(titre, grid);
    }

    

    private void configureGrid() {
        this.grid = new Grid<>(Joueur.class, false); // 'false' pour ne pas créer les colonnes automatiquement
        grid.addColumn(joueur -> {
        List<Joueur> items = grid.getDataProvider().fetch(new com.vaadin.flow.data.provider.Query<>()).toList();
        return items.indexOf(joueur) + 1;
        }).setHeader("#").setWidth("60px").setFlexGrow(0); // Petite colonne fixe
        grid.addColumn(Joueur::getSurnom).setHeader("Surnom");
        grid.addColumn(Joueur::getTaille).setHeader("Taille (cm)");
        grid.addColumn(Joueur::getSexe).setHeader("Sexe");
        grid.addColumn(Joueur::getScoretotal).setHeader("Score total");
        grid.setSizeFull();
        // Ajout de bandes alternées pour la lisibilité
        grid.addThemeVariants(com.vaadin.flow.component.grid.GridVariant.LUMO_ROW_STRIPES);
    }


    private void loadData() {
        try {
        // Appel au backend 
        List<Joueur> joueursTries = Joueur.getClassementGeneral();
        grid.setItems(joueursTries);
        } catch (Exception e) {
            com.vaadin.flow.component.notification.Notification.show(
                "Erreur lors du chargement du classement : " + e.getMessage(),
                5000,
                com.vaadin.flow.component.notification.Notification.Position.MIDDLE)
                .addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_ERROR);
        e.printStackTrace();
        }
    }
}



    



