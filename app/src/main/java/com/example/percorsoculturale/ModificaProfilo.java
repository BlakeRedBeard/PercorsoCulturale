package com.example.percorsoculturale;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

        textNomeCognome = (TextView) findViewById(R.id.txt);
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

                updateInfo(mailUtente);
                Intent i = new Intent(getApplicationContext(),ProfiloActivity.class);
                startActivity(i);
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
