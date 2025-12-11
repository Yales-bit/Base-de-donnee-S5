/*
Copyright 2000- Francois de Bertrand de Beuvron

This file is part of CoursBeuvron.

CoursBeuvron is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CoursBeuvron is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CoursBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.insa.toto.webui.Utilisateur;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.insa.toto.webui.InterfacePrincipale;
import java.util.List;

/**
 *
 * @author vicbl
 */
@Route(value = "CreationAdmin", layout = InterfacePrincipale.class)
@PageTitle("Créer un compte") 

public class CreationAdmin extends FormLayout{
    
    private TextField identifiant;
    private PasswordField mdp;
    private ComboBox<String> role;
    
    
    public CreationAdmin(){
       this.identifiant = new TextField("Identifiants");
       this.mdp = new PasswordField("Mot de passe");
       this.role = new ComboBox<String>("role");
       this.role.setItems(List.of("utilisateur","administrateur"));
       
       PasswordField confirmMdp = new PasswordField("Confirmer le mot de passe");
      
            // Conteneur VerticalLayout pour centrer le formulaire
        VerticalLayout container = new VerticalLayout();
        container.setSizeFull();
        container.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        container.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Ajouter le container dans ce FormLayout (ou remplacer l'usage de 'this' par 'container' dans ton UI)
        add(container);
	add(new H1("Connexion"));
        
FormLayout formLayout = new FormLayout();
this.addFormRow(this.identifiant);
this.addFormRow(this.mdp, confirmMdp);
this.addFormRow(this.role);
    }
}
