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

@Route(value = "inscription", layout = InterfacePrincipale.class)
@PageTitle("Inscription")
public class VueInscription extends VerticalLayout {

    // Déclaration des champs au niveau de la classe pour y accéder dans les deux boutons
    private TextField tfNom = new TextField("Nom");
    private TextField tfPrenom = new TextField("Prenom");
    private TextField tfSurnom = new TextField("Surnom");
    private NumberField nfJour = new NumberField("Jour de naissance");
    private ComboBox<Enum_Mois> cbMois = new ComboBox<>("Mois de naissance");
    private NumberField nfAnnee = new NumberField("Année de naissance");
    private NumberField nfTaille = new NumberField("Taille (cm)");
    private ComboBox<StatutSexe> cbSexe = new ComboBox<>("Sexe");

    public VueInscription() {
        this.setAlignItems(Alignment.CENTER);
        H1 titre = new H1("Inscription");
        add(titre);

        // Configuration des composants
        cbMois.setAllowCustomValue(false);
        cbMois.setItems(Enum_Mois.values());
        cbMois.setItemLabelGenerator(mois -> mois.toString().toUpperCase());

        // Configuration des NumberField pour n'accepter que des entiers visuellement (optionnel mais mieux)
        nfJour.setStep(1);
        nfAnnee.setStep(1);
        nfTaille.setStep(1);

        cbSexe.setAllowCustomValue(false);
        cbSexe.setItems(StatutSexe.values());
        cbSexe.setItemLabelGenerator(statut -> statut.toString().toUpperCase());

        // Layouts
        HorizontalLayout hLayout = new HorizontalLayout(tfNom, tfPrenom, tfSurnom);
        // Note : j'ai supprimé 'nfMois' qui était déclaré mais inutilisé
        HorizontalLayout hLayout2 = new HorizontalLayout(nfJour, cbMois, nfAnnee);
        HorizontalLayout hLayout3 = new HorizontalLayout(nfTaille, cbSexe);
        add(hLayout, hLayout2, hLayout3);


        // --- BOUTON VALIDER ---
        Button bValider = new Button("Valider");
        bValider.addClickListener(event -> {
            try {
                // 1. Lectures et validations sécurisées contre les NullPointerException

                // Strings : Vérifier si vide ou null
                String surnom = tfSurnom.getValue();
                String nom = tfNom.getValue();
                if (nom == null || nom.trim().isEmpty()) {
                    throw new Exception("Veuillez entrer un nom.");
                }
                String prenom = tfPrenom.getValue();
                if (prenom == null || prenom.trim().isEmpty()) {
                    throw new Exception("Veuillez entrer un prénom.");
                }

                // Nombres : Utilisation de la méthode utilitaire sécurisée (voir plus bas)
                int jour = readIntValue(nfJour, "jour de naissance");
                if (jour < 1 || jour > 31) {
                    throw new Exception("Le jour de naissance n'est pas valide.");
                }

                int annee = readIntValue(nfAnnee, "année de naissance");
                if (annee < 1900 || annee > 2100) {
                    throw new Exception("L'année de naissance n'est pas valide.");
                }

                int taille = readIntValue(nfTaille, "taille");
                if (taille < 50 || taille > 250) {
                    throw new Exception("La taille n'est pas valide.");
                }

                // ComboBox Mois : Vérification du null avant d'appeler une méthode
                Enum_Mois moisEnum = cbMois.getValue();
                if (moisEnum == null) {
                    throw new Exception("Veuillez sélectionner un mois de naissance.");
                }
                int mois = moisEnum.getNumero(); // Maintenant c'est sûr

                // ComboBox Sexe : Vérification du null (C'était déjà correct dans ton code !)
                StatutSexe sexe = cbSexe.getValue();
                if (sexe == null) {
                    throw new Exception("Veuillez sélectionner un sexe.");
                }

                // 2. Création du joueur si tout est bon
                Joueur j = new Joueur(surnom, sexe, taille, nom, prenom, mois, jour, annee);
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
                // Affiche le message de l'exception (soit les nôtres, soit une erreur BDD)
                Notification.show("Erreur : " + e.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                // e.printStackTrace(); // Optionnel : utile pour le débogage console
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
        nfJour.clear();
        cbMois.clear();
        nfAnnee.clear();
    }

    /**
     * Lit la valeur d'un NumberField de manière sécurisée.
     * Jette une exception explicite si le champ est vide au lieu de faire un NullPointerException.
     */
    private int readIntValue(NumberField field, String fieldName) throws Exception {
        Double value = field.getValue();
        if (value == null) {
            throw new Exception("Veuillez entrer une valeur pour : " + fieldName + ".");
        }
        return value.intValue();
    }
}