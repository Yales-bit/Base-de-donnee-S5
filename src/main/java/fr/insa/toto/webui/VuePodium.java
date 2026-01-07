package fr.insa.toto.webui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.toto.model.dto.LigneClassement;
import fr.insa.toto.model.Tournoi;

import java.util.List;

@Route(value = "podium", layout = InterfacePrincipale.class)
@PageTitle("Podium Final") 
public class VuePodium extends VerticalLayout implements HasUrlParameter<Integer> {

    private H1 titrePrincipal = new H1("PODIUM FINAL");
    private H2 nomTournoi = new H2();
    // Layout horizontal qui contiendra les 3 marches
    private HorizontalLayout podiumLayout = new HorizontalLayout();

    public VuePodium() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        addClassName("podium-container"); // Pour le fond CSS

        titrePrincipal.getElement().getStyle().set("font-size", "4em");
        titrePrincipal.getElement().getStyle().set("color", "var(--lumo-primary-text-color)");
        titrePrincipal.getElement().getStyle().set("text-shadow", "2px 2px 4px rgba(0,0,0,0.2)");

        podiumLayout.setAlignItems(Alignment.END); // Important : aligner les marches en bas
        podiumLayout.setSpacing(true);
        podiumLayout.setPadding(true);
        podiumLayout.getThemeList().add("spacing-xl");

        Button btnRetour = new Button("Retour au tournoi", VaadinIcon.ARROW_LEFT.create());
        btnRetour.addThemeVariants(ButtonVariant.LUMO_LARGE);
        btnRetour.addClickListener(e -> UI.getCurrent().getPage().getHistory().back());

        add(titrePrincipal, nomTournoi, podiumLayout, btnRetour);
    }

    @Override
    public void setParameter(BeforeEvent event, Integer tournoiId) {
        try {
            Tournoi t = Tournoi.getTournoiById(tournoiId);
            // SÉCURITÉ : On vérifie que le tournoi est bien FINI.
            if (t == null || !t.isFini()) {
                Notification.show("Accès refusé : Le tournoi n'est pas terminé.");
                // On redirige si l'utilisateur essaie d'accéder via l'URL
                event.forwardTo(VueListeTournois.class);
                return;
            }

            nomTournoi.setText(t.getNom().toUpperCase());
            
            // On récupère le top 3
            List<LigneClassement> top3 = t.getClassement().stream().limit(3).toList();
            construirePodium(top3);

        } catch (Exception e) {
            Notification.show("Erreur : " + e.getMessage());
        }
    }

    private void construirePodium(List<LigneClassement> top3) {
        podiumLayout.removeAll();

        LigneClassement first = top3.size() > 0 ? top3.get(0) : null;
        LigneClassement second = top3.size() > 1 ? top3.get(1) : null;
        LigneClassement third = top3.size() > 2 ? top3.get(2) : null;

        
        // Marche du 2ème (Hauteur moyenne)
        if (second != null) {
            podiumLayout.add(creerMarchePodium(second, 2, "350px"));
        }
        
        
        if (first != null) {
            podiumLayout.add(creerMarchePodium(first, 1, "450px"));
        }
        
        
        if (third != null) {
            podiumLayout.add(creerMarchePodium(third, 3, "250px"));
        }
    }

    private VerticalLayout creerMarchePodium(LigneClassement ligne, int rang, String height) {
        VerticalLayout step = new VerticalLayout();
        step.addClassName("podium-step");
        // Ajout de la classe spécifique pour la couleur et le délai d'animation CSS
        step.addClassName("rank-" + rang);
        
        step.setWidth("220px");
        step.setHeight(height);
        step.setPadding(true);
        // Position relative pour placer le gros numéro en bas
        step.getElement().getStyle().set("position", "relative");

        // Gros numéro de rang en fond
        Span rankNum = new Span("#" + rang);
        rankNum.addClassName("rank-number");

        // Nom du joueur
        Span name = new Span(ligne.getNomAffichage());
        name.addClassName("player-name");

        // Score
        Span score = new Span(ligne.getTotalPoints() + " pts");
        score.addClassName("player-score");

        // Icône de coupe
        VaadinIcon icon = (rang == 1) ? VaadinIcon.TROPHY : VaadinIcon.MEDAL;
        com.vaadin.flow.component.icon.Icon cupIcon = icon.create();
        cupIcon.setSize("3em");
        cupIcon.getElement().getStyle().set("margin-bottom", "20px");

        step.add(cupIcon, name, score, rankNum);
        return step;
    }
}