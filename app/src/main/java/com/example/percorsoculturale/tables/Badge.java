package com.example.percorsoculturale.tables;

public enum Badge {

    //costanti
    BRONZO,
    ARGENTO,
    ORO;

    //vettore enum per poter accedere alle costanti e creare un metodo get per ciacun ebadg

    //Per ogni punteggio viene associato un badge
    public static int getIntValue(Badge tipoBadge) {
        int value;

        switch(tipoBadge) {
            case BRONZO: value = 50;
                break;

            case ARGENTO: value = 100;
                break;

            case ORO: value = 150;
                break;

            default: value = 0;
                break;
        }

        return value;
    }
}
