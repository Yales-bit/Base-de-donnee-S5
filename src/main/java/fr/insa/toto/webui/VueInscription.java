package fr.insa.toto.webui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.checkerframework.checker.units.qual.t;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
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

import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.StatutSexe;
import fr.insa.toto.model.Joueur;
import fr.insa.toto.model.Enum_Mois;

@Route(value = "inscription", layout = InterfacePrincipale.class)
@PageTitle("Inscription")


public class VueInscription extends VerticalLayout {
    public VueInscription() {

        H1 titre = new H1("Inscription");
        add(titre);
        this.setAlignItems(Alignment.CENTER);
        TextField tfNom = new TextField("Nom");
        TextField tfPrenom = new TextField("Prenom");
        NumberField nfJour = new NumberField("Jour de naissance");
        ComboBox<Enum_Mois> cbMois = new ComboBox<>("Mois de naissance");
        cbMois.setAllowCustomValue(false);
        cbMois.setItems(Enum_Mois.values());
        cbMois.setItemLabelGenerator(mois -> mois.toString().toUpperCase());
        NumberField nfMois = new NumberField("Mois de naissance");
        NumberField nfAnnee = new NumberField("Année de naissance");
        TextField tfSurnom = new TextField("Surnom");
        NumberField nfTaille = new NumberField("Taille (cm)");
        ComboBox<StatutSexe> cbSexe = new ComboBox<>("Sexe");
        cbSexe.setAllowCustomValue(false);
        cbSexe.setItems(StatutSexe.values());
        cbSexe.setItemLabelGenerator(statut -> statut.toString().toUpperCase());
        HorizontalLayout hLayout = new HorizontalLayout(tfNom, tfPrenom, tfSurnom);
        HorizontalLayout hLayout2 = new HorizontalLayout(nfJour, cbMois, nfAnnee);
        HorizontalLayout hLayout3 = new HorizontalLayout(nfTaille, cbSexe);
        add(hLayout, hLayout2, hLayout3);
        Button bValider = new Button("Valider");
        bValider.addClickListener(event -> {
            try {
                String Surnom = tfSurnom.getValue();
                if (nfTaille.getValue() == null) {
                     throw new Exception("Veuillez entrer une taille valide.");
                }
                String Nom = tfNom.getValue();
                if (Nom == null) {
                    throw new Exception("Veuillez entrer un nom valide.");
                }
                String Prenom = tfPrenom.getValue();
                if (Prenom == null) {
                    throw new Exception("Veuillez entrer un prenom valide.");
                }
                int Jour = nfJour.getValue().intValue();
                if (Jour < 1 || Jour > 31) {
                    throw new Exception("Veuillez entrer un jour de naissance valide.");
                }
                int Annee = nfAnnee.getValue().intValue();
                if (Annee < 1900 || Annee > 2100) {
                    throw new Exception("Veuillez entrer une année de naissance valide.");
                }
                int Taille = nfTaille.getValue().intValue();
                if (Taille < 50 || Taille > 250) {
                    throw new Exception("Veuillez entrer une taille valide.");
                }
                int Mois = cbMois.getValue().getNumero();
                if (Mois < 1 || Mois > 12) {
                    throw new Exception("cb.mois.getnumero a vraiment planté");
                }
                StatutSexe Sexe = cbSexe.getValue();
                if (Sexe == null) {
                    throw new Exception("Veuillez sélectionner un sexe.");
                }
                Joueur J = new Joueur(Surnom, Sexe, Taille, Nom, Prenom, Mois, Jour, Annee);
                Joueur.creerJoueur(J);
                Notification.show("Joueur créé avec succès ! " + "Surnom : " + Surnom + ", Taille : " + Taille + ", Sexe : " + Sexe.toString() + ", Nom : " + Nom + ", Prenom : " + Prenom + ", Mois : " + Mois + ", Jour : " + Jour + ", Annee : " + Annee)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                tfSurnom.clear(); nfTaille.clear(); cbSexe.clear();          
                tfNom.clear(); tfPrenom.clear(); nfJour.clear(); cbMois.clear(); nfAnnee.clear();
                
            } catch (NumberFormatException e) {
                 Notification.show("Erreur : Veuillez entrer des nombres valides dans les champs numériques.");
            } catch (Exception e) {
                Notification.show("Erreur : " + e.getMessage());
                e.printStackTrace();
            }        
        });
        Button bAnnuler = new Button("Annuler");
        bAnnuler.addClickListener(event -> {
            tfSurnom.clear();
            nfTaille.clear();
            cbSexe.clear();
            tfNom.clear();
            tfPrenom.clear();
            nfJour.clear();
            cbMois.clear();
            nfAnnee.clear();
        });
        this.add(new HorizontalLayout(bValider, bAnnuler));

    }



}
