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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import fr.insa.toto.webui.Session.Login;
import fr.insa.toto.webui.Session.Logout;
import fr.insa.toto.webui.Session.Sessioninfo;
import com.vaadin.flow.component.avatar.AvatarVariant;

@Route("")
public class InterfacePrincipale extends AppLayout {

    public InterfacePrincipale() {
        this.addToDrawer(new Menu());
        this.addToNavbar(createHeaderContent());

        VerticalLayout welcomeLayout = new VerticalLayout();
        welcomeLayout.setSizeFull();
        welcomeLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        welcomeLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        H1 welcomeText = new H1("  Bienvenue sur TournamentMaker.io!");
        welcomeText.getStyle().set("font-size", "4em");
        welcomeText.getStyle().set("text-align", "center");

        welcomeLayout.add(welcomeText);
        setContent(welcomeLayout);
    }
    private Component createHeaderContent() {
        HorizontalLayout header = new HorizontalLayout();
        header.setId("header");
        header.setWidthFull();
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setSpacing(false);

        header.getElement().getStyle().set("background", "var(--lumo-base-color-translucent-90pct)");
        header.getElement().getStyle().set("backdrop-filter", "blur(8px)"); 
        header.getElement().getStyle().set("border-bottom", "1px solid var(--lumo-contrast-10pct)");

        header.addClassNames(
                LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.Padding.Vertical.SMALL
        );
        DrawerToggle toggle = new DrawerToggle();

        Span titleIcon = new Span(VaadinIcon.TROPHY.create());
        titleIcon.addClassName(LumoUtility.Margin.Right.SMALL);

        titleIcon.addClassName(LumoUtility.TextColor.PRIMARY);

        H1 title = new H1("TOURNAMENTMAKER.io");
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.NONE, LumoUtility.FontWeight.EXTRABOLD);
        title.getElement().getStyle().set("letter-spacing", "0.05em");

        HorizontalLayout leftSection = new HorizontalLayout(toggle, titleIcon, title);
        leftSection.setAlignItems(FlexComponent.Alignment.CENTER);


        // --- Partie Droite : Authentification ---
        Component authSection = createAuthSection();
        authSection.addClassName(LumoUtility.Margin.Left.AUTO);

        header.add(leftSection, authSection);
        return header;
    }



    private Component createAuthSection() {
        HorizontalLayout authLayout = new HorizontalLayout();
        authLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        authLayout.setSpacing(true);

        if (Sessioninfo.userConnected()) {
            // ...
            Avatar avatar = new Avatar(Sessioninfo.curUser().map(u -> u.getIdentifiant()).orElse("User"));
            avatar.addThemeVariants(AvatarVariant.LUMO_XSMALL);

            Logout logoutBtn = new Logout();
            if(logoutBtn.getChildren().findFirst().isPresent() && logoutBtn.getChildren().findFirst().get() instanceof Button) {
                 Button btn = (Button) logoutBtn.getChildren().findFirst().get();
                 btn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            }
            authLayout.add(avatar, logoutBtn);

        } else {
            // --- Cas : Utilisateur Non Connecté ---
            Button loginBtn = new Button("Se connecter", VaadinIcon.SIGN_IN.create());

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
        loginDialog.add(loginForm);
        loginDialog.setDraggable(true);
        loginDialog.setModal(true);
        loginDialog.setCloseOnEsc(true);
        loginDialog.setCloseOnOutsideClick(true);

        loginDialog.open();
    }
}