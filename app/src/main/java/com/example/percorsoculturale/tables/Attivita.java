package com.example.percorsoculturale.tables;

public class Attivita {

    private String id;
    private int punteggio;
    public Attivita(String id, int punteggio) {
        this.id = id;
        this.punteggio = punteggio;
    }

    public String getId() {
        return id;
    }

    public int getPunteggio() {
        return punteggio;
    }


}