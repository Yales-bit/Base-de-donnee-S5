package fr.insa.toto.webui;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import fr.insa.toto.webui.Session.Sessioninfo;
import fr.insa.toto.webui.Utilisateur.CreationAdmin;

public class Menu extends SideNav {
    public Menu(){
        SideNavItem accueil = new SideNavItem("Accueil", VueListeTournois.class);
        accueil.setPrefixComponent(new Icon(VaadinIcon.HOME));
       // SideNavItem utilisateur = new SideNavItem("Utilisateur");
       // SideNavItem creationadmin = new SideNavItem("Création Administrateur",CreationAdmin.class);
      //  utilisateur.addItem(creationadmin);
       // SideNavItem tournoi = new SideNavItem("Tournoi",VueListeTournois.class);
        this.addItem(accueil);
        
       
       
        
        setLabel("Navigation");
        // Pages principales
        SideNavItem tournois = new SideNavItem("Tournois", VueListeTournois.class);
        SideNavItem joueurs = new SideNavItem("Joueurs");
        joueurs.addItem(new SideNavItem("Rechercher", VueRechercheJoueur.class));
        joueurs.addItem(new SideNavItem("Nouveau Joueur", VueInscription.class));

        // Administration
        if(Sessioninfo.adminConnected()){
        SideNavItem admin = new SideNavItem("Administration");
        admin.addItem(new SideNavItem("Créer Utilisateur", CreationAdmin.class));
        this.addItem(admin);
        }
        this.addItem(tournois, joueurs);
    }
}
