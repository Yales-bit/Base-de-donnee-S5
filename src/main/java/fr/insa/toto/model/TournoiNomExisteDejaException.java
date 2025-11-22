package fr.insa.toto.model; // Ou votre package d'exceptions

// Cette exception sert juste à signaler précisément ce problème
public class TournoiNomExisteDejaException extends Exception {
    public TournoiNomExisteDejaException(String message) {
        super(message);
    }
}