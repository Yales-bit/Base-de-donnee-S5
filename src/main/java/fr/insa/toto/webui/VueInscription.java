package fr.insa.toto.webui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import fr.insa.toto.model.Enum_Mois;
import fr.insa.toto.model.Joueur;
import fr.insa.toto.model.StatutSexe;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Route(value = "inscription", layout = InterfacePrincipale.class)
@PageTitle("Inscription")
public class VueInscription extends VerticalLayout {

    // Déclaration des champs au niveau de la classe pour y accéder dans les deux boutons
    private TextField tfNom = new TextField("Prenom"); //inversion
    private TextField tfPrenom = new TextField("Nom");
    private TextField tfSurnom = new TextField("Surnom");

    // MODIFICATION ICI : Utilisation de ComboBox<Integer> au lieu de NumberField
    private ComboBox<Integer> cbJour = new ComboBox<>("Jour");
    private ComboBox<Enum_Mois> cbMois = new ComboBox<>("Mois");
    private ComboBox<Integer> cbAnnee = new ComboBox<>("Année");

    private NumberField nfTaille = new NumberField("Taille (cm)");
    private ComboBox<StatutSexe> cbSexe = new ComboBox<>("Sexe");

    public VueInscription() {
        this.setAlignItems(Alignment.CENTER);
        H1 titre = new H1("Inscription");
        add(titre);

        // --- CONFIGURATION DES COMBOBOX DATE ---

        // 1. JOURS : Liste de 1 à 31
        List<Integer> jours = IntStream.rangeClosed(1, 31).boxed().collect(Collectors.toList());
        cbJour.setItems(jours);
        cbJour.setPlaceholder("JJ");
        cbJour.setWidth("80px"); // Taille ajustée pour faire propre

        // 2. MOIS (Configuration existante)
        cbMois.setAllowCustomValue(false);
        cbMois.setItems(Enum_Mois.values());
        cbMois.setItemLabelGenerator(mois -> mois.toString().toUpperCase());
        cbMois.setPlaceholder("MM");
        cbMois.setWidth("120px");

        // 3. ANNÉES : Liste de (Année actuelle - 100 ans) à (Année actuelle)
        int anneeCourante = LocalDate.now().getYear();
        List<Integer> annees = new ArrayList<>();
        // On affiche du plus récent au plus ancien pour que ce soit plus pratique
        for (int i = anneeCourante; i >= anneeCourante - 100; i--) {
            annees.add(i);
        }
        cbAnnee.setItems(annees);
        cbAnnee.setPlaceholder("AAAA");
        cbAnnee.setWidth("100px");

        // ---------------------------------------

        // Configuration des NumberField
        nfTaille.setStep(1);
        nfTaille.setPlaceholder("Ex: 175");

        cbSexe.setAllowCustomValue(false);
        cbSexe.setItems(StatutSexe.values());
        cbSexe.setItemLabelGenerator(statut -> statut.toString().toUpperCase());

        // Layouts
        HorizontalLayout hLayout = new HorizontalLayout(tfNom, tfPrenom, tfSurnom);
        // Note : j'ai renommé les variables pour que ce soit cohérent (nfJour -> cbJour)
        HorizontalLayout hLayout2 = new HorizontalLayout(cbJour, cbMois, cbAnnee);
        // On aligne les champs date sur la ligne de base pour que ce soit joli
        hLayout2.setAlignItems(Alignment.BASELINE); 
        
        HorizontalLayout hLayout3 = new HorizontalLayout(nfTaille, cbSexe);
        add(hLayout, hLayout2, hLayout3);


        // --- BOUTON VALIDER ---
        Button bValider = new Button("Valider");
        bValider.addClickListener(event -> {
            try {
                // 1. Lectures et validations

                // Strings
                String surnom = tfSurnom.getValue();
                String nom = tfNom.getValue();
                if (nom == null || nom.trim().isEmpty()) { throw new Exception("Veuillez entrer un nom."); }
                String prenom = tfPrenom.getValue();
                if (prenom == null || prenom.trim().isEmpty()) { throw new Exception("Veuillez entrer un prénom."); }

                // --- VALIDATION SIMPLIFIÉE POUR LA DATE (ComboBox) ---
                Integer jourInt = cbJour.getValue();
                if (jourInt == null) { throw new Exception("Veuillez sélectionner un jour de naissance."); }

                Enum_Mois moisEnum = cbMois.getValue();
                if (moisEnum == null) { throw new Exception("Veuillez sélectionner un mois de naissance."); }

                Integer anneeInt = cbAnnee.getValue();
                if (anneeInt == null) { throw new Exception("Veuillez sélectionner une année de naissance."); }

                // Validation basique de cohérence (ex: 31 Février)
                try {
                    LocalDate.of(anneeInt, moisEnum.getNumero(), jourInt);
                } catch (Exception e) {
                    throw new Exception("La date de naissance saisie n'est pas valide (ex: 31 Février).");
                }
                // ----------------------------------------------------

                // Taille (toujours un NumberField)
                int taille = readIntValue(nfTaille, "taille");
                if (taille < 50 || taille > 250) {
                    throw new Exception("La taille n'est pas valide (doit être entre 50 et 250 cm).");
                }

                // Sexe
                StatutSexe sexe = cbSexe.getValue();
                if (sexe == null) {
                    throw new Exception("Veuillez sélectionner un sexe.");
                }

                // 2. Création du joueur
                Joueur j = new Joueur(surnom, sexe, taille, nom, prenom, moisEnum.getNumero(), jourInt, anneeInt);
                Joueur.creerJoueur(j);

                Notification.show("Joueur créé avec succès : " + prenom + " " + nom)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                clearForm();

                // Bouton pour aller s'inscrire à un tournoi
                Button btnVoirTournois = new Button("S'inscrire à un tournoi maintenant",
                        ev -> getUI().ifPresent(ui -> ui.navigate("tournois")));
                btnVoirTournois.getStyle().set("margin-top", "20px");
                add(btnVoirTournois);

            } catch (Exception e) {
                Notification.show("Erreur : " + e.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        // --- BOUTON ANNULER ---
        Button bAnnuler = new Button("Annuler");
        bAnnuler.addClickListener(event -> clearForm());

        this.add(new HorizontalLayout(bValider, bAnnuler));
    }

    // --- Méthodes Utilitaires ---

    /**
     * Méthode pour vider le formulaire
     */
    private void clearForm() {
        tfSurnom.clear();
        nfTaille.clear();
        cbSexe.clear();
        tfNom.clear();
        tfPrenom.clear();
        // MODIFICATION ICI AUSSI
        cbJour.clear();
        cbMois.clear();
        cbAnnee.clear();
    }

    /**
     * Lit la valeur d'un NumberField de manière sécurisée (utilisé uniquement pour la taille maintenant).
     */
    private int readIntValue(NumberField field, String fieldName) throws Exception {
        Double value = field.getValue();
        if (value == null) {
            throw new Exception("Veuillez entrer une valeur pour : " + fieldName + ".");
        }
        return value.intValue();
    }
}