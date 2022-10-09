package com.example.percorsoculturale;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputLayout emailView;
    private TextInputLayout pwdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.login);

        emailView = (TextInputLayout) findViewById(R.id.editTextTextEmailAddress);
        pwdView = (TextInputLayout) findViewById(R.id.editTextTextPassword);
        Button login = (Button) findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Aggiungere controlli sull'input
                String email = emailView.getEditText().getText().toString();
                String password = pwdView.getEditText().getText().toString();
                signIn(email, password);

            }
        });
        Button iscriviti = (Button) findViewById(R.id.iscrivitiButton);
        iscriviti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //passa alla sezione per la registrazione
                Intent intent = new Intent(getApplicationContext(), IscrivitiActivity.class);
                startActivity(intent);
            }
        });
        Button login_ospite = (Button) findViewById(R.id.accediComeOspite);
        login_ospite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //effettua il login come ospite con credenziali predefinite
                signIn("user@guest.com", "userguest");
            }
        });

        TextView changePwd = (TextView) findViewById(R.id.cambiaPassword);
        changePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Avviare la procedura di cambio password
                Log.d("DEBUG", "è stata mandata una mail");
                Toast.makeText(LoginActivity.this, "È stata mandata una mail per la procedura di cambio password.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //cambiare schermata poiché l'utente ha già effettuato l'accesso
    }

    protected void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //TODO quando funzionante, eliminare le istruzioni di debug
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d("DEBUG", user.getEmail());
                            Toast.makeText(LoginActivity.this, "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                            //Aggiornare interfaccia, l'utente ha effettuato l'accesso
                            Intent intent = new Intent(getApplicationContext(), RicercaPercorsiActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            //TODO quando funzionante, eliminare le istruzioni di debug
                            Log.w("DEBUG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //TODO Aggiornare l'interfaccia, autenticazione fallita (credenziali errate o errore di sistema)
                        }
                    }
                });
    }

}