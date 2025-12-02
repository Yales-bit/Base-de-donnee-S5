package fr.insa.toto.webui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.toto.model.Tournoi;

@Route(value = "nouveau-tournoi")
@PageTitle("Création Tournoi")
public class VueCreation extends VerticalLayout {

    // Utilisation de IntegerField au lieu de TextField pour les nombres (plus sûr)
    private TextField tfNom = new TextField("Nom du tournoi");
    private IntegerField tfNbRondes = new IntegerField("Nombre de rondes");
    private IntegerField tfNbrEquipes = new IntegerField("Nombre d'équipes (estimé)");
    private IntegerField tfNbrJoueurs = new IntegerField("Joueurs par équipe");
    private IntegerField tfNbrTerrains = new IntegerField("Terrains disponibles");
    private IntegerField tfDuree = new IntegerField("Durée matchs (min)");

    private Button bValider = new Button("Créer le tournoi");
    private Button bAnnuler = new Button("Annuler");

    public VueCreation() {
        setAlignItems(Alignment.CENTER);
        add(new H2("Nouveau Tournoi"));

        // Configuration des champs
        tfNom.setWidth("300px");
        tfNbRondes.setMin(1); tfNbRondes.setValue(3);
        tfNbrJoueurs.setMin(1); tfNbrJoueurs.setValue(2); // Double par défaut
        tfNbrTerrains.setMin(1);
        
        // Style des boutons
        bValider.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        bAnnuler.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // --- ACTION VALIDER ---
        bValider.addClickListener(event -> {
            try {
                // 1. Récupération et conversion
                String nom = tfNom.getValue();
                int nbRondes = tfNbRondes.getValue() != null ? tfNbRondes.getValue() : 0;
                int nbrEquipes = tfNbrEquipes.getValue() != null ? tfNbrEquipes.getValue() : 0;
                int nbrJoueurs = tfNbrJoueurs.getValue() != null ? tfNbrJoueurs.getValue() : 0;
                int nbrTerrains = tfNbrTerrains.getValue() != null ? tfNbrTerrains.getValue() : 0;
                int duree = tfDuree.getValue() != null ? tfDuree.getValue() : 0;

                // 2. Création de l'objet (Constructeur sans ID)
                Tournoi t = new Tournoi(nbrJoueurs, nbrEquipes, duree, nbRondes, nom, nbrTerrains, false, false);

                // 3. Sauvegarde en BDD
                Tournoi.creerTournoi(t);
                
                Notification.show("Tournoi créé avec succès !", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                // 4. REDIRECTION vers la liste
                getUI().ifPresent(ui -> ui.navigate(VueListeTournois.class));

            } catch (Exception e) {
                Notification.show("Erreur : " + e.getMessage(), 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        // --- ACTION ANNULER ---
        bAnnuler.addClickListener(e -> {
             getUI().ifPresent(ui -> ui.navigate(VueListeTournois.class));
        });

        HorizontalLayout boutons = new HorizontalLayout(bValider, bAnnuler);
        
        // Formulaire
        VerticalLayout form = new VerticalLayout(tfNom, tfNbRondes, tfNbrEquipes, tfNbrJoueurs, tfNbrTerrains, tfDuree, boutons);
        form.setAlignItems(Alignment.CENTER);
        
        add(form);
    }
}