package com.example.percorsoculturale;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

public class ListaBadge extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        setContentView(R.layout.lista_badge);

        ImageView cinquePunti = (ImageView) findViewById(R.id.imageView10);
        ImageView dieciPunti = (ImageView) findViewById(R.id.imageView15);
        ImageView venticinquePunti = (ImageView) findViewById(R.id.imageView16);
        ImageView venticinquePuntiGrigio= (ImageView) findViewById(R.id.imageView12);

        ImageView cinquePuntiGrigio = (ImageView) findViewById(R.id.imageView17);
        ImageView dieciPuntiGrigio = (ImageView) findViewById(R.id.imageView14);
        ImageView cinquantaPunti = (ImageView) findViewById(R.id.imageView11);
        ImageView cinquantaPuntiGrigio = (ImageView) findViewById(R.id.imageView13);

        TextView textRegistrazione = (TextView) findViewById(R.id.textView10);
        TextView textAttivita = (TextView) findViewById(R.id.textView11);
        TextView textPercorso = (TextView) findViewById(R.id.textView12);
        TextView textPercorsi = (TextView) findViewById(R.id.textView13);

        mostraTuttiBadge(cinquePunti, dieciPunti, venticinquePunti, cinquantaPunti, cinquePuntiGrigio, dieciPuntiGrigio, venticinquePuntiGrigio, cinquantaPuntiGrigio, textRegistrazione, textAttivita, textPercorso, textPercorsi);

    }
    public void mostraTuttiBadge(ImageView badge1, ImageView badge2, ImageView badge3, ImageView badge4, ImageView badge1Grigio, ImageView badge2Grigio, ImageView badge3Grigio, ImageView badge4Grigio, TextView textRegistrazione, TextView textAttivita, TextView textPercorso, TextView textPercorsi) {

        //prendo la mail dell'utente loggato

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String mailUtente = firebaseUser.getEmail();


        String percorsoCollezione = "utente";

        //verifico se l'utente ha sbloccato il badge da 5 punti
        DocumentReference docRef = db.collection(percorsoCollezione).document(mailUtente);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        //int ultimaVersion = document.get(Integer.class);
                        int puntiVeri = document.getLong("punti").intValue();

                        //
                        if (puntiVeri >= 50 ){
                            mostraBadge("5punti",badge1);
                            mostraBadge("10punti",badge2);
                            mostraBadge("25punti",badge3);
                            mostraBadge("50punti",badge4);
                        }
                        else {
                            mostraBadge("50puntigrigio",badge4);
                            //badge4Grigio.setVisibility(View.VISIBLE);
                            if (puntiVeri >= 25 ){
                                mostraBadge("5punti",badge1);
                                mostraBadge("10punti",badge2);
                                mostraBadge("25punti",badge3);
                            }
                            else {
                                mostraBadge("25puntigrigio",badge3);
                                //badge3Grigio.setVisibility(View.VISIBLE);
                                if (puntiVeri >= 10 ){
                                    mostraBadge("5punti",badge1);
                                    mostraBadge("10punti",badge2);
                                }
                                else {
                                    mostraBadge("10puntigrigio",badge2);
                                    badge2Grigio.setVisibility(View.VISIBLE);
                                    if (puntiVeri >= 5 ){
                                        mostraBadge("5punti",badge1);
                                    }
                                    else {
                                        mostraBadge("5puntigrigio",badge1);
                                        //badge1Grigio.setVisibility(View.VISIBLE);
                                    }
                                }
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

    public void mostraBadge(String search,ImageView badge){
        db.collection("badge")
                .document(search)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        Log.d("DEBUG", document.getId() + " => " + document.getData());
                        for(Map.Entry<String, Object> entry : document.getData().entrySet()){
                            if(entry.getKey().equals("immagine")){
                                StorageReference gsReference = storage.getReferenceFromUrl((String) entry.getValue());
                                final long ONE_MEGABYTE = 102 * 102;
                                gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        // image retrieved
                                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        badge.setImageBitmap(bmp);
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

        badge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i=new Intent(ListaBadge.this, InfoBadge.class);
                i.putExtra("nome", search);

                startActivity(i);
            }
        });

    }
}

