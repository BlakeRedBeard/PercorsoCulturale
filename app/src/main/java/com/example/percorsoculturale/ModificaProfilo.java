package com.example.percorsoculturale;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ModificaProfilo extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private TextView textNomeCognome;

    private TextInputEditText inputNome;
    private TextInputEditText inputCognome;
    private TextInputEditText inputPassword;
    private Button invia;
    private String mailUtente;

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_profilo);


        inputNome= (TextInputEditText) findViewById(R.id.nome);
        inputCognome= (TextInputEditText) findViewById(R.id.cognome);
        inputPassword= (TextInputEditText) findViewById(R.id.password);
        invia = (Button) findViewById(R.id.invia);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        mailUtente = firebaseUser.getEmail();


        invia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messaggio =  "Sei sicuro di voler salvare le modifiche?";
                showMessage(messaggio);
            }
        });
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
    }


    public void updateInfo(String mail) {
        Map<String, Object> user = new HashMap<>();

        if(!inputNome.getText().toString().equals("")){
            user.put("nome", inputNome.getText().toString());
        }
        if(!inputCognome.getText().toString().equals("")){
            user.put("cognome", inputCognome.getText().toString());
        }
        if(!inputPassword.getText().toString().equals("12345678")){
            user.put("password", inputPassword.getText().toString());
        }

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
    private void showMessage(String messaggio) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ModificaProfilo.this);
        builder.setMessage(messaggio)
                .setTitle("Modifica profilo");
// Add the buttons
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                updateInfo(mailUtente);
                Intent i = new Intent(getApplicationContext(),ProfiloActivity.class);
                startActivity(i);
            }
        });
        builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
// Set other dialog properties
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
