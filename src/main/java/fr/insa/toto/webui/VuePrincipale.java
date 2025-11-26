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

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.beuvron.vaadin.utils.dataGrid.ResultSetGrid;



@Route(value = "")
@PageTitle("vueprincipale")
public class VuePrincipale extends VerticalLayout {

    private final Button Admin;
    private final Button Joueur;
    private final Button Spectateur;

  
  
  
            
    public VuePrincipale() {
        this.add(new H2("Super site web"));
        
        H1 titrePage = new H1("Connexion");
        
        this.setAlignItems(Alignment.CENTER);
        
        
        TextField tfNom = new TextField("Nom du tournoi");
        this.Admin = new Button("Administrateur");
        this.Joueur = new Button("Joueur");
        this.Spectateur = new Button("Spectateur");
        
        this.Admin.addClickListener((t) -> {
          UI.getCurrent().navigate("ConnexionAdmin");  // aller à la page Admin connexion

        });
        
        this.add(Admin,Joueur,Spectateur);
        
        
        
    }

}

/*public class VuePrincipale extends VerticalLayout {
    private BoiteACoucou bac1;
    private BoiteACoucou bac2;
            
    public VuePrincipale() {
        this.add(new H2("Super site web"));
        this.bac1 = new BoiteACoucou();
        this.bac2 = new BoiteACoucou();
        this.add(this.bac1, this.bac2);
        
        try (Connection con = ConnectionPool.getConnection()){
            //select nom,categorie from joueur where surnom = 'toto'
            PreparedStatement st = con.prepareStatement( "select surnom,taille from joueur");
            ResultSetGrid g = new ResultSetGrid(st);
            this.add(g);

        } catch(SQLException ex){
            Notification.show("Erreur : " + ex.getMessage());
        }
    }

}
*/
