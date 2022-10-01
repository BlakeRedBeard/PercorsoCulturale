package com.example.percorsoculturale.tables;

import java.sql.Date;

public class Utente {

    private int id;
    private String nome;
    private String cognome;
    private Date data_di_nascita;
    private String email;
    private String puntiTotali;

    public Utente(int id, String nome, String cognome, Date data_di_nascita, String email, String puntiTotali) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.data_di_nascita = data_di_nascita;
        this.email = email;
        this.puntiTotali = puntiTotali;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public Date getData_di_nascita() {
        return data_di_nascita;
    }

    public String getEmail() {
        return email;
    }

    public String getPuntiTotali() {
        return puntiTotali;
    }
}
