package com.example.percorsoculturale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class EliminaProfilo extends AppCompatActivity {

    private TextInputEditText password;
    private Button invia;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elimina_account);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        invia = (Button) findViewById(R.id.invia);
        TextInputEditText inputPwd = (TextInputEditText) findViewById(R.id.password);

        invia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), inputPwd.getText().toString());

                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("DEBUG", "User re-authenticated.");
                            }
                        });
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            db.collection("utente")
                                    .document(user.getEmail())
                                    .delete();
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
                        Log.e("DEBUG: error ", e.getMessage());
                    }
                });
            }
        });


    }


}
