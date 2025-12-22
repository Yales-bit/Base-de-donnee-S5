package fr.insa.toto.webui;

import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import fr.insa.toto.webui.Utilisateur.CreationAdmin;

public class Menu extends SideNav {
    public Menu(){
        setLabel("Navigation");
        // Pages principales
        SideNavItem tournois = new SideNavItem("Tournois", VueListeTournois.class);
        SideNavItem joueurs = new SideNavItem("Joueurs");
        joueurs.addItem(new SideNavItem("Rechercher", VueRechercheJoueur.class));
        joueurs.addItem(new SideNavItem("Nouveau Joueur", VueInscription.class));

        // Administration
        SideNavItem admin = new SideNavItem("Administration");
        admin.addItem(new SideNavItem("Créer Utilisateur", CreationAdmin.class));

        this.addItem(tournois, joueurs, admin);
    }
}