package com.example.percorsoculturale.tables;

public class Quiz {

    private String domanda;
    private String risposta_corretta;
    private String risposta_errata1;
    private String risposta_errata2;
    private String risposta_errata3;
    private int tempo;
    private int punti;


    public Quiz(String domanda, String risposta_corretta, String risposta_errata1, String risposta_errata2, String risposta_errata3, int tempo, int punti) {
        this.domanda = domanda;
        this.risposta_corretta = risposta_corretta;
        this.risposta_errata1 = risposta_errata1;
        this.risposta_errata2 = risposta_errata2;
        this.risposta_errata3 = risposta_errata3;
        this.tempo = tempo;
        this.punti = punti;
    }

    public Quiz(){

    }

    public String getDomanda() {
        return domanda;
    }

    public String getRisposta_corretta() {
        return risposta_corretta;
    }

    public String getRisposta_errata1() {
        return risposta_errata1;
    }

    public String getRisposta_errata2() {
        return risposta_errata2;
    }

    public String getRisposta_errata3() {
        return risposta_errata3;
    }

    public int getPunti() {
        return punti;
    }

    public int getTempo() {
        return tempo;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "domanda='" + domanda + '\'' +
                ", risposta_corretta='" + risposta_corretta + '\'' +
                ", risposta_errata1='" + risposta_errata1 + '\'' +
                ", risposta_errata2='" + risposta_errata2 + '\'' +
                ", risposta_errata3='" + risposta_errata3 + '\'' +
                ", tempo=" + tempo +
                '}';
    }
}
