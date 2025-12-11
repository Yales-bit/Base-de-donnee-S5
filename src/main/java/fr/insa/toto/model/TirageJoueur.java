/*
Copyright 2000- Francois de Bertrand de Beuvron

This file is part of CoursBeuvron.

CoursBeuvron is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CoursBeuvron is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CoursBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.insa.toto.model;

/**
 *
 * @author vicbl
 */
import fr.insa.beuvron.utils.database.ConnectionPool;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import fr.insa.toto.model.Joueur;
import static fr.insa.toto.model.Joueur.getNombreJoueurs;
import fr.insa.toto.model.Tournoi;

// pas encore opérationnel 
public class TirageJoueur {

    public static void main(String[] args) {
        try {
            int nbJoueurs = getNombreJoueurs();
            System.out.println("Nombre de joueurs : " + nbJoueurs);
     //       int nbrequipe = 2*t.getNbrTerrains(); // récupérer le nbr d'équipes

            List<Integer> joueurin = creerListe(nbJoueurs);

            int valeurTiree = tirerEtSupprimer(joueurin);
            System.out.println("Valeur tirée : " + valeurTiree);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
    // Créer une table de 1 à n
    public static List<Integer> creerListe(int n) {
        List<Integer> liste = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            liste.add(i);
        }
        return liste;
    }

    //  Tirer un élément au hasard, l'afficher, puis le supprimer
    public static int tirerEtSupprimer(List<Integer> liste) {
        Random rand = new Random();
        int index = rand.nextInt(liste.size());  // index aléatoire
        int valeur = liste.get(index);           // valeur tirée

        liste.remove(index);                     // suppression définitive

        return valeur;
    }
}
