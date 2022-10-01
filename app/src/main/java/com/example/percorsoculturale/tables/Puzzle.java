package com.example.percorsoculturale.tables;

public class Puzzle {

    private Attivita attivita;
    private String directory;


    public Puzzle(Attivita attivita, String directory) {
        this.attivita = attivita;
        this.directory = directory;
    }

    public Attivita getAttivita() {
        return attivita;
    }

    public String getDirectory() {
        return directory;
    }

}
