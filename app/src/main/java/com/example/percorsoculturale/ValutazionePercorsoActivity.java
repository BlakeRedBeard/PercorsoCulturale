package com.example.percorsoculturale;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

public class ValutazionePercorsoActivity extends AppCompatActivity {
    private TextView textview1;
    private TextView textview2;
    private DocumentReference noteRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String nomePercorso = "";
    ImageView img;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        img = findViewById(R.id.immagineePercorso);
        Bundle extra = getIntent().getExtras();
        nomePercorso = extra.getString("nomePercorso");
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        showPercorso(nomePercorso);

        textview1 = (TextView) findViewById(R.id.textView4);
        textview2 = (TextView) findViewById(R.id.textView6);


        TextView textview3 = (TextView) findViewById(R.id.msg_finePercorso);
        if (QrcodeActivity.getPunti() <= 5) {
            ImageView spunta = (ImageView) findViewById(R.id.spunta_verde);
            spunta.setImageDrawable(getDrawable(R.drawable.error_failure));
            textview3.setText(getString(R.string.msg_consolazione));
        }
        ImageView spunta = (ImageView) findViewById(R.id.spunta_verde);

        TextView textview4 = (TextView) findViewById(R.id.textView5);

        //legge da db
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String mailUtente = firebaseUser.getEmail();

        noteRef = db.collection("utente").document(mailUtente);
        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                textview1.setText(documentSnapshot.getString("nome"));

            }
        });


        int puntiTotali = QrcodeActivity.getPunti();

        textview2.setText(Integer.toString(puntiTotali));


        //aggiorna punti utente
        noteRef.update("punti", FieldValue.increment(puntiTotali))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error updating document", e);
                    }
                });

        QrcodeActivity.resettaPunti();
        FloatingActionButton condividi = (FloatingActionButton) findViewById(R.id.button);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        condividi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String body = "Ho ottenuto " + textview2.getText().toString() + " punti completando il percorso " + nomePercorso;
                String sub = "Valutazione percorso";
                myIntent.putExtra(Intent.EXTRA_SUBJECT, sub);
                myIntent.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(myIntent, "Share Using"));

            }
        });


        Button btnContinua = (Button) findViewById(R.id.continuaButton);
        btnContinua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ValutazionePercorsoUtente.class);
                intent.putExtra("nomePercorso", nomePercorso);
                startActivity(intent);
            }

        });



    }

    public void showPercorso(String search) {
        db.collection("percorso")
                .document(search)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        Log.d("DEBUG", document.getId() + " => " + document.getData());
                        for (Map.Entry<String, Object> entry : document.getData().entrySet()) {
                            if (entry.getKey().equals("immagine")) {
                                StorageReference gsReference = storage.getReferenceFromUrl((String) entry.getValue());
                                final long ONE_MEGABYTE = 1024 * 1024;
                                gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        // image retrieved
                                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        img.setImageBitmap(bmp);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                        Log.w("DEBUG", "Error downloading image", task.getException());
                                    }
                                });
                            }
                        }
                    }
                });
    }
}

