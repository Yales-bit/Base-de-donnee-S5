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

/**
 *
 * @author vicbl
 */
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.RouterLink;
import fr.insa.toto.webui.Session.Login;
import fr.insa.toto.webui.Session.Logout;
import fr.insa.toto.webui.Session.Sessioninfo;

@Route("")
public class InterfacePrincipale extends AppLayout {

    public InterfacePrincipale() {

        // --- MENU DE GAUCHE ---
        
        this.addToDrawer(new Menu());
        this.addToDrawer(new Button("Classement"));
        this.addToDrawer(new Button("Participants"));
        this.addToDrawer(new Button("Ronde"));
        this.addToDrawer(new Button("Règle"));
        
        
        VerticalLayout menu = new VerticalLayout();
        
        //menu.add(new RouterLink("Tournoi", VueDetailsTournoi.class)); 
        menu.add(new RouterLink("Classement", VueRanking.class));   // 


        addToDrawer(menu);
        DrawerToggle toggle = new DrawerToggle(); //permettre de replier 
        this.addToNavbar(toggle,new H2("Menu"));
        
        if (Sessioninfo.userConnected()){     // Si il y a un utilisater connecté possibilité de se déconnecter
            Div spacer = new Div();
            spacer.getStyle().set("flex-grow", "1");
            this.addToNavbar(spacer, new Logout());
            
        }else {
            //this.addToNavbar(new Login());
            Div spacer = new Div();
            spacer.getStyle().set("flex-grow", "1");

        // 🔹 Bouton Compte avec icône
             Button compteBtn = new Button("Compte", VaadinIcon.USER.create());

        // 🔹 Composant Login
            Login login = new Login();
            
            login.setVisible(false);

        // 🔹 Toggle affichage login
            compteBtn.addClickListener(e ->{
            compteBtn.setVisible(false);
            login.setVisible(!login.isVisible());
          
                    });

        this.addToNavbar(spacer, compteBtn, login);

        }
        
         
        
        
 
    }
}
/*
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

@Route("InterfacePrincipale")
public class InterfacePrincipale extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Label title = new Label("Menu");
        title.addStyleName(ValoTheme.MENU_TITLE);

        Button view1 = new Button("View 1", e -> getNavigator().navigateTo("view1"));
        view1.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);
        Button view2 = new Button("View 2", e -> getNavigator().navigateTo("view2"));
        view2.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);

        CssLayout menu = new CssLayout(title, view1, view2);
        menu.addStyleName(ValoTheme.MENU_ROOT);

        CssLayout viewContainer = new CssLayout();

        HorizontalLayout mainLayout = new HorizontalLayout(menu, viewContainer);
        mainLayout.setSizeFull();
        setContent(mainLayout);

        Navigator navigator = new Navigator(this, viewContainer);
        navigator.addView("", DefaultView.class);
        navigator.addView("view1", View1.class);
        navigator.addView("view2", View2.class);
    }

}*/