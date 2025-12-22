package fr.insa.toto.webui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import fr.insa.toto.webui.Session.Login;
import fr.insa.toto.webui.Session.Logout;
import fr.insa.toto.webui.Session.Sessioninfo;

@Route("")
public class InterfacePrincipale extends AppLayout {

    public InterfacePrincipale() {
        // 1. Le Menu Latéral (Drawer)
        this.addToDrawer(new Menu());
/*<<<<<<< HEAD
       // this.addToDrawer(new Button("Classement"));
        //this.addToDrawer(new Button("Participants"));
        //this.addToDrawer(new Button("Ronde"));
        //this.addToDrawer(new Button("Règle"));
        
        
        VerticalLayout menu = new VerticalLayout();
        
        //menu.add(new RouterLink("Tournoi", VueDetailsTournoi.class)); 
        menu.add(new RouterLink("Classement", VueRanking.class));
        menu.add(new RouterLink("Inscription", VueInscription.class));// 
=======
>>>>>>> origin/master+*/

        // 2. La Barre de Navigation Supérieure (Navbar)
        DrawerToggle toggle = new DrawerToggle();
        H2 title = new H2("Gestion Tournois");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");

        // Zone de droite pour le login/logout
        HorizontalLayout headerRight = new HorizontalLayout();
        headerRight.getStyle().set("margin-left", "auto").set("margin-right", "1em");

        if (Sessioninfo.userConnected()) {
            // Si connecté : Bouton de déconnexion
            headerRight.add(new Logout());
        } else {
            // Si pas connecté : Formulaire de login
           

            Button compteBtn = new Button("Compte", VaadinIcon.USER.create());

            Login login = new Login();
            login.setVisible(false);

            compteBtn.addClickListener(e -> {
            login.setVisible(!login.isVisible());
            compteBtn.setVisible(false);
    });

    headerRight.add(compteBtn, login); 
          //  headerRight.add(new Login());
        }

        // Assemblage de la navbar
        HorizontalLayout header = new HorizontalLayout(toggle, title, headerRight);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames("py-0", "px-m");

        this.addToNavbar(header);
    }
}