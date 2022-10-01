package com.example.percorsoculturale.tables;

import java.sql.Date;

public class Utente_Percorso {

    private Utente utente;
    private Percorso percorso;
    private Date data;
    private int punteggioOttenuto;

    public Utente_Percorso(Utente utente, Percorso percorso, Date data, int punteggioOttenuto) {
        this.utente = utente;
        this.percorso = percorso;
        this.data = data;
        this.punteggioOttenuto = punteggioOttenuto;
    }

    public Utente getUtente() {
        return utente;
    }

    public Percorso getPercorso() {
        return percorso;
    }

    public Date getData() {
        return data;
    }

    public int getPunteggioOttenuto() {
        return punteggioOttenuto;
    }

}
