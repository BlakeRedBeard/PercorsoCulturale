package com.example.percorsoculturale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Map;

public class MostraPercorsiActivity extends AppCompatActivity {

    private String   idPercorso;
    private TextView nomePercorso,
                     descrizionePercorso,
                     regionePercorso,
                     comunePercorso;
    private ImageView immaginePercorso;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Button avvia;
    private List<String> attrazioni, attivita;
    private final String LINK = "https://www.percorsoculturale.com/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            //FirebaseAuth.getInstance().signInWithEmailAndPassword("user@guest.com", "userguest");
            Intent login = new Intent(this, LoginActivity.class);
            login.putExtra("intent", this.getIntent());
            startActivity(login);
        }
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_mostra_percorso_landscape);
        }else{
            setContentView(R.layout.activity_mostra_percorso);
        }
        nomePercorso = (TextView) findViewById(R.id.nomePercorso);
        descrizionePercorso = (TextView) findViewById(R.id.descrizionePercorso);
        regionePercorso = (TextView) findViewById(R.id.regionePercorso);
        comunePercorso = (TextView) findViewById(R.id.comunePercorso);
        immaginePercorso = (ImageView) findViewById(R.id.immaginePercorso);
        avvia = (Button) findViewById(R.id.avviaButton);
        avvia.setEnabled(false);

        if(savedInstanceState == null) {
            Bundle extra = getIntent().getExtras();
            if (extra != null) {
                if (extra.getString("percorso") != null) {
                    idPercorso = extra.getString("percorso");
                    showPercorso(idPercorso);
                    FloatingActionButton btnShare = findViewById(R.id.btnCondividiPercorso);
                    btnShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Prova questo percorso culturale!");
                            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, LINK + extra.getString("percorso"));
                            emailIntent.setType("text/plain");
                            startActivity(Intent.createChooser(emailIntent, "Send to friend"));
                        }
                    });
                } else {
                    //TODO generare eccezione (id percorso non reperito)
                }
            } else {
                idPercorso = savedInstanceState.getString("percorso");
                showPercorso(idPercorso);
                FloatingActionButton btnShare = findViewById(R.id.btnCondividiPercorso);
                btnShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Prova questo percorso culturale!");
                        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, LINK + savedInstanceState.getSerializable("percorso"));
                        emailIntent.setType("text/plain");
                        startActivity(Intent.createChooser(emailIntent, "Send to friend"));
                    }
                });
            }


            avvia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //passa alla sezione attrazione relativa al percorso
                    Intent intent = new Intent(getApplicationContext(), MostraAttrazioni.class);
                    intent.putExtra("attrazione", 0);
                    intent.putExtra("nomePercorso", idPercorso);
                    MostraAttrazioni.setIsSvolta();
                    startActivity(intent);
                }
            });
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();

                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        savedInstanceState.putString("percorso", extra.getString("percorso"));
    }

    @Override
    protected void onStart(){
        super.onStart();
        Intent intent = getIntent();
        Uri data = intent.getData();
        if(data != null) {

            showPercorso(data.getLastPathSegment());

        }else Log.i("DEBUG: intent", "data è nullo");
    }


    public void showPercorso(String search){
        db.collection("percorso")
                .document(search)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
                        String language = pref.getString("My_Lang", "");
                        DocumentSnapshot document = task.getResult();
                        Log.d("DEBUG", document.getId() + " => " + document.getData());
                        for(Map.Entry<String, Object> entry : document.getData().entrySet()){
                            if(entry.getKey().equals("nome"+language)){
                                nomePercorso.setText((String) entry.getValue());
                            }else if(entry.getKey().equals("descrizione"+language)){
                                descrizionePercorso.setText((String) entry.getValue());
                            }else if(entry.getKey().equals("regione")){
                                regionePercorso.setText((String) entry.getValue());
                            }else if(entry.getKey().equals("comune")){
                                comunePercorso.setText((String) entry.getValue());
                            }else if(entry.getKey().equals("attrazioni")) {
                                attrazioni = (List<String>) entry.getValue();
                                MostraAttrazioni.setAttrazioni(attrazioni);
                            }else if(entry.getKey().equals("gmaps")){
                                //TODO aggiungere bottone che va su google maps
                            }else if(entry.getKey().equals("attivita")){
                                attivita = (List<String>) entry.getValue();
                                MostraAttrazioni.setAttivita(attivita);
                            }else if(entry.getKey().equals("immagine")){
                                StorageReference gsReference = storage.getReferenceFromUrl((String) entry.getValue());
                                final long ONE_MEGABYTE = 1024 * 1024;
                                gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        // image retrieved
                                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        immaginePercorso.setImageBitmap(bmp);
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
                        avvia.setEnabled(true);
                    }
                });
    }




}