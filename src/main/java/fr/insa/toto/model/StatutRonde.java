package fr.insa.toto.model;

public enum StatutRonde {
    EN_ATTENTE, // La ronde est planifiée mais pas encore démarrée
    EN_COURS,   // La ronde est en cours, des matchs sont joués
    TERMINEE,   // Tous les matchs de la ronde ont été joués et les résultats saisis
    ANNULEE     // La ronde a été annulée
}