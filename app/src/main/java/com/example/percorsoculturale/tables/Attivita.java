package com.example.percorsoculturale.tables;

public class Attivita {

    private int id;
    private int punteggio;
 private Puzzle puzzle;
    public Attivita(int id, int punteggio) {
        this.id = id;
        this.punteggio = punteggio;
    }

    public int getId() {
        return id;
    }

    public int getPunteggio() {
        return punteggio;
    }


}