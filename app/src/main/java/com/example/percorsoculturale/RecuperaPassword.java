package com.example.percorsoculturale;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperaPassword extends AppCompatActivity{


    private TextInputLayout emailView;
    private Button btnReset;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recupera_password);
        emailView = (TextInputLayout) findViewById(R.id.editTextTextEmailAddress);
        btnReset = (Button) findViewById(R.id.loginButton);

    btnReset.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String mail = ((TextInputEditText) findViewById(R.id.iscrivitiEmail)).getText().toString().trim().toLowerCase();
            FirebaseAuth auth = FirebaseAuth.getInstance();

            if (TextUtils.isEmpty(mail)) {
                Toast.makeText(getApplication(), "U sce scriv apprim", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.sendPasswordResetEmail(mail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RecuperaPassword.this, "Awand la password", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(i);
                            }

                        }
                    });
        }
    });


    }

}
