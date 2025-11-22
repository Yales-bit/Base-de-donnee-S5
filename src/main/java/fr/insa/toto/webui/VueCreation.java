package fr.insa.toto.webui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
// AJOUT DE L'IMPORT POUR H1
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Tournoi;

@Route(value = "newtournoi")
@PageTitle("NOUVEAU TOURNOI") 

public class VueCreation extends VerticalLayout {

    public VueCreation() {

        // On crée un composant H1 (Titre principal)
        H1 titrePage = new H1("NOUVEAU TOURNOI");

        //Pour centrer tout le contenu de la page
         this.setAlignItems(Alignment.CENTER);

        TextField tfNom = new TextField("Nom du tournoi");
        TextField tfDate = new TextField("Date du tournoi");
        TextField tfNbRondes = new TextField("Nombre de rondes");
        TextField tfNbrEquipes = new TextField("Nombre d'equipes");
        TextField tfNbrJoueurs = new TextField("Nombre de joueurs par equipe");
        TextField tfNbrTerrains = new TextField("Nombre de terrains");
        TextField tfDuree = new TextField("Duree des matchs");
        
        Button bValider = new Button("Valider");
        bValider.addClickListener(event -> {
            try {
                String Nom = tfNom.getValue();
                String Date = tfDate.getValue(); 
                int NbRondes = Integer.parseInt(tfNbRondes.getValue());
                int NbrEquipes = Integer.parseInt(tfNbrEquipes.getValue());
                int NbrJoueurs = Integer.parseInt(tfNbrJoueurs.getValue());
                int NbrTerrains = Integer.parseInt(tfNbrTerrains.getValue());
                int Duree = Integer.parseInt(tfDuree.getValue());
                
                Tournoi T = new Tournoi(NbrJoueurs, NbrEquipes, Duree, NbRondes, Nom, NbrTerrains, false, false);
                
                Tournoi.creerTournoi(T);
                Notification.show("Tournoi créé !");
            } catch (NumberFormatException e) {
                 Notification.show("Erreur : Veuillez entrer des nombres valides dans les champs numériques.");
            } catch (Exception e) {
                Notification.show("Erreur : " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        Button bAnnuler = new Button("Annuler");
        HorizontalLayout hLayout = new HorizontalLayout(bValider, bAnnuler);

        this.add(titrePage, tfNom, tfDate, tfDuree, tfNbRondes, tfNbrEquipes, tfNbrJoueurs, tfNbrTerrains, hLayout);
    }
}