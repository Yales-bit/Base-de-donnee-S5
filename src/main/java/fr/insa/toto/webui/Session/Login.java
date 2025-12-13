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
package fr.insa.toto.webui.Session;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Utilisateur;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 *
 * @author vicbl
 */
public class Login extends HorizontalLayout {
    public TextField identifiant;
    public PasswordField mdp;
    public Button login;
    
    public Login() {
    this.identifiant = new TextField("Identifiants : ");
    this.login = new Button("Se Connecter");
    this.mdp = new PasswordField("Mot de Passe");
    this.login.addClickListener((t)->{
    this.doLogin();        // A REMETRRE QUAND CE SERA FONCTIONNEL
       
    });
    this.add(this.identifiant,this.mdp,this.login);
}
    public void doLogin(){
        String identifiant = this.identifiant.getValue();
        String mdp = this.mdp.getValue();
        try (Connection con = ConnectionPool.getConnection()){
            Optional<Utilisateur> trouve = Utilisateur.findByIdentifiantMdp(con, identifiant, mdp);   
        if (trouve.isEmpty()){
            Notification.show("Identifiant ou mot de passe incorrect");
        }else{
            Sessioninfo.login(trouve.get());
            UI.getCurrent().refreshCurrentRoute(true); // permet de rafraichir la page 
        }
        
        } catch (SQLException ex){
            Notification.show("Problème"+ex.getLocalizedMessage());
            
        }
        // SUITE PROCHAINEMENT
    }
    
}
