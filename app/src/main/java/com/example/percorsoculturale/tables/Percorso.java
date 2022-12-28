package com.example.percorsoculturale.tables;

import android.media.Image;

import java.util.ArrayList;
import java.util.List;

public class Percorso {

    private String id;
    private String  nome,
                    comune,
                    provincia,
                    regione,
                    stato,
                    descrizione;
    private ArrayList<Attrazione> attrazioni;

    public Percorso(String id, String nome, String comune, String provincia, String regione, String stato, String descrizione) {
        this.id = id;
        this.nome = nome;
        this.comune = comune;
        this.provincia = provincia;
        this.regione = regione;
        this.stato = stato;
        this.descrizione = descrizione;
        this.attrazioni = new ArrayList<Attrazione>();
    }

    public Percorso(String id, String nome, String comune, String provincia, String regione, String stato, String descrizione, List<Attrazione> attrazioni) {
        this.id = id;
        this.nome = nome;
        this.comune = comune;
        this.provincia = provincia;
        this.regione = regione;
        this.stato = stato;
        this.descrizione = descrizione;
        this.attrazioni = new ArrayList<Attrazione>(attrazioni);
    }

    public void addAttrazione(Attrazione attrazione){
        attrazioni.add(attrazione);
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getComune() {
        return comune;
    }

    public String getProvincia() {
        return provincia;
    }

    public String getRegione() {
        return regione;
    }

    public String getStato() {
        return stato;
    }

    public String getDescrizione(){ return  descrizione; }

    public ArrayList<Attrazione> getAttrazioni() {
        return attrazioni;
    }

    @Override
    public String toString() {
        return "Percorso{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", comune='" + comune + '\'' +
                ", provincia='" + provincia + '\'' +
                ", regione='" + regione + '\'' +
                ", stato='" + stato + '\'' +
                ", attrazioni=" + attrazioni.toString() +
                '}';
    }
}
