package com.example.percorsoculturale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.percorsoculturale.databinding.ActivityRicercaPercorsiBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

public class RicercaPercorsiActivity extends AppCompatActivity {

    private TextView nomePercorso,
                     descrizionePercorso,
                     regionePercorso,
                     comunePercorso;
    private ImageView immaginePercorso;
    private FirebaseFirestore db;
    private FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        setContentView(R.layout.activity_ricerca_percorsi);
        nomePercorso = (TextView) findViewById(R.id.nomePercorso);
        descrizionePercorso = (TextView) findViewById(R.id.descrizionePercorso);
        regionePercorso = (TextView) findViewById(R.id.regionePercorso);
        comunePercorso = (TextView) findViewById(R.id.comunePercorso);
        immaginePercorso = (ImageView) findViewById(R.id.immaginePercorso);
        showPercorsi("Bari");

    }

    public void showPercorsi(String search){
        db.collection("percorso")
                .whereEqualTo("comune", "Bari")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //istanze del database ottenute con successo
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("DEBUG", document.getId() + " => " + document.getData());
                                for(Map.Entry<String, Object> entry : document.getData().entrySet()){
                                    if(entry.getKey().equals("nome")){
                                        nomePercorso.setText((String) entry.getValue());
                                    }else if(entry.getKey().equals("descrizione")){
                                        descrizionePercorso.setText((String) entry.getValue());
                                    }else if(entry.getKey().equals("regione")){
                                        regionePercorso.setText((String) entry.getValue());
                                    }else if(entry.getKey().equals("comune")){
                                        comunePercorso.setText((String) entry.getValue());
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
                            }
                        } else {
                            Log.w("DEBUG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}