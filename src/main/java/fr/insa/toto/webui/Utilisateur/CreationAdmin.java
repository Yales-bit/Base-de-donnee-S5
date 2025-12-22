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
public class CreationAdmin extends VerticalLayout {

    private TextField identifiant = new TextField("Identifiant");
    private PasswordField mdp = new PasswordField("Mot de passe");
    private PasswordField confirmMdp = new PasswordField("Confirmer le mot de passe");
    private ComboBox<String> role = new ComboBox<>("Rôle");
    private Button save = new Button("Sauvegarder le compte");

    public CreationAdmin() {
        this.setSizeFull();
        this.setAlignItems(FlexComponent.Alignment.CENTER);
        this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        this.role.setItems(List.of("utilisateur", "administrateur"));
        this.role.setValue("utilisateur");

        this.save.addClickListener((t) -> {
            if (!mdp.getValue().equals(confirmMdp.getValue())) {
                 Notification.show("Les mots de passe ne correspondent pas.");
                 return;
            }
            this.doSave();
        });

        H1 titre = new H1("Créer un nouveau compte");

        FormLayout formLayout = new FormLayout();
        formLayout.setMaxWidth("500px"); 
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
        
        formLayout.add(this.identifiant, 2);
        formLayout.add(this.mdp, confirmMdp);
        formLayout.add(this.role, 2);
        formLayout.add(this.save, 2);

        this.add(titre, formLayout);
    }

    public void doSave() {
        try (Connection con = ConnectionPool.getConnection()) {
            String idSaisi = this.identifiant.getValue();
            String mdpSaisi = this.mdp.getValue();
            
            // Rôle par défaut = 2 (utilisateur)
            int roleId = 2; 
            // CORRECTION CRITIQUE ICI : "administrateur" (avec un 'i')
            if (this.role.getValue() != null && this.role.getValue().equals("administrateur")) {
                roleId = 1;
            }
            
            // Utilisation du modèle Utilisateur de ton collègue
            Utilisateur u = new Utilisateur(idSaisi, mdpSaisi, roleId);
            u.saveInDB(con);
            
            Notification.show("Succès : Utilisateur '" + idSaisi + "' créé avec le rôle " + this.role.getValue() + ".");
            this.identifiant.clear(); this.mdp.clear(); this.confirmMdp.clear();

        } catch (SQLException ex) {
            Notification.show("Erreur SQL : " + ex.getLocalizedMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
             Notification.show("Erreur inattendue : " + ex.getLocalizedMessage());
        }
    }
}