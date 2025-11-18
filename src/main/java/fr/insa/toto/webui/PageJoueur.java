package fr.insa.toto.webui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.beuvron.vaadin.utils.dataGrid.ResultSetGrid;
import fr.insa.toto.model.Joueur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 1. Convention Java : Les classes commencent par une Majuscule (PageJoueur)
@Route("joueur")
@PageTitle("Tableau des joueurs")
public class PageJoueur extends VerticalLayout { // 2. Il faut étendre un Layout (VerticalLayout)

    // Déclaration des composants
    private Grid<Joueur> gridJoueurs = new Grid<>(Joueur.class);
    private Button buttonGetJoueurs = new Button("Récupérer les joueurs");

    // 3. Tout le code d'initialisation doit être dans le constructeur
    public PageJoueur() {
        // Configuration de la grille
        // Assurez-vous que la classe Joueur a bien les getters correspondants (getSurnom, etc.)
        gridJoueurs.setColumns("surnom", "categorie", "taille"); 

        // Configuration du bouton
        buttonGetJoueurs.addClickListener(event -> {
            refreshGrid();
        });

        // 4. IMPORTANT : Ajouter les composants à l'écran
        add(buttonGetJoueurs);
    }

    private void refreshGrid() {
        /*List<Joueur> listeJoueurs = new ArrayList<>();

        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement("SELECT * FROM joueur");
            ResultSet rs = pst.executeQuery();

            // 5. Conversion : ResultSet -> List<Joueur>
            while (rs.next()) {
                // Il faut créer un objet Joueur pour chaque ligne
                // Adaptez ce constructeur selon votre classe Joueur réelle !
                String surnom = rs.getString("surnom");
                int taille = rs.getInt("taille");
                String categorie = rs.getString("categorie");

                // On suppose ici que vous avez un constructeur ou des setters
                Joueur j = new Joueur(surnom, categorie, taille);
                listeJoueurs.add(j);
            }

            // Mettre à jour la grille avec la LISTE (pas le ResultSet)
            gridJoueurs.setItems(listeJoueurs);

        } catch (SQLException e) {
            e.printStackTrace();
            Notification.show("Erreur SQL : " + e.getMessage());
        } catch (Exception e) {
            Notification.show("Erreur : " + e.getMessage());
        }*/
        try (Connection con = ConnectionPool.getConnection()){
            PreparedStatement st = con.prepareStatement( "select surnom,taille,categorie from joueur");
            ResultSetGrid g = new ResultSetGrid(st);
            this.add(g);

        } catch(SQLException ex){
            Notification.show("Erreur : " + ex.getMessage());
        }
    }
}