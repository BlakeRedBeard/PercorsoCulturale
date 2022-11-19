package com.example.percorsoculturale;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import androidx.appcompat.app.AppCompatActivity;

public class AttrazioneActivity extends AppCompatActivity {

    private TextView nomeAttrazione,
            descrizioneAttrazione;

    private ImageView immagineAttrazione;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private Button indietro;
    private Button attivita;
    private Button avanti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        setContentView(R.layout.activity_attrazione);
        nomeAttrazione = (TextView) findViewById(R.id.nomeAttrazione);
        descrizioneAttrazione = (TextView) findViewById(R.id.descrizioneAttrazione);
        immagineAttrazione = (ImageView) findViewById(R.id.immagineAttrazione);

        if(savedInstanceState == null){
            Bundle extra = getIntent().getExtras();
            System.out.println(extra);

            //https://firebase.google.com/docs/database/android/read-and-write
        }

    }

}
