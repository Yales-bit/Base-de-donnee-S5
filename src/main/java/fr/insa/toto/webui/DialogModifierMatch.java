package fr.insa.toto.webui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import fr.insa.toto.model.Equipe;
import fr.insa.toto.model.Joueur;
import fr.insa.toto.model.Match;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ClickEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DialogModifierMatch extends Dialog {

    private Match match;
    
    private List<Joueur> tempJoueursA = new ArrayList<>();
    private List<Joueur> tempJoueursB = new ArrayList<>();

    
    private VerticalLayout containerA = new VerticalLayout();
    private VerticalLayout containerB = new VerticalLayout();

    // Bouton de sauvegarde
    private Button btnSauvegarder = new Button("Valider les nouvelles équipes", VaadinIcon.CHECK.create());
    private Span validationMessage = new Span(); // Pour afficher les erreurs d'équilibre

    public DialogModifierMatch(Match match) {
        this.match = match;
        setHeaderTitle("Modifier la composition des équipes");
        setWidth("850px"); 

        chargerDonneesInitiales();

        // 2. Construction de l'interface principale (deux colonnes)
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setWidthFull();
        mainLayout.setSpacing(true);
        // On aligne en haut pour que les titres soient au même niveau
        mainLayout.setAlignItems(FlexComponent.Alignment.START); 

        // Colonne Equipe A (Gauche)
        VerticalLayout layoutA = new VerticalLayout();
        layoutA.setPadding(false);
        layoutA.add(new H4("Equipe A : " + match.getEquipe1().getNom()));
        styleContainer(containerA); // Applique le style "boîte"
        layoutA.add(containerA);

        // Colonne Equipe B (Droite)
        VerticalLayout layoutB = new VerticalLayout();
        layoutB.setPadding(false);
        layoutB.add(new H4("Equipe B : " + match.getEquipe2().getNom()));
        styleContainer(containerB); // Applique le style "boîte"
        layoutB.add(containerB);

        mainLayout.add(layoutA, layoutB);
        // Répartition de l'espace 50/50
        mainLayout.setFlexGrow(1, layoutA);
        mainLayout.setFlexGrow(1, layoutB);

        add(mainLayout);

        // 3. Configuration du pied de page (boutons et messages)
        btnSauvegarder.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnSauvegarder.addClickListener(e -> sauvegarderModifications());
        btnSauvegarder.setEnabled(false); // Désactivé au départ tant qu'on n'a pas vérifié

        validationMessage.addClassName(LumoUtility.TextColor.ERROR);
        validationMessage.getElement().getStyle().set("font-size", "0.9em");
        validationMessage.getElement().getStyle().set("flex-grow", "1"); // Pousse les boutons à droite

        HorizontalLayout footerLayout = new HorizontalLayout();
        footerLayout.setWidthFull();
        footerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        
        Button btnAnnuler = new Button("Annuler", e -> this.close());
        footerLayout.add(validationMessage, btnAnnuler, btnSauvegarder);
        
        getFooter().add(footerLayout);

        // 4. Premier rendu des listes et vérification de l'équilibre
        rafraichirListesEtValidation();
    }

    private void chargerDonneesInitiales() {
        try {

            tempJoueursA.addAll(Equipe.getJoueursDeLEquipe(match.getEquipe1().getId()));
            tempJoueursB.addAll(Equipe.getJoueursDeLEquipe(match.getEquipe2().getId()));
        } catch (SQLException e) {
            Notification.show("Erreur critique de chargement : " + e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            close(); // Impossible de continuer si on ne peut pas charger les joueurs
        }
    }

    private void rafraichirListesEtValidation() {
        containerA.removeAll();
        containerB.removeAll();

        
        for (Joueur j : tempJoueursA) {
            containerA.add(creerLigneJoueur(j, VaadinIcon.ARROW_RIGHT, e -> {
                // LOGIQUE DU SWAP : Retirer de A, Ajouter à B
                tempJoueursA.remove(j);
                tempJoueursB.add(j);
                // On rafraîchit tout l'affichage pour refléter le changement
                rafraichirListesEtValidation(); 
            }));
        }

        // Remplissage visuel colonne B (Bouton flèche vers la gauche <-)
        for (Joueur j : tempJoueursB) {
            containerB.add(creerLigneJoueur(j, VaadinIcon.ARROW_LEFT, e -> {
                // LOGIQUE DU SWAP : Retirer de B, Ajouter à A
                tempJoueursB.remove(j);
                tempJoueursA.add(j);
                // On rafraîchit tout l'affichage
                rafraichirListesEtValidation(); 
            }));
        }

        // les équipes doivent être égales
        int countA = tempJoueursA.size();
        int countB = tempJoueursB.size();
        boolean equilibre = (countA == countB) && (countA > 0);

        btnSauvegarder.setEnabled(equilibre);

        if (!equilibre) {
            if (countA == 0 && countB == 0) {
                 validationMessage.setText("Erreur : Les équipes sont vides.");
            } else {
                validationMessage.setText("Déséquilibre : " + countA + " joueurs vs " + countB + " joueurs. Le nombre doit être égal pour valider.");
            }
        } else {
            // Message vert pour dire que c'est OK
            validationMessage.setText("Équipes équilibrées (" + countA + " vs " + countB + "). Vous pouvez valider.");
            validationMessage.removeClassName(LumoUtility.TextColor.ERROR);
            validationMessage.addClassName(LumoUtility.TextColor.SUCCESS);
        }
    }

    private HorizontalLayout creerLigneJoueur(Joueur j, VaadinIcon icon, ComponentEventListener<ClickEvent<Button>> clickAction) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setPadding(true);
        row.setSpacing(true);
        // Petit style "carte" pour chaque joueur
        row.getElement().getStyle().set("background-color", "var(--lumo-base-color)");
        row.getElement().getStyle().set("border-radius", "var(--lumo-border-radius-s)");
        row.getElement().getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");
        // row.getElement().getStyle().set("box-shadow", "var(--lumo-box-shadow-xs)");

        Span nomLabel = new Span(j.getPrenom() + " " + j.getNom().toUpperCase());
        nomLabel.getElement().getStyle().set("font-weight", "500");
        nomLabel.getElement().getStyle().set("font-size", "0.9em");

        Button moveButton = new Button(icon.create());
        moveButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        moveButton.setTooltipText("Déplacer ce joueur dans l'autre équipe");
        // On associe l'action passée en paramètre au clic du bouton
        moveButton.addClickListener(clickAction);

        if (icon == VaadinIcon.ARROW_RIGHT) {
            // Equipe A : Nom à gauche, bouton flèche à droite
            row.add(nomLabel, moveButton);
            row.setFlexGrow(1, nomLabel); // Le nom prend l'espace
            row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        } else {
            // Equipe B : Bouton flèche à gauche, nom à droite
            row.add(moveButton, nomLabel);
            row.setFlexGrow(1, nomLabel);
            row.setJustifyContentMode(FlexComponent.JustifyContentMode.START); // Alignés au début
        }

        return row;
    }

    // Appelé quand on clique sur "Valider"
    private void sauvegarderModifications() {
        try {
            Match.updateEquipesDuMatch(match, tempJoueursA, tempJoueursB);

            Notification.show("Équipes modifiées avec succès !")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            this.close();
            
            // IMPORTANT : Recharger la page courante pour voir les changements sur la carte du match
            com.vaadin.flow.component.UI.getCurrent().getPage().reload();

        } catch (SQLException e) {
            Notification.show("Erreur critique lors de la sauvegarde en base : " + e.getMessage(), 5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    // Petit helper pour le style des conteneurs de liste (zone grise avec scroll)
    private void styleContainer(VerticalLayout container) {
        container.setPadding(true);
        container.setSpacing(true);
        container.getElement().getStyle().set("background-color", "var(--lumo-contrast-5pct)");
        container.getElement().getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        // Hauteur fixe pour que les deux colonnes aient la même taille et que ça scrolle si trop de joueurs
        container.setHeight("400px"); 
        container.getElement().getStyle().set("overflow-y", "auto");
        container.getElement().getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");
    }
}