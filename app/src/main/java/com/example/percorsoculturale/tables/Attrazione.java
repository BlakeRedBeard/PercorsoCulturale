package com.example.percorsoculturale.tables;

public class Attrazione {

    private String id;
    private String nome;

    public Attrazione(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return "Attrazione{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}