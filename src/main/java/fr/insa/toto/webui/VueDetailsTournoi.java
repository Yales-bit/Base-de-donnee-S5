package fr.insa.toto.webui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.toto.model.Tournoi;

import java.sql.SQLException;

@Route("tournoi") // L'URL sera .../tournoi/ID
@PageTitle("Détails du Tournoi")
public class VueDetailsTournoi extends VerticalLayout implements HasUrlParameter<Integer> {

    private H2 titreTournoi = new H2();
    private Span statusBadge = new Span();
    
    // Informations détaillées
    private Span infoRondes = new Span();
    private Span infoTerrains = new Span();
    private Span infoEquipes = new Span();
    private Span infoDuree = new Span();

    private Button btnRetour = new Button("Retour liste", VaadinIcon.ARROW_LEFT.create());

    public VueDetailsTournoi() {
        setAlignItems(Alignment.CENTER);

        // Configuration du bouton retour
        btnRetour.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(VueListeTournois.class)));
        
        // Mise en page des infos
        VerticalLayout infosLayout = new VerticalLayout(infoRondes, infoTerrains, infoEquipes, infoDuree);
        infosLayout.setAlignItems(Alignment.CENTER);
        infosLayout.setSpacing(false);

        add(btnRetour, titreTournoi, statusBadge, new H4("Paramètres"), infosLayout);
    }

    @Override
    public void setParameter(BeforeEvent event, Integer tournoiId) {
        try {
            Tournoi t = Tournoi.getTournoiById(tournoiId);
            if (t != null) {
                afficherTournoi(t);
            } else {
                titreTournoi.setText("Tournoi introuvable");
            }
        } catch (SQLException e) {
            titreTournoi.setText("Erreur : " + e.getMessage());
        }
    }

    private void afficherTournoi(Tournoi t) {
        titreTournoi.setText(t.getNom());
        
        // Gestion du statut (Ouvert/Fini)
        String statut = "En préparation";
        String couleur = "grey";
        if (t.isFini()) {
            statut = "Terminé";
            couleur = "red";
        } else if (t.isOuvert()) {
            statut = "En cours";
            couleur = "green";
        }
        
        statusBadge.setText(statut);
        statusBadge.getElement().getStyle().set("background-color", couleur);
        statusBadge.getElement().getStyle().set("color", "white");
        statusBadge.getElement().getStyle().set("padding", "5px 10px");
        statusBadge.getElement().getStyle().set("border-radius", "10px");

        // Remplissage des infos
        infoRondes.setText(t.getNbrRondes() + " rondes prévues");
        infoTerrains.setText(t.getNbrTerrains() + " terrains disponibles");
        infoEquipes.setText(t.getNbrJoueursParEquipe() + " joueurs par équipe");
        infoDuree.setText("Matchs de " + t.getDureeMatch() + " minutes");
    }
}