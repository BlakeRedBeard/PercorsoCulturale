package com.example.percorsoculturale;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
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

    public static void setAttrazioni(Collection<String> lista){
        attrazioni = new ArrayList<String>(lista);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        setContentView(R.layout.activity_attrazione);
        nomeAttrazione = (TextView) findViewById(R.id.nomeAttrazione);
        descrizioneAttrazione = (TextView) findViewById(R.id.descrizioneAttrazione);
        immagineAttrazione = (ImageView) findViewById(R.id.immagineAttrazione);
        btnIndietro = (Button) findViewById(R.id.btnIndietro);
        btnAttivita = (Button) findViewById(R.id.btnAttivita);
        btnAvanti = (Button) findViewById(R.id.btnAvanti);

        //serve per recuperare l'attrazione specifica al percorso stabilito
        if(savedInstanceState == null) {
           Bundle extra = getIntent().getExtras();
           if (extra != null) {
               id = extra.getInt("attrazione");
               if(id < 0){
                   //TODO generare eccezione (non è possibile identificare l'attrazione)
               }
               showAttrazione(attrazioni.get(id));
               checkAttivita(attrazioni.get(id));
                //Inizializzazione bottoni
               btnAvanti.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                       int cont = attrazioni.size();

                       Intent intent = new Intent(getApplicationContext(), MostraAttrazioni.class);
                       intent.putExtra("attrazione", id+1);
                       startActivity(intent);

                       if (id+1 >= cont) {
                           Intent intent2 = new Intent(getApplicationContext(), ValutazionePercorsoActivity.class);
                           startActivity(intent2);
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
               btnAttivita.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {



                       Intent intent3 = new Intent(getApplicationContext(), QrcodeActivity.class);
                       startActivity(intent3);

                   }
               });
           } else {
               //TODO generare eccezione (non è possibile identificare l'attrazione)
           }
        }




    }


    public void showAttrazione(String search){
        db.collection("attrazione")
                .document(search)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        Log.d("DEBUG", document.getId() + " => " + document.getData());
                        for(Map.Entry<String, Object> entry : document.getData().entrySet()){
                            if(entry.getKey().equals("nome")){
                                nomeAttrazione.setText((String) entry.getValue());
                            }else if(entry.getKey().equals("descrizione")){
                                descrizioneAttrazione.setText((String) entry.getValue());
                            }else if(entry.getKey().equals("immagine")){
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
        db.collection("attrazione")
                .document(search)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    int valore = 0;
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        String attivita = document.getString("attivita");


                        if (attivita == ""){
                            valore = 0;//controlla se non è vuoto

                            if (valore == 1) {
                                btnAttivita.setVisibility(View.VISIBLE);
                            }
                            else {
                                btnAttivita.setVisibility(View.INVISIBLE);
                            }
                        }


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

}