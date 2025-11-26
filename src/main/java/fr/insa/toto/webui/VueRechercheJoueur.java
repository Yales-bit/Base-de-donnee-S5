package fr.insa.toto.webui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.component.icon.VaadinIcon;
import fr.insa.toto.model.Joueur;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Route("recherche")
@PageTitle("Rechercher un joueur")
public class VueRechercheJoueur extends VerticalLayout {

    private TextField barreRecherche = new TextField("Surnom, prenom ou nom du joueur");
    private Button boutonChercher = new Button("Rechercher", VaadinIcon.SEARCH.create());
    private Grid<Joueur> gridResultats = new Grid<>(Joueur.class);

    public VueRechercheJoueur() {
        setAlignItems(Alignment.CENTER);
        
        // 1. Configuration de la barre de recherche
        barreRecherche.setPlaceholder("Ex: 'Spiderman'");
        barreRecherche.setClearButtonVisible(true);
        
        // Permet de lancer la recherche avec la touche Entrée
        barreRecherche.addKeyPressListener(com.vaadin.flow.component.Key.ENTER, e -> lancerRecherche());
        boutonChercher.addClickListener(e -> lancerRecherche());

        HorizontalLayout zoneRecherche = new HorizontalLayout(barreRecherche, boutonChercher);
        zoneRecherche.setAlignItems(Alignment.BASELINE);

        // 2. Configuration de la Grille de résultats
        gridResultats.setColumns("surnom", "prenom", "nom", "sexe", "taille");
        gridResultats.addComponentColumn(joueur -> {
            Button b = new Button("Voir profil", VaadinIcon.EYE.create());
            b.addClickListener(e -> {
                // NAVIGATION VERS LA PAGE PROFIL AVEC L'ID
                b.getUI().ifPresent(ui -> ui.navigate(VueProfilJoueur.class, joueur.getId()));
            });
            return b;
        });

        add(zoneRecherche, gridResultats);
    }

    private void lancerRecherche() {
    String texte = barreRecherche.getValue();
    if (texte == null || texte.isEmpty()) {
        Notification.show("Veuillez entrer un nom");
        return;
    }

    try {
        List<Joueur> resultats = Joueur.rechercherParSurnom(texte);
        
        if (!resultats.isEmpty()) {
            gridResultats.setItems(resultats);
        } else {
            List<Joueur> resultats2 = new ArrayList<>(); 
            try {
                resultats2 = Joueur.rechercherParPrenom(texte);
            } catch (SQLException e) {
                Notification.show("Erreur lors de la recherche par prénom : " + e.getMessage());
            }
            if (!resultats2.isEmpty()) {
                gridResultats.setItems(resultats2);
            } else {
                try {
                    List<Joueur> resultats3 = Joueur.rechercherParNom(texte);
                    gridResultats.setItems(resultats3);
                    if (resultats3.isEmpty()) {
                         Notification.show("Aucun joueur trouvé pour : " + texte);
                    }
                } catch (SQLException e) {
                    Notification.show("Erreur lors de la recherche par nom : " + e.getMessage());
                }
            }
        }
    } catch (SQLException e) {
        Notification.show("Erreur lors de la recherche par surnom : " + e.getMessage());
    }
}
    
}