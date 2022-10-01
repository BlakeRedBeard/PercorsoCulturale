package com.example.percorsoculturale.tables;

import android.media.Image;

public class Percorso {

    private int id;
    private String nome;
    private String descrizione;
    private Image immagine;
    private Double latitudine;
    private Double longitudine;

    public Percorso(int id, String nome, String descrizione, Image immagine, Double latitudine, Double longitudine) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.immagine = immagine;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public Image getImmagine() {
        return immagine;
    }

    public Double getLatitudine() { return latitudine; }

    public Double getLongitudine() {
        return longitudine;
    }

}
