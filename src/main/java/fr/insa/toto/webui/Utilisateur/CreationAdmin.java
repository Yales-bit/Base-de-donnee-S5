/*
 * VERSION CORRIGÉE de CreationAdmin.java
 */
package fr.insa.toto.webui.Utilisateur;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Utilisateur;
import fr.insa.toto.webui.InterfacePrincipale;
import java.sql.Connection;
import java.sql.SQLException;

@Route(value = "CreationAdmin", layout = InterfacePrincipale.class)
@PageTitle("Créer un compte")
public class CreationAdmin extends Div {

    private TextField identifiant = new TextField("Identifiant");
    private PasswordField mdp = new PasswordField("Mot de passe");
    private PasswordField confirmMdp = new PasswordField("Confirmer le mot de passe");
    private Button save = new Button("Sauvegarder le compte");

    public CreationAdmin() {
        this.setSizeFull();
        this.getStyle().set("display", "flex");
        this.getStyle().set("flex-direction", "column");
        this.getStyle().set("align-items", "center");
        this.getStyle().set("justify-content", "center");

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
        formLayout.getStyle().set("margin", "0 auto"); // Centrage horizontal des champs

        formLayout.add(this.identifiant, 2);
        formLayout.add(this.mdp, confirmMdp);
        formLayout.add(this.save, 2);

        this.add(titre, formLayout);
    }

    public void doSave() {
        try (Connection con = ConnectionPool.getConnection()) {
            String idSaisi = this.identifiant.getValue();
            String mdpSaisi = this.mdp.getValue();
            
            // Rôle par défaut = 1 (admin)
            int roleId = 1; 
            
            Utilisateur u = new Utilisateur(idSaisi, mdpSaisi, roleId);
            u.saveInDB(con);
            
            Notification.show("Succès : Utilisateur '" + idSaisi + "' créé avec le rôle " + roleId + ".");
            this.identifiant.clear(); this.mdp.clear(); this.confirmMdp.clear();

        } catch (SQLException ex) {
            Notification.show("Erreur SQL : " + ex.getLocalizedMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
             Notification.show("Erreur inattendue : " + ex.getLocalizedMessage());
        }
    }
}