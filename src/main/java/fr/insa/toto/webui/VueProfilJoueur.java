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
import java.time.LocalDate;
import java.time.Period;


import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.beuvron.vaadin.utils.dataGrid.ResultSetGrid;


@Route(value = "profil")
@PageTitle("Profil") 


public class VueProfilJoueur extends VerticalLayout implements HasUrlParameter<Integer> {

    private H1 surnomHeader = new H1();
    private Span infoSexe = new Span();
    private Span infoTaille = new Span();
    private Span infoNom = new Span();
    private Span infoPrenom = new Span();
    private Span infoJour = new Span();
    private Span infoMois = new Span();
    private Span infoAnnee = new Span();
    private Span infoDate = new Span();
    private Span infoAge = new Span();
    private String mois;

    
    public VueProfilJoueur() {
        // Mise en page de base
        setAlignItems(Alignment.CENTER);
        
        // Bouton retour
        Button btnRetour = new Button("Retour à la recherche", e -> 
            getUI().ifPresent(ui -> ui.navigate("recherche"))
        );

        add(btnRetour, surnomHeader, new H3("Informations"),infoNom, infoPrenom, infoSexe, infoJour, infoMois, infoAnnee, infoTaille, infoDate, infoAge);
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
        infoNom.setText("Nom : " + j.getNom());
        infoPrenom.setText("Prénom : " + j.getPrenom());
        if(j.getMois()<10){ mois = "0"+j.getMois();}else{mois = ""+j.getMois();};
        infoDate.setText("Date de naissance : " + j.getJour() + "/" + mois + "/" + j.getAnnee());

        // --- CALCUL DE L'ÂGE ---
        try {
            // 1. On crée la date de naissance à partir des 3 champs du joueur
            LocalDate dateNaissance = LocalDate.of(j.getAnnee(), j.getMois(), j.getJour());
            
            // 2. On récupère la date d'aujourd'hui
            LocalDate aujourdhui = LocalDate.now();
            
            // 3. On calcule la période entre les deux dates et on extrait le nombre d'années
            int age = Period.between(dateNaissance, aujourdhui).getYears();
            
            // 4. On affiche l'âge (en supposant que vous avez créé le composant infoAge)
            infoAge.setText("Âge : " + age + " ans");
            
        } catch (Exception e) {
            // Sécurité : si les données de date dans le joueur sont invalides (ex: 31 février), 
            // LocalDate.of lancera une erreur. On affiche "inconnu" dans ce cas.
            infoAge.setText("Âge : Inconnu (date invalide)");
        }
        
        
        // Tu pourras ajouter ici l'historique des matchs plus tard !
    }
}