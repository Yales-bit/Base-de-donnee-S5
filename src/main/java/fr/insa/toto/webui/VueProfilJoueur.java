package fr.insa.toto.webui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.toto.model.Joueur;
import fr.insa.toto.model.dto.LigneHistoriqueDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Route(value = "profil")
@PageTitle("Profil")

public class VueProfilJoueur extends VerticalLayout implements HasUrlParameter<Integer> {

    private H1 surnomHeader = new H1();
    private Span infoSexe = new Span();
    private Span infoTaille = new Span();
    private Span infoNom = new Span();
    private Span infoPrenom = new Span();
    private Span infoDate = new Span();
    private Span infoAge = new Span();
    private String mois;

    // Grid pour l'historique des matchs
    private Grid<LigneHistoriqueDTO> gridHistorique = new Grid<>(LigneHistoriqueDTO.class, false);
    private H2 titreHistorique = new H2("Historique des Matchs");

    public VueProfilJoueur() {
        // Mise en page de base
        setAlignItems(Alignment.CENTER);

        // Bouton retour
        Button btnRetour = new Button("Retour à la recherche", e -> getUI().ifPresent(ui -> ui.navigate("recherche")));

        // Configuration de la grille d'historique
        configureGridHistorique();

        // Initialement on cache le titre et la grille (seront affichés si données)
        titreHistorique.setVisible(false);
        gridHistorique.setVisible(false);

        add(btnRetour, surnomHeader, new H3("Informations"), infoNom, infoPrenom, infoSexe, infoDate, infoTaille,
                infoAge);
        add(titreHistorique, gridHistorique);
    }

    /**
     * Configure les colonnes de la grille d'historique des matchs.
     */
    private void configureGridHistorique() {
        gridHistorique.addColumn(LigneHistoriqueDTO::getNomTournoi)
                .setHeader("Tournoi")
                .setSortable(true);

        gridHistorique.addColumn(LigneHistoriqueDTO::getNumeroRonde)
                .setHeader("Ronde")
                .setSortable(true);

        gridHistorique.addColumn(LigneHistoriqueDTO::getAdversaires)
                .setHeader("Adversaire(s)");

        gridHistorique.addColumn(LigneHistoriqueDTO::getScore)
                .setHeader("Score");

        // Colonne "Résultat" avec affichage coloré
        gridHistorique.addComponentColumn(ligne -> {
            Span resultat = new Span(ligne.isVictoire() ? "Victoire" : "Défaite");
            resultat.getStyle().set("color", ligne.isVictoire() ? "green" : "red");
            resultat.getStyle().set("font-weight", "bold");
            return resultat;
        }).setHeader("Résultat");

        gridHistorique.setWidth("100%");
        gridHistorique.setMaxWidth("900px");
    }

    // Cette méthode est appelée automatiquement par Vaadin quand on arrive sur la
    // page
    // avec un paramètre (ex: /profil/12)
    @Override
    public void setParameter(BeforeEvent event, Integer joueurId) {
        try {
            Joueur joueur = Joueur.getJoueurById(joueurId);
            if (joueur != null) {
                afficherJoueur(joueur);
                chargerHistorique(joueurId);
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
        infoNom.setText("Nom : " + j.getNom());
        infoPrenom.setText("Prénom : " + j.getPrenom());
        if (j.getMois() < 10) {
            mois = "0" + j.getMois();
        } else {
            mois = "" + j.getMois();
        }
        ;
        infoDate.setText("Date de naissance : " + j.getJour() + "/" + mois + "/" + j.getAnnee());

        // --- CALCUL DE L'ÂGE ---
        try {
            LocalDate dateNaissance = LocalDate.of(j.getAnnee(), j.getMois(), j.getJour());
            LocalDate aujourdhui = LocalDate.now();
            int age = Period.between(dateNaissance, aujourdhui).getYears();
            infoAge.setText("Âge : " + age + " ans");
        } catch (Exception e) {
            infoAge.setText("Âge : Inconnu (date invalide)");
        }
    }

    /**
     * Charge et affiche l'historique des matchs du joueur.
     */
    private void chargerHistorique(int joueurId) {
        try {
            List<LigneHistoriqueDTO> historique = Joueur.getHistoriqueMatchs(joueurId);

            if (historique != null && !historique.isEmpty()) {
                gridHistorique.setItems(historique);
                titreHistorique.setVisible(true);
                gridHistorique.setVisible(true);
            } else {
                // Pas d'historique : on affiche un message
                titreHistorique.setText("Historique des Matchs (aucun match joué)");
                titreHistorique.setVisible(true);
                gridHistorique.setVisible(false);
            }
        } catch (SQLException e) {
            Notification.show("Erreur lors du chargement de l'historique : " + e.getMessage());
            e.printStackTrace();
        }
    }
}