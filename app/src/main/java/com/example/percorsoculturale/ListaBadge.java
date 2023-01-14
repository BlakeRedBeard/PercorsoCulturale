package com.example.percorsoculturale;

import static android.content.ContentValues.TAG;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private Animation anim=null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.lista_badge_landscape);
        }else{
            setContentView(R.layout.lista_badge);
        }

        ImageView cinquePunti = (ImageView) findViewById(R.id.imageView10);
        ImageView dieciPunti = (ImageView) findViewById(R.id.imageView15);
        ImageView venticinquePunti = (ImageView) findViewById(R.id.imageView16);
        ImageView cinquantaPunti = (ImageView) findViewById(R.id.imageView11);

        TextView descrizioneB1 = (TextView) findViewById(R.id.textView10);
        TextView descrizioneB2 = (TextView) findViewById(R.id.textView11);
        TextView descrizioneB3 = (TextView) findViewById(R.id.textView12);
        TextView descrizioneB4 = (TextView) findViewById(R.id.textView13);

        anim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.animazione);

        cinquePunti.startAnimation(anim);
        dieciPunti.startAnimation(anim);
        venticinquePunti.startAnimation(anim);
        cinquantaPunti.startAnimation(anim);

        descrizioneB1.startAnimation(anim);
        descrizioneB2.startAnimation(anim);
        descrizioneB3.startAnimation(anim);
        descrizioneB4.startAnimation(anim);

        mostraTuttiBadge(cinquePunti, dieciPunti, venticinquePunti, cinquantaPunti, descrizioneB1, descrizioneB2, descrizioneB3, descrizioneB4);


        Button btnContinua = (Button)findViewById(R.id.continuaButton);
        btnContinua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListaBadge.this, RicercaPercorsiActivity.class));
            }

        });

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

    }
    public void mostraTuttiBadge(ImageView badge1, ImageView badge2, ImageView badge3, ImageView badge4, TextView descrizioneB1, TextView descrizioneB2, TextView descrizioneB3, TextView descrizioneB4) {

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


                        int puntiVeri=0;
                        if(document.getLong("punti")!=null) {
                            puntiVeri = document.getLong("punti").intValue();
                        }

                        if (puntiVeri >= 50 ){
                            mostraBadge("5punti",badge1,descrizioneB1);
                            mostraBadge("10punti",badge2,descrizioneB2);
                            mostraBadge("25punti",badge3,descrizioneB3);
                            mostraBadge("50punti",badge4,descrizioneB4);
                        }
                        else {
                            mostraBadge("50puntigrigio",badge4,descrizioneB4);
                            if (puntiVeri >= 25 ){
                                mostraBadge("5punti",badge1,descrizioneB1);
                                mostraBadge("10punti",badge2,descrizioneB2);
                                mostraBadge("25punti",badge3,descrizioneB3);
                            }
                            else {
                                mostraBadge("25puntigrigio",badge3,descrizioneB3);
                                if (puntiVeri >= 10 ){
                                    mostraBadge("5punti",badge1,descrizioneB1);
                                    mostraBadge("10punti",badge2,descrizioneB2);
                                }
                                else {
                                    mostraBadge("10puntigrigio",badge2,descrizioneB2);
                                    if (puntiVeri >= 5 ){
                                        mostraBadge("5punti",badge1,descrizioneB1);
                                    }
                                    else {
                                        mostraBadge("5puntigrigio",badge1,descrizioneB1);
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

    public void mostraBadge(String search,ImageView badge, TextView descrizione){
        db.collection("badge")
                .document(search)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        Log.d("DEBUG", document.getId() + " => " + document.getData());
                        for(Map.Entry<String, Object> entry : document.getData().entrySet()){
                            if(entry.getKey().equals("descrizione")){
                                descrizione.setText((String) entry.getValue());
                            }
                            else if(entry.getKey().equals("immagine")){
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

    }
}

