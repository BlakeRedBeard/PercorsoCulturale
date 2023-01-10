package com.example.percorsoculturale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EliminaProfilo extends AppCompatActivity {

    private TextInputEditText password;
    private Button invia;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getInstance().getCurrentUser();

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elimina_account);

        SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = pref.getString("My_Lang", "en").equals("") ? "it" : pref.getString("My_Lang", "en");

        auth.setLanguageCode(language);

        invia = (Button) findViewById(R.id.invia);


        invia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = ((TextInputEditText) findViewById(R.id.password)).getText().toString();
                System.out.println("DEBUG: mail formattata: "+ password);
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EliminaProfilo.this, "Account Eliminato", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(EliminaProfilo.this, "Password inserita non valida", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EliminaProfilo.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }


}
