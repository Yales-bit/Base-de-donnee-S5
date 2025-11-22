package fr.insa.toto.model;

// Cette exception sert juste à signaler précisément ce problème
public class TournoiNomExisteDejaException extends Exception {
    public TournoiNomExisteDejaException(String message) {
        super(message);
    }
}