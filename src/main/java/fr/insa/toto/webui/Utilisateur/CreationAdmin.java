/*
 * VERSION CORRIGÉE de CreationAdmin.java
 */
package fr.insa.toto.webui.Utilisateur;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Utilisateur;
import fr.insa.toto.webui.InterfacePrincipale;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Route(value = "CreationAdmin", layout = InterfacePrincipale.class)
@PageTitle("Créer un compte")
public class CreationAdmin extends FormLayout {

//<<<<<<< HEAD
//public class CreationAdmin extends FormLayout {

  //  private TextField identifiant;
   // private PasswordField mdp;
    //private ComboBox<String> role;
  //  private Button save;
//=======
    private TextField identifiant = new TextField("Identifiant");
    private PasswordField mdp = new PasswordField("Mot de passe");
    private PasswordField confirmMdp = new PasswordField("Confirmer le mot de passe");
   // private ComboBox<String> role = new ComboBox<>("Rôle");
    private Button save = new Button("Sauvegarder le compte");
//>>>>>>> origin/master*/

    public CreationAdmin() {
//<<<<<<< HEAD
        this.identifiant = new TextField("Identifiants");
        this.mdp = new PasswordField("Mot de passe");
        //this.role = new ComboBox<String>("role");
      //  this.role.setItems(List.of("utilisateur", "administrateur"));
        this.save = new Button("sauvegarder");
//=======
        this.setSizeFull();
     //   this.setAlignItems(FlexComponent.Alignment.CENTER);
      //  this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

      //  this.role.setItems(List.of("utilisateur", "administrateur"));
       // this.role.setValue("utilisateur");

//>>>>>>> origin/master
        this.save.addClickListener((t) -> {
            if (!mdp.getValue().equals(confirmMdp.getValue())) {
                 Notification.show("Les mots de passe ne correspondent pas.");
                 return;
            }
            this.doSave();
        });

        H1 titre = new H1("Créer un nouveau compte");

        FormLayout formLayout = new FormLayout();
//<<<<<<< HEAD
        this.addFormRow(this.identifiant);
        this.addFormRow(this.mdp, confirmMdp);
       // this.addFormRow(this.role);
        this.addFormRow(this.save);
//=======
        formLayout.setMaxWidth("500px"); 
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
        
        formLayout.add(this.identifiant, 2);
        formLayout.add(this.mdp, confirmMdp);
   //     formLayout.add(this.role, 2);
        formLayout.add(this.save, 2);
//>>>>>>> origin/master

        this.add(titre, formLayout);
    }

    public void doSave() {
        try (Connection con = ConnectionPool.getConnection()) {
//<<<<<<< HEAD
/*
            String identifiant = this.identifiant.getValue();
            String mdp = this.mdp.getValue();
            int role = 1;*/
  /*          if (this.role.getValue() != null && this.role.getValue().equals("adminstrateur")) {
                role = 1;
        
        }*/
  
  // On conserve la possibilité de créer un utilisateur normal au cas où on ait le temps de faire des améliorations bonus
  
  //          Utilisateur u = new Utilisateur(identifiant, mdp, role);
//======
            String idSaisi = this.identifiant.getValue();
            String mdpSaisi = this.mdp.getValue();
            
            // Rôle par défaut = 1 (admin)
            int roleId = 1; 
            // CORRECTION CRITIQUE ICI : "administrateur" (avec un 'i')
           /* if (this.role.getValue() != null && this.role.getValue().equals("administrateur")) {
                roleId = 1;
            }*/
            
            // Utilisation du modèle Utilisateur de ton collègue
            Utilisateur u = new Utilisateur(idSaisi, mdpSaisi, roleId);
//>>>>>>> origin/master
            u.saveInDB(con);
            
            Notification.show("Succès : Utilisateur '" + idSaisi + "' créé avec le rôle " + 1 + ".");
            this.identifiant.clear(); this.mdp.clear(); this.confirmMdp.clear();

        } catch (SQLException ex) {
            Notification.show("Erreur SQL : " + ex.getLocalizedMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
             Notification.show("Erreur inattendue : " + ex.getLocalizedMessage());
        }
    }
}