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

import com.example.percorsoculturale.databinding.ActivityRecuperaPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperaPassword extends AppCompatActivity {

    private TextInputLayout emailView;
    private Button btnReset;
    FirebaseAuth auth = FirebaseAuth.getInstance();


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recupera_password);

        SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = pref.getString("My_Lang", "en").equals("") ? "it" : pref.getString("My_Lang", "en");

        auth.setLanguageCode(language);

        emailView = (TextInputLayout) findViewById(R.id.editTextTextEmailAddress);
        if (savedInstanceState != null) {
            emailView.getEditText().setText(savedInstanceState.getString("email"));
        }


        btnReset = (Button) findViewById(R.id.loginButton);


        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = emailView.getEditText().getText().toString().trim().toLowerCase();
                System.out.println("DEBUG: mail formattata: "+mail);
                auth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RecuperaPassword.this, "Email inviata", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(RecuperaPassword.this, "Questa email non è registrata", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RecuperaPassword.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }
}
