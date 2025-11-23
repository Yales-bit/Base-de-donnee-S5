package fr.insa.toto.model;

public enum StatutSexe {
    MASCULIN,
    FEMININ;


    public boolean isEmpty() {
        return this == null || this.toString().isEmpty();
    }

    public String toString() {
        return this.name().toLowerCase();
    }
}
