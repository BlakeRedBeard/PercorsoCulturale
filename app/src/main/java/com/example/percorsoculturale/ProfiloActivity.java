package com.example.percorsoculturale;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
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
    private TextInputEditText inputNome;
    private TextInputEditText inputCognome;
    private TextInputEditText inputPassword;
    private Button invia;
    private String mailUtente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo);

        textNomeCognome = (TextView) findViewById(R.id.txt);
        inputNome= (TextInputEditText) findViewById(R.id.nome);
        inputCognome= (TextInputEditText) findViewById(R.id.cognome);
        inputPassword= (TextInputEditText) findViewById(R.id.password);
        puntiUtente = (TextView) findViewById(R.id.badge2);
        invia = (Button) findViewById(R.id.invia);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        mailUtente = firebaseUser.getEmail();

        searchUser(mailUtente);

        LinearLayout rl = (LinearLayout) findViewById(R.id.LinearProfile2);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfiloActivity.this, BadgeActivity.class));
            }
        });

        invia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInfo(mailUtente);
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
                        int punti = document.getLong("punti").intValue();

                        textNomeCognome.setText(nome+"  "+cognome);
                        inputNome.setText(nome);
                        inputCognome.setText(cognome);
                        inputPassword.setText(password);
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

    public void updateInfo(String mail) {
                Map<String, Object> user = new HashMap<>();
                user.put("nome", inputNome.getText().toString());
                user.put("cognome", inputCognome.getText().toString());
                user.put("password", inputPassword.getText().toString());

                db.collection("utente").document(mail)
                        .set(user, SetOptions.merge())
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


}