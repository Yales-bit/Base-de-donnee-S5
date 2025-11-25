package fr.insa.toto.webui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.toto.model.Joueur;
import fr.insa.beuvron.vaadin.utils.VaadinUtils; // Si tu veux utiliser tes utilitaires
import java.awt.Color;
import java.sql.SQLException;


import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.beuvron.vaadin.utils.dataGrid.ResultSetGrid;


@Route(value = "profil")
@PageTitle("Profil") 


public class VueProfilJoueur extends VerticalLayout implements HasUrlParameter<Integer> {

    private H1 surnomHeader = new H1();
    private Span infoSexe = new Span();
    private Span infoTaille = new Span();
    
    public VueProfilJoueur() {
        // Mise en page de base
        setAlignItems(Alignment.CENTER);
        
        // Bouton retour
        Button btnRetour = new Button("Retour à la recherche", e -> 
            getUI().ifPresent(ui -> ui.navigate("recherche"))
        );

        add(btnRetour, surnomHeader, new H3("Informations"), infoSexe, infoTaille);
    }

    // Cette méthode est appelée automatiquement par Vaadin quand on arrive sur la page
    // avec un paramètre (ex: /profil/12)
    @Override
    public void setParameter(BeforeEvent event, Integer joueurId) {
        try {
            Joueur joueur = Joueur.getJoueurById(joueurId);
            if (joueur != null) {
                afficherJoueur(joueur);
            } else {
                surnomHeader.setText("Joueur introuvable");
            }
        } catch (SQLException e) {
            surnomHeader.setText("Erreur BDD : " + e.getMessage());
        }
    }

    private void afficherJoueur(Joueur j) {
        surnomHeader.setText(j.getSurnom());
        infoSexe.setText("Sexe : " + (j.getSexe() != null ? j.getSexe().toString() : "?"));
        infoTaille.setText("Taille : " + j.getTaille() + " cm");
        
        // Tu pourras ajouter ici l'historique des matchs plus tard !
    }
}