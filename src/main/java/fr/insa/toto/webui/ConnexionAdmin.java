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
package fr.insa.toto.webui;

import com.vaadin.flow.component.button.Button;
// AJOUT DE L'IMPORT POUR H1
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import fr.insa.toto.model.Authentification;


@Route(value = "ConnexionAdmin")
@PageTitle("Connexion") 



/**
 *
 * @author vicbl
 */


public class ConnexionAdmin extends VerticalLayout {

    private LoginForm loginForm = new LoginForm();

    public ConnexionAdmin() {
        
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        
       

	add(new H1("Connexion"));

        loginForm.addLoginListener(event -> {
            String identifiant = event.getUsername();
            String mdp = event.getPassword();

            if (Authentification.ok(identifiant, mdp)) {
                loginForm.getUI().ifPresent(ui -> ui.navigate("VueAdmin"));
            } else {
                loginForm.setError(true);
            }
        });

        add(loginForm);
    }
}
/*
public class ConnexionAdmin extends VerticalLayout implements BeforeEnterObserver  {
  
    private final LoginForm login = new LoginForm(); 

	public ConnexionAdmin(){
		addClassName("ConnexionAdmin");
		setSizeFull(); 
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);

		login.setAction("Connexion"); 

		add(new H1("Connexion"), login);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		// inform the user about an authentication error
		if(beforeEnterEvent.getLocation()  
        .getQueryParameters()
        .getParameters()
        .containsKey("error")) {
            login.setError(true);
        }
	}
}
*/
/*
    public ConnexionAdmin() {
         H1 titrePage = new H1("Connexion");
         //Pour centrer tout le contenu de la page
         this.setAlignItems(Alignment.CENTER);

        TextField user = new TextField("Identifiants");
        TextField mdp = new PasswordField("Mot de passe");
        
        Button bValider = new Button("Valider");
        bValider.addClickListener(event -> {
           
                String nom = user.getValue();
                String MDP = mdp.getValue();
                
           



        });
        this.add(titrePage, user,mdp, bValider);
    }}
*/