package fr.insa.toto.model;

/**
 * Définit l'ensemble fixe des statuts possibles pour un match dans le système de tournoi.
 * Utiliser une énumération assure la sécurité de type, la lisibilité et la cohérence
 * des données de statut à travers l'application.
 */
public enum StatutMatch {
    EN_ATTENTE, // Le match est créé et planifié, mais n'a pas encore commencé.
    EN_COURS,   // Le match est actuellement en cours de jeu.
    TERMINE,    // Le match est terminé et les résultats ont été saisis.
    ANNULE;     // Le match a été annulé pour une raison quelconque.
}