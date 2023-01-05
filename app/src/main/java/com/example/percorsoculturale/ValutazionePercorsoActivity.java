package com.example.percorsoculturale;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ValutazionePercorsoActivity extends AppCompatActivity {
    private TextView textview1;
    private TextView textview2;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference noteRef;
    private FirebaseAuth firebaseAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.valutazione_percorso_landscape);
        } else {
            setContentView(R.layout.valutazione_percorso);
        }

        textview1 = (TextView) findViewById(R.id.textView4);
        textview2 = (TextView) findViewById(R.id.textView6);

        //legge da db
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String mailUtente = firebaseUser.getEmail();

        noteRef = db.collection("utente").document(mailUtente);
        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                textview1.setText(documentSnapshot.getString("nome"));

            }
        });


        int puntiTotali = QrcodeActivity.getPunti();

        textview2.setText(Integer.toString(puntiTotali));


        //aggiorna punti utente
        noteRef
                .update("punti", FieldValue.increment(puntiTotali))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error updating document", e);
                    }
                });

        QrcodeActivity.resettaPunti();
        Button condividi = (Button) findViewById(R.id.button);


        condividi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String nomePercorso = "a";
                String body = "Ho ottenuto " + textview2.getText().toString() + " punti completando il percorso " + nomePercorso;
                String sub = "Valutazione percorso";
                myIntent.putExtra(Intent.EXTRA_SUBJECT, sub);
                myIntent.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(myIntent, "Share Using"));

            }
        });


        Button btnBadge = (Button) findViewById(R.id.badgeButton);
        btnBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ValutazionePercorsoActivity.this, BadgeActivity.class));
            }

        });


        Button btnContinua = (Button) findViewById(R.id.continuaButton);
        btnContinua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ValutazionePercorsoActivity.this, RicercaPercorsiActivity.class));
            }

        });

    }
}

