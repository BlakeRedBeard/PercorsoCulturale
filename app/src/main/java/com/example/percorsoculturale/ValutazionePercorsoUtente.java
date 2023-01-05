package com.example.percorsoculturale;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ValutazionePercorsoUtente extends AppCompatActivity {
    ImageView unaStellaGrigia;
    ImageView dueStelleGrigie;
    ImageView treStelleGrigie;
    ImageView quattroStelleGrigie;
    ImageView cinqueStelleGrigie;

    ImageView unaStella;
    ImageView dueStelle;
    ImageView treStelle;
    ImageView quattroStelle;
    ImageView cinqueStelle;

    Button invia;

    private FirebaseAuth firebaseAuth;
    private String numeroStelle = "";
    private String nomePercorso = "";
    private String mailUtente = "";
    private String commento = "";

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private EditText commentoUtente;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.valutazione_percorso_utente_landscape);
        }else{
            setContentView(R.layout.valutazione_percorso_utente);
        }

        unaStellaGrigia = (ImageView) findViewById(R.id.imageView6);
        dueStelleGrigie= (ImageView) findViewById(R.id.imageView10);
        treStelleGrigie = (ImageView) findViewById(R.id.imageView9);
        quattroStelleGrigie = (ImageView) findViewById(R.id.imageView8);
        cinqueStelleGrigie = (ImageView) findViewById(R.id.imageView7);

        unaStella = (ImageView) findViewById(R.id.imageView12);
        dueStelle= (ImageView) findViewById(R.id.imageView15);
        treStelle = (ImageView) findViewById(R.id.imageView14);
        quattroStelle = (ImageView) findViewById(R.id.imageView13);
        cinqueStelle = (ImageView) findViewById(R.id.imageView16);

        commentoUtente = (EditText) findViewById(R.id.editTextTextPersonName2);

        unaStella.setVisibility(View.INVISIBLE);
        dueStelle.setVisibility(View.INVISIBLE);
        treStelle.setVisibility(View.INVISIBLE);
        quattroStelle.setVisibility(View.INVISIBLE);
        cinqueStelle.setVisibility(View.INVISIBLE);

        unaStellaGrigia.setVisibility(View.VISIBLE);
        dueStelleGrigie.setVisibility(View.VISIBLE);
        treStelleGrigie.setVisibility(View.VISIBLE);
        quattroStelleGrigie.setVisibility(View.VISIBLE);
        cinqueStelleGrigie.setVisibility(View.VISIBLE);

        if(savedInstanceState != null){
            int stellaUno = savedInstanceState.getInt("unastella");
            if(stellaUno  == 0) {
                numeroStelle = "unastellaGrigia";
                unaStella.setVisibility(View.VISIBLE);
                unaStellaGrigia.setVisibility(View.INVISIBLE);

            }
            int stellaUnoGrigia = savedInstanceState.getInt("unastellagrigia");
            if(stellaUnoGrigia  == 0) {

                unaStella.setVisibility(View.INVISIBLE);
                unaStellaGrigia.setVisibility(View.VISIBLE);
            }
            //
            int stellaDue = savedInstanceState.getInt("duestelle");
            if(stellaDue  == 0) {
                numeroStelle = "duestelleGrigie";
                dueStelle.setVisibility(View.VISIBLE);
                dueStelleGrigie.setVisibility(View.INVISIBLE);

            }
            int stellaDueGrigia = savedInstanceState.getInt("duestellegrigie");
            if(stellaDueGrigia  == 0) {

                dueStelle.setVisibility(View.INVISIBLE);
                dueStelleGrigie.setVisibility(View.VISIBLE);
            }

            //
            int stellaTre = savedInstanceState.getInt("trestelle");
            if(stellaTre  == 0) {
                numeroStelle = "trestelleGrigie";
                treStelle.setVisibility(View.VISIBLE);
                treStelleGrigie.setVisibility(View.INVISIBLE);

            }
            int stellaTreGrigia = savedInstanceState.getInt("trestellegrigie");
            if(stellaTreGrigia  == 0) {

                treStelle.setVisibility(View.INVISIBLE);
                treStelleGrigie.setVisibility(View.VISIBLE);
            }

            //
            int stellaQuattro = savedInstanceState.getInt("quattrostelle");
            if(stellaQuattro  == 0) {
                numeroStelle = "quattrostelleGrigie";
                quattroStelle.setVisibility(View.VISIBLE);
                quattroStelleGrigie.setVisibility(View.INVISIBLE);

            }
            int stellaQuattroGrigia = savedInstanceState.getInt("quattrostellegrigie");
            if(stellaQuattroGrigia  == 0) {

                quattroStelle.setVisibility(View.INVISIBLE);
                quattroStelleGrigie.setVisibility(View.VISIBLE);
            }

            //
            int stellaCinque = savedInstanceState.getInt("cinquestelle");
            if(stellaCinque  == 0) {
                numeroStelle = "cinquestelleGrigie";
                cinqueStelle.setVisibility(View.VISIBLE);
                cinqueStelleGrigie.setVisibility(View.INVISIBLE);

            }
            int stellaCinqueGrigia = savedInstanceState.getInt("cinquestellegrigie");
            if(stellaCinqueGrigia  == 0) {

                cinqueStelle.setVisibility(View.INVISIBLE);
                cinqueStelleGrigie.setVisibility(View.VISIBLE);
            }
        }

        invia = (Button) findViewById(R.id.btnInvia);

        Bundle extra = getIntent().getExtras();
        nomePercorso = extra.getString("nomePercorso");

        db = FirebaseFirestore.getInstance();

        unaStellaGrigia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numeroStelle = "unaStellaGrigia";
                valutazioneStelle(numeroStelle);
            }
        });

        dueStelleGrigie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numeroStelle = "dueStelleGrigie";
                valutazioneStelle(numeroStelle);
            }
        });

        treStelleGrigie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numeroStelle = "treStelleGrigie";
                valutazioneStelle(numeroStelle);
            }
        });

        quattroStelleGrigie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               numeroStelle = "quattroStelleGrigie";
                valutazioneStelle(numeroStelle);
            }
        });

        cinqueStelleGrigie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numeroStelle = "cinqueStelleGrigie";
                valutazioneStelle(numeroStelle);
            }
        });

        unaStella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numeroStelle = "unaStellaGrigia";
                valutazioneStelle(numeroStelle);
            }
        });

        dueStelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numeroStelle = "dueStelleGrigie";
                valutazioneStelle(numeroStelle);
            }
        });

        treStelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numeroStelle = "treStelleGrigie";
                valutazioneStelle(numeroStelle);
            }
        });

        quattroStelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numeroStelle = "quattroStelleGrigie";
                valutazioneStelle(numeroStelle);
            }
        });

        cinqueStelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numeroStelle = "cinqueStelleGrigie";
                valutazioneStelle(numeroStelle);
            }
        });

        invia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numeroStelle.equals("")) {
                    String messaggio = "Non hai selezionato un numero di stelle valido";
                    mostraMessaggio(messaggio);
                }
                else {
                    commento = commentoUtente.getText().toString();
                    aggiungiValutazione(nomePercorso, numeroStelle, commento);
                    String messaggio = "Grazie per la tua valutazione";
                    mostraMessaggio(messaggio);
                    Intent intent = new Intent(getApplicationContext(), RicercaPercorsiActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    public void valutazioneStelle(String stelle) {
        unaStella.setVisibility(View.INVISIBLE);
        dueStelle.setVisibility(View.INVISIBLE);
        treStelle.setVisibility(View.INVISIBLE);
        quattroStelle.setVisibility(View.INVISIBLE);
        cinqueStelle.setVisibility(View.INVISIBLE);

        unaStellaGrigia.setVisibility(View.VISIBLE);
        dueStelleGrigie.setVisibility(View.VISIBLE);
        treStelleGrigie.setVisibility(View.VISIBLE);
        quattroStelleGrigie.setVisibility(View.VISIBLE);
        cinqueStelleGrigie.setVisibility(View.VISIBLE);

        if (stelle.equals("unaStellaGrigia")) {
            unaStellaGrigia.setVisibility(View.INVISIBLE);
            unaStella.setVisibility(View.VISIBLE);
        }
        else if (stelle.equals("dueStelleGrigie")) {
            unaStellaGrigia.setVisibility(View.INVISIBLE);
            dueStelleGrigie.setVisibility(View.INVISIBLE);
            unaStella.setVisibility(View.VISIBLE);
            dueStelle.setVisibility(View.VISIBLE);
        }
        else if (stelle.equals("treStelleGrigie")) {
            unaStellaGrigia.setVisibility(View.INVISIBLE);
            dueStelleGrigie.setVisibility(View.INVISIBLE);
            treStelleGrigie.setVisibility(View.INVISIBLE);
            unaStella.setVisibility(View.VISIBLE);
            dueStelle.setVisibility(View.VISIBLE);
            treStelle.setVisibility(View.VISIBLE);
        }
        else if (stelle.equals("quattroStelleGrigie")) {
            unaStellaGrigia.setVisibility(View.INVISIBLE);
            dueStelleGrigie.setVisibility(View.INVISIBLE);
            treStelleGrigie.setVisibility(View.INVISIBLE);
            quattroStelleGrigie.setVisibility(View.INVISIBLE);
            unaStella.setVisibility(View.VISIBLE);
            dueStelle.setVisibility(View.VISIBLE);
            treStelle.setVisibility(View.VISIBLE);
            quattroStelle.setVisibility(View.VISIBLE);
        }
        else if (stelle.equals("cinqueStelleGrigie")) {
            unaStellaGrigia.setVisibility(View.INVISIBLE);
            dueStelleGrigie.setVisibility(View.INVISIBLE);
            treStelleGrigie.setVisibility(View.INVISIBLE);
            quattroStelleGrigie.setVisibility(View.INVISIBLE);
            cinqueStelleGrigie.setVisibility(View.INVISIBLE);
            unaStella.setVisibility(View.VISIBLE);
            dueStelle.setVisibility(View.VISIBLE);
            treStelle.setVisibility(View.VISIBLE);
            quattroStelle.setVisibility(View.VISIBLE);
            cinqueStelle.setVisibility(View.VISIBLE);
        }
    }

    public void aggiungiValutazione(String nomePercorso, String numeroStelle, String commento) {
        Map<String,Object> valutazioni = new HashMap<>();
        valutazioni.put("commento", commento);

        if (numeroStelle.equals("unaStellaGrigia")) {
            valutazioni.put("voto", 1);
        }
        else if (numeroStelle.equals("dueStelleGrigie")) {
            valutazioni.put("voto", 2);
        }
        else if (numeroStelle.equals("treStelleGrigie")) {
            valutazioni.put("voto", 3);
        }
        else if (numeroStelle.equals("quattroStelleGrigie")) {
            valutazioni.put("voto", 4);
        }
        else if (numeroStelle.equals("cinqueStelleGrigie")) {
            valutazioni.put("voto", 5);
        }

        String uniqueID = UUID.randomUUID().toString();

        String percorsoCollezione = "percorso/"+nomePercorso+"/recensioni";

        //inserisco il badge percorsi
        db.collection(percorsoCollezione).document(uniqueID)
                .set(valutazioni)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void mostraMessaggio(String messaggio) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ValutazionePercorsoUtente.this);

        builder.setMessage(messaggio)
                .setTitle("Info");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("unastella", unaStella.getVisibility());
        savedInstanceState.putInt("duestelle", dueStelle.getVisibility());
        savedInstanceState.putInt("trestelle", treStelle.getVisibility());
        savedInstanceState.putInt("quattrostelle", quattroStelle.getVisibility());
        savedInstanceState.putInt("cinquestelle", cinqueStelle.getVisibility());
        savedInstanceState.putInt("unastellagrigia", unaStellaGrigia.getVisibility());
        savedInstanceState.putInt("duestellegrigie", dueStelleGrigie.getVisibility());
        savedInstanceState.putInt("trestellegrigie", treStelleGrigie.getVisibility());
        savedInstanceState.putInt("quattrostellegrigie", quattroStelleGrigie.getVisibility());
        savedInstanceState.putInt("cinquestellegrigie", cinqueStelleGrigie.getVisibility());
    }

}
