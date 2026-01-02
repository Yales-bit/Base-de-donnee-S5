package fr.insa.toto.webui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import fr.insa.toto.webui.Session.Login;
import fr.insa.toto.webui.Session.Logout;
import fr.insa.toto.webui.Session.Sessioninfo;
import com.vaadin.flow.component.avatar.AvatarVariant;

@Route("")
public class InterfacePrincipale extends AppLayout {

    public InterfacePrincipale() {
        // 1. Configuration du Menu Latéral (Drawer)
        // On suppose que la classe 'Menu' est définie ailleurs et fonctionne.
        this.addToDrawer(new Menu());

        // 2. Configuration de la Barre de Navigation Supérieure (Navbar)
        this.addToNavbar(createHeaderContent());
    }

    /*private Component createHeaderContent() {
        HorizontalLayout header = new HorizontalLayout();
        header.setId("header");
        header.setWidthFull();
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setSpacing(false);
        header.addClassNames(
                LumoUtility.Background.TRANSPARENT, // 
                LumoUtility.TextColor.PRIMARY, // Texte blanc pour contraster
                LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.Padding.Vertical.SMALL,
                LumoUtility.BoxShadow.SMALL
        );

        // --- Partie Gauche : Toggle + Titre ---
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassName(LumoUtility.TextColor.PRIMARY_CONTRAST);

        // Icône décorative
        VaadinIcon.TROPHY.create();
        Span titleIcon = new Span(VaadinIcon.TROPHY.create());
        titleIcon.addClassName(LumoUtility.Margin.Right.SMALL);

        H1 title = new H1("TOURNOIMAKER.io");
        // H1 pour la sémantique, mais stylisé plus petit pour rentrer dans le header
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE, LumoUtility.FontWeight.BOLD);

        HorizontalLayout leftSection = new HorizontalLayout(toggle, titleIcon, title);
        leftSection.setAlignItems(FlexComponent.Alignment.CENTER);


        // --- Partie Droite : Authentification ---
        Component authSection = createAuthSection();
        // Pousse la section de droite au bout
        authSection.addClassName(LumoUtility.Margin.Left.AUTO);

        header.add(leftSection, authSection);
        return header;
    }*/
   // DANS InterfacePrincipale.java

    private Component createHeaderContent() {
        HorizontalLayout header = new HorizontalLayout();
        header.setId("header");
        header.setWidthFull();
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setSpacing(false);

        // --- AMÉLIORATION UI : Style "Verre Dépoli" ---
        // On retire le fond transparent et l'ombre simple.
        // On applique un style CSS personnalisé pour l'effet translucide.
        header.getElement().getStyle().set("background", "var(--lumo-base-color-translucent-90pct)");
        header.getElement().getStyle().set("backdrop-filter", "blur(8px)"); // Effet de flou derrière
        header.getElement().getStyle().set("border-bottom", "1px solid var(--lumo-contrast-10pct)");
        // header.addClassName(LumoUtility.BoxShadow.SMALL); // On peut garder ou enlever l'ombre selon le goût

        header.addClassNames(
                // Plus besoin de Background.TRANSPARENT ni de TextColor.PRIMARY_CONTRAST ici
                LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.Padding.Vertical.SMALL
        );
        // -----------------------------------------------

        // --- Partie Gauche : Toggle + Titre ---
        DrawerToggle toggle = new DrawerToggle();
        // SUPPRESSION : toggle.addClassName(LumoUtility.TextColor.PRIMARY_CONTRAST);
        // Le toggle reprendra automatiquement la couleur de texte par défaut (foncée).

        // Icône décorative
        Span titleIcon = new Span(VaadinIcon.TROPHY.create());
        titleIcon.addClassName(LumoUtility.Margin.Right.SMALL);
        // La couleur de l'icône trophée peut être la couleur primaire pour ressortir
        titleIcon.addClassName(LumoUtility.TextColor.PRIMARY);

        H1 title = new H1("TOURNAMENTMAKER.io");
        // Style plus "branding" : un peu plus grand, plus gras, et en majuscules
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.NONE, LumoUtility.FontWeight.EXTRABOLD);
        // Optionnel : un léger espacement entre les lettres pour un look moderne
        title.getElement().getStyle().set("letter-spacing", "0.05em");

        HorizontalLayout leftSection = new HorizontalLayout(toggle, titleIcon, title);
        leftSection.setAlignItems(FlexComponent.Alignment.CENTER);


        // --- Partie Droite : Authentification ---
        Component authSection = createAuthSection();
        authSection.addClassName(LumoUtility.Margin.Left.AUTO);

        header.add(leftSection, authSection);
        return header;
    }

    // DANS createAuthSection() : PETITE MODIFICATION NECESSAIRE

    /*private Component createAuthSection() {
        // ... début de la méthode ...

        if (Sessioninfo.userConnected()) {
            // ...
            Avatar avatar = new Avatar(Sessioninfo.curUser().map(u -> u.getIdentifiant()).orElse("User"));
            avatar.addThemeVariants(AvatarVariant.LUMO_XSMALL);
            // SUR FOND CLAIR : On veut un avatar coloré par défaut, pas transparent.
            // SUPPRESSION : avatar.getElement().getStyle().set("background-color", "rgba(255,255,255, 0.2)");
            // Vaadin gère automatiquement une couleur d'avatar basée sur le nom, c'est très bien.

            Logout logoutBtn = new Logout();
            if(logoutBtn.getChildren().findFirst().isPresent() && logoutBtn.getChildren().findFirst().get() instanceof Button) {
                 Button btn = (Button) logoutBtn.getChildren().findFirst().get();
                 // SUR FOND CLAIR : On veut un bouton normal (primaire ou tertiaire), pas CONTRAST (blanc).
                 // SUPPRESSION : btn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
                 // AJOUT : Un style plus léger pour la déconnexion
                 btn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            }
            authLayout.add(avatar, logoutBtn);

        } else {
            // --- Cas : Utilisateur Non Connecté ---
            Button loginBtn = new Button("Se connecter", VaadinIcon.SIGN_IN.create());
            // SUR FOND CLAIR : Le bouton primaire standard (bleu par défaut) ressortira très bien.
            // SUPPRESSION : loginBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_PRIMARY);
            // AJOUT : Juste le style primaire
            loginBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            loginBtn.addClickListener(e -> openLoginDialog());
            authLayout.add(loginBtn);
        }
        return authLayout;
    }*/

    private Component createAuthSection() {
        HorizontalLayout authLayout = new HorizontalLayout();
        authLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        authLayout.setSpacing(true);

        if (Sessioninfo.userConnected()) {
            // ...
            Avatar avatar = new Avatar(Sessioninfo.curUser().map(u -> u.getIdentifiant()).orElse("User"));
            avatar.addThemeVariants(AvatarVariant.LUMO_XSMALL);
            // SUR FOND CLAIR : On veut un avatar coloré par défaut, pas transparent.
            // SUPPRESSION : avatar.getElement().getStyle().set("background-color", "rgba(255,255,255, 0.2)");
            // Vaadin gère automatiquement une couleur d'avatar basée sur le nom, c'est très bien.

            Logout logoutBtn = new Logout();
            if(logoutBtn.getChildren().findFirst().isPresent() && logoutBtn.getChildren().findFirst().get() instanceof Button) {
                 Button btn = (Button) logoutBtn.getChildren().findFirst().get();
                 // SUR FOND CLAIR : On veut un bouton normal (primaire ou tertiaire), pas CONTRAST (blanc).
                 // SUPPRESSION : btn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
                 // AJOUT : Un style plus léger pour la déconnexion
                 btn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            }
            authLayout.add(avatar, logoutBtn);

        } else {
            // --- Cas : Utilisateur Non Connecté ---
            Button loginBtn = new Button("Se connecter", VaadinIcon.SIGN_IN.create());
            // SUR FOND CLAIR : Le bouton primaire standard (bleu par défaut) ressortira très bien.
            // SUPPRESSION : loginBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_PRIMARY);
            // AJOUT : Juste le style primaire
            loginBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            loginBtn.addClickListener(e -> openLoginDialog());
            authLayout.add(loginBtn);
        }
        return authLayout;
    }


    /**
     * Ouvre une fenêtre modale propre contenant le formulaire de login.
     */
    private void openLoginDialog() {
        Dialog loginDialog = new Dialog();
        loginDialog.setHeaderTitle("Connexion");

        Login loginForm = new Login();
        // Le formulaire Login doit idéalement gérer la fermeture du dialog en cas de succès,
        // ou on peut ajouter un listener ici si Login lance un événement.
        // Pour l'instant, on l'ajoute simplement.

        loginDialog.add(loginForm);
        loginDialog.setDraggable(true);
        loginDialog.setModal(true);
        loginDialog.setCloseOnEsc(true);
        loginDialog.setCloseOnOutsideClick(true);

        loginDialog.open();
    }
}