package com.example.percorsoculturale;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ProfiloActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private TextView textNomeCognome;
    private TextView puntiUtente;
    private Button invia;
    private MaterialCardView modificaProfilo;
    private MaterialCardView eliminaProfilo;
    private String mailUtente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo);
        firebaseAuth = FirebaseAuth.getInstance();
        mailUtente = firebaseAuth.getCurrentUser().getEmail();
        puntiUtente = (TextView) findViewById(R.id.badge2);
        textNomeCognome = (TextView) findViewById(R.id.txt);
        modificaProfilo = (MaterialCardView) findViewById(R.id.editProfile);
        eliminaProfilo = (MaterialCardView) findViewById(R.id.deleteAccount);


        searchUser(mailUtente);
        eliminaProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EliminaProfilo.class);
                startActivity(intent);
            }
        });

        modificaProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ModificaProfilo.class);
                    startActivity(intent);
            }
        });

        LinearLayout rl = (LinearLayout) findViewById(R.id.LinearProfile2);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfiloActivity.this, BadgeActivity.class));
            }
        });


   
   
    }

    public void backHome(View view) {

        Intent backIntent=new Intent(this,RicercaPercorsiActivity.class);
        startActivity(backIntent);

    }

    public void searchUser(String mail) {
        String percorsoCollezione = "utente";

        //verifico se l'utente ha sbloccato il badge da 5 punti
        DocumentReference docRef = db.collection(percorsoCollezione).document(mail);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        //int ultimaVersion = document.get(Integer.class);
                        String nome = document.getString("nome");
                        String cognome = document.getString("cognome");
                        String password = document.getString("password");
                        int punti=0;
                        if(document.getLong("punti")!=null) {
                             punti = document.getLong("punti").intValue();
                        }


                        textNomeCognome.setText(nome+"  "+cognome);
                        puntiUtente.setText(Integer.toString(punti));


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }



}