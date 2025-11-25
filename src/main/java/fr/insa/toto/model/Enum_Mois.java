package fr.insa.toto.model;

public enum Enum_Mois {

    Janvier, Fevrier, Mars, Avril, Mai, Juin, Juillet, Aout, Septembre, Octobre, Novembre, Decembre;

    // Méthode pour obtenir le numéro du mois (1 pour Janvier, 2 pour Février, etc.)
    public int getNumero() {
        // ordinal() renvoie l'index de la constante dans l'énumération (0 pour Janvier, 1 pour Février, etc.)
        // On ajoute 1 pour obtenir le numéro du mois de 1 à 12.
        return this.ordinal() + 1;
    }

    // Méthode statique pour obtenir l'enum à partir du numéro (facultatif, mais utile)
    public static Enum_Mois fromNumero(int numero) {
        if (numero < 1 || numero > 12) {
            throw new IllegalArgumentException("Numéro de mois invalide : " + numero + ". Doit être entre 1 et 12.");
        }
        // On soustrait 1 pour retrouver l'index ordinal (0 à 11)
        return Enum_Mois.values()[numero - 1];
    }
}