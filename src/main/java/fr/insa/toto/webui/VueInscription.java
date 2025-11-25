package fr.insa.toto.webui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

@Route(value = "inscription")
@PageTitle("Inscription")


public class VueInscription extends VerticalLayout {
    public VueInscription() {

        H1 titre = new H1("Inscription");
        add(titre);
        this.setAlignItems(Alignment.CENTER);
        TextField tfSurnom = new TextField("Surnom");
        NumberField nfTaille = new NumberField("Taille (cm)");
        ComboBox<StatutSexe> cbSexe = new ComboBox<>("Sexe");
        cbSexe.setAllowCustomValue(false);
        cbSexe.setItems(StatutSexe.values());
        cbSexe.setItemLabelGenerator(statut -> statut.toString().toUpperCase());
        HorizontalLayout hLayout = new HorizontalLayout(tfSurnom, nfTaille, cbSexe);
        add(hLayout);
        Button bValider = new Button("Valider");
        bValider.addClickListener(event -> {
            try {
                String Surnom = tfSurnom.getValue();
                if (nfTaille.getValue() == null) {
                     throw new Exception("Veuillez entrer une taille valide.");
                }
                int Taille = nfTaille.getValue().intValue();
                StatutSexe Sexe = cbSexe.getValue();
                if (Sexe == null) {
                    throw new Exception("Veuillez sélectionner un sexe.");
                }
                Joueur J = new Joueur(Surnom, Sexe, Taille);
                Joueur.creerJoueur(J);
                Notification.show("Joueur créé avec succès ! " + "Surnom : " + Surnom + ", Taille : " + Taille + ", Sexe : " + Sexe.toString())
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                tfSurnom.clear(); nfTaille.clear(); cbSexe.clear();
                
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
        });
        this.add(new HorizontalLayout(tfSurnom, nfTaille, cbSexe), new HorizontalLayout(bValider, bAnnuler));

    }



}
