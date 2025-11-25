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
import java.util.List;

@Route("recherche")
@PageTitle("Rechercher un joueur")
public class VueRechercheJoueur extends VerticalLayout {

    private TextField barreRecherche = new TextField("Nom du joueur");
    private Button boutonChercher = new Button("Rechercher", VaadinIcon.SEARCH.create());
    private Grid<Joueur> gridResultats = new Grid<>(Joueur.class);

    public VueRechercheJoueur() {
        setAlignItems(Alignment.CENTER);
        
        // 1. Configuration de la barre de recherche
        barreRecherche.setPlaceholder("Ex: John Doe");
        barreRecherche.setClearButtonVisible(true);
        
        // Permet de lancer la recherche avec la touche Entrée
        barreRecherche.addKeyPressListener(com.vaadin.flow.component.Key.ENTER, e -> lancerRecherche());
        boutonChercher.addClickListener(e -> lancerRecherche());

        HorizontalLayout zoneRecherche = new HorizontalLayout(barreRecherche, boutonChercher);
        zoneRecherche.setAlignItems(Alignment.BASELINE);

        // 2. Configuration de la Grille de résultats
        gridResultats.setColumns("surnom", "sexe", "taille");
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
            gridResultats.setItems(resultats);
            if (resultats.isEmpty()) {
                Notification.show("Aucun joueur trouvé.");
            }
        } catch (SQLException e) {
            Notification.show("Erreur : " + e.getMessage());
        }
    }
    
}