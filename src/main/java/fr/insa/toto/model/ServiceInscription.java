/*cette classe servira de point d'accès unique à l'inscription des joueurs, de sorte que si on améliore le projet plus tard
(comme avec une verification de la session avant de pouvoir inscrire n'importe qui)
 on ne changera que cette classe, et pas les différentes vues qui l'implémentent*/
package fr.insa.toto.model;
import fr.insa.toto.model.Joueur;
import fr.insa.toto.model.Tournoi;

import java.util.List;

public class ServiceInscription {
    public static void tenterInscription(Tournoi t, List<Joueur> joueursAInscrire) throws Exception {
        // Logique actuelle ultra permissive : n'importe qui peut inscrire qqun d'autre au tournoi
        t.inscrireJoueurs(joueursAInscrire);
        // plus tard on vérifiera peut être si le joueur en question est bien connecté avant de pouvoir l'inscrire
    }

}
