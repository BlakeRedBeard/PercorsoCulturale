package com.example.percorsoculturale.tables;

public class Valutazione {

    private int id;
    private Percorso percorso;
    private int valutazione;
    private String commento;
    private Utente utente;

    public Valutazione(int id, Percorso percorso, int valutazione, String commento, Utente utente) {
        this.id = id;
        this.percorso = percorso;
        this.valutazione = valutazione;
        this.commento = commento;
        this.utente = utente;
    }


    public int getId() {
        return id;
    }

    public Percorso getPercorso() {
        return percorso;
    }

    public int getValutazione() {
        return valutazione;
    }

    public String getCommento() {
        return commento;
    }

    public Utente getUtente() {
        return utente;
    }

}
