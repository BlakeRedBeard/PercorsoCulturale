package com.example.percorsoculturale;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class MostraAttrazioni extends AppCompatActivity {

    private TextView nomeAttrazione,
            descrizioneAttrazione;
    private Button btnIndietro,
            btnAttivita,
            btnAvanti;
    private ImageView immagineAttrazione;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private int id;
    private static ArrayList<String> attrazioni;
    private static ArrayList<String> attivita;
    private static ArrayList<Boolean> isSvolta;
    private String nomePercorso = "";

    public static void setAttrazioni(Collection<String> lista) {
        attrazioni = new ArrayList<String>(lista);
    }

    public static void setAttivita(Collection<String> lista) {
        attivita = new ArrayList<String>(lista);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_attrazione_landscape);
        } else {
            setContentView(R.layout.activity_attrazione);
        }

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        nomeAttrazione = (TextView) findViewById(R.id.nomeAttrazione);
        descrizioneAttrazione = (TextView) findViewById(R.id.descrizioneAttrazione);
        immagineAttrazione = (ImageView) findViewById(R.id.immagineAttrazione);
        btnIndietro = (Button) findViewById(R.id.btnIndietro);
        btnAttivita = (Button) findViewById(R.id.btnAttivita);
        btnAvanti = (Button) findViewById(R.id.btnAvanti);

        if (savedInstanceState != null) {
            nomeAttrazione.setText(savedInstanceState.getString("nomeAttrazione"));

            Bundle extra = getIntent().getExtras();
            if (extra != null) {
                id = extra.getInt("attrazione");
                nomePercorso = extra.getString("nomePercorso");
                if (id < 0) {
                    //TODO generare eccezione (non è possibile identificare l'attrazione)
                }
                //setIsSvolta();
                showAttrazione(attrazioni.get(id));
                checkAttivita(attivita.get(id));
                //Inizializzazione bottoni
                btnAvanti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int cont = attrazioni.size();

                        if (id + 1 >= cont) {
                            if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("user@guest.com")) {
                                Intent intent = new Intent(getApplicationContext(), ValutazionePercorsoUtente.class);
                                intent.putExtra("nomePercorso", nomePercorso);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), ValutazionePercorsoActivity.class);
                                intent.putExtra("nomePercorso", nomePercorso);
                                startActivity(intent);
                            }
                        } else {
                            Intent intent = new Intent(getApplicationContext(), MostraAttrazioni.class);
                            intent.putExtra("attrazione", id + 1);
                            intent.putExtra("nomePercorso", nomePercorso);
                            startActivity(intent);
                        }


                    }
                });
                btnIndietro.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });


                //TODO se presente bisogna settarlo

            } else {
                //TODO generare eccezione (non è possibile identificare l'attrazione)
            }
        }


        //serve per recuperare l'attrazione specifica al percorso stabilito
        if (savedInstanceState == null) {
            Bundle extra = getIntent().getExtras();
            if (extra != null) {
                id = extra.getInt("attrazione");
                nomePercorso = extra.getString("nomePercorso");
                if (id < 0) {
                    //TODO generare eccezione (non è possibile identificare l'attrazione)
                }
                //setIsSvolta();
                showAttrazione(attrazioni.get(id));
                if(attivita != null)
                    checkAttivita(attivita.get(id));
                //Inizializzazione bottoni
                btnAvanti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int cont = attrazioni.size();

                        if (id + 1 >= cont) {
                            if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("user@guest.com")) {
                                Intent intent = new Intent(getApplicationContext(), ValutazionePercorsoUtente.class);
                                intent.putExtra("nomePercorso", nomePercorso);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), ValutazionePercorsoActivity.class);
                                intent.putExtra("nomePercorso", nomePercorso);
                                startActivity(intent);
                            }
                        } else {
                            Intent intent = new Intent(getApplicationContext(), MostraAttrazioni.class);
                            intent.putExtra("attrazione", id + 1);
                            intent.putExtra("nomePercorso", nomePercorso);
                            startActivity(intent);
                        }


                    }
                });
                btnIndietro.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });


                //TODO se presente bisogna settarlo

            } else {
                //TODO generare eccezione (non è possibile identificare l'attrazione)
            }
        }

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back=new Intent(getApplicationContext(), MostraPercorsiActivity.class);
                startActivity(back);

            }
        });
    }


    public void showAttrazione(String search) {
        db.collection("attrazione")
                .document(search)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        Log.d("DEBUG", document.getId() + " => " + document.getData());
                        for (Map.Entry<String, Object> entry : document.getData().entrySet()) {
                            if (entry.getKey().equals("nome")) {
                                nomeAttrazione.setText((String) entry.getValue());
                            } else if (entry.getKey().equals("descrizione")) {
                                descrizioneAttrazione.setText((String) entry.getValue());
                            } else if (entry.getKey().equals("immagine")) {
                                StorageReference gsReference = storage.getReferenceFromUrl((String) entry.getValue());
                                final long ONE_MEGABYTE = 1024 * 1024;
                                gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        // image retrieved
                                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        immagineAttrazione.setImageBitmap(bmp);
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

    public void checkAttivita(String search) {
        if (isSvolta.get(id) == false) {
            if (search != null) {

                btnAttivita.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent3 = new Intent(getApplicationContext(), QrcodeActivity.class);
                        intent3.putExtra("attivita", attivita.get(id));
                        intent3.putExtra("Idattrazioni", id);
                        intent3.putExtra("nomePercorso", nomePercorso);
                        startActivity(intent3);
                        btnAttivita.setEnabled(false);
                    }
                });
            } else
                btnAttivita.setEnabled(false);
        } else {
            btnAttivita.setEnabled(false);

        }

    }

    public static void setIsSvolta() {

        int cont = attrazioni.size();
        isSvolta = new ArrayList<Boolean>(Collections.nCopies(cont, false));

    }

    public static void setTrue(int id) {

        isSvolta.set(id, true);

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("nomeAttrazione", nomeAttrazione.getText().toString());

    }

}