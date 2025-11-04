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

import com.mysql.cj.xdevapi.PreparableStatement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.utils.database.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author jcardot01
 */
@Route(value = "bac")
@PageTitle("Boite a coucou")

public class BoiteACoucou extends VerticalLayout{
    private TextField tfnom;
    private TextArea taMess;
    private Button bCoucou;
    private Button bSalut;
    private HorizontalLayout hLayout;
    
    private void testUtilisateur (String Nom){
        try (Connection con = ConnectionPool.getConnection()){
            //select nom,categorie from joueur where surnom = 'toto'
            PreparedStatement st = con.prepareStatement( "select nom,categorie from joueur where surnom = ? ");
            st.setString(1, Nom);
            ResultSet resultat = st.executeQuery();
            
            if (resultat.next()){
                String categorie = resultat.getString("categorie");
                this.taMess.setValue(this.taMess.getValue() + "\n" + categorie);
              
            } else{
                
            }
        } catch(SQLException ex){
            Notification.show("Erreur : " + ex.getMessage());
        }
    }
        
    public BoiteACoucou(){
        this.tfnom = new TextField("nom");
        this.taMess = new TextArea();
        this.taMess.setWidth("75%");
        this.taMess.setHeight("20em");
        this.bCoucou = new Button("Coucou");
        this.bCoucou.addClickListener((t) -> {
            String Nom = this.tfnom.getValue();
            this.taMess.setValue(this.taMess.getValue() + "Coucou " + Nom + "\n");
        });
        this.bSalut = new Button("Salut");
        this.bSalut.getStyle().set("color", "red");
         this.bSalut.addClickListener((t) -> {
            String Nom2 = this.tfnom.getValue();
            this.taMess.setValue(this.taMess.getValue() + "Salut " + Nom2 + "\n");
        });
        this.hLayout = new HorizontalLayout(this.bCoucou, this.bSalut);
        this.add(this.tfnom, this.taMess, this.hLayout);        
        
    }
    
    
}
